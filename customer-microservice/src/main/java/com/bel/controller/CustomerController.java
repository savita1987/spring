package com.bel.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bel.model.Customer;
import com.bel.model.Product;
import com.bel.repository.CustomerRepository;
import com.bel.service.CustomerService;
import com.bel.service.ProductConsumer;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;


// hit URL http://localhost:6002/customer to get a product via Customer service
// hit URL 
@RestController
@RequestMapping("/customer")
public class CustomerController {
	@Autowired
	private ProductConsumer consumer;
	
	@Autowired
	private CustomerService service;
	
	@Autowired
	private CustomerRepository repo;
	

	@GetMapping(value="/all")
	@HystrixCommand(fallbackMethod = "fallback")
	public ResponseEntity<List<Customer>> findProducts(){
		return new ResponseEntity<List<Customer>>(service.getCustomers(), HttpStatus.OK);
	}
	
	
	public ResponseEntity<List<Customer>> fallback(){
		List l = new ArrayList<>();
		l.add("CIRCUIT BREAKER ENABLED!!! No Response From Product Service at this moment. Product Service is down!!!"
				+ " Service will be back shortly - ");
		return ResponseEntity.ok().body(l);
	}
	
	@GetMapping(value = "/get/{id}")
	public ResponseEntity<Customer> getProduct(@PathVariable("id") Long pId){
		return new ResponseEntity<>(service.getCustomer(pId), HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<List<Customer>> addNewCustomer(@RequestBody Customer customer){
		System.out.println(" Hit addNewCustomer"+customer.toString());
		Product p = customer.getProduct();
		customer.setProduct(p);
		p.setCustomer(customer);
		consumer.addNewProduct(p);				//// Calling Parent
	return new ResponseEntity<>(service.saveCustomer(customer), HttpStatus.CREATED);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<Customer> updateCustomer(@PathVariable("id") Long pId,@RequestBody Customer customer){
		Customer c = repo.findById(pId).get();
		//Fetching Product ID to update in product microservice
		Product pro = c.getProduct();
		
		c.setCustName(customer.getCustName());
		
		pro.setProductName(customer.getProduct().getProductName());
		pro.setPrice(customer.getProduct().getPrice());
		
		c.setProduct(pro);
		pro.setCustomer(c);
		

		Product uProduct = consumer.updateProduct(pro.getProductId(), pro);			// Calling Parent
		uProduct.setCustomer(c);
		Customer updatedCustomer = service.updateCustomer(pId, c);
		return ResponseEntity.ok().body(updatedCustomer);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteCustomer(@PathVariable("id") Long pId){
		Product p = service.getCustomer(pId).getProduct();
		service.deleteCustomer(pId);
		consumer.deleteProduct(p.getProductId());				//Calling Parent
		return ResponseEntity.ok().body("Customer Deleted.");
	}
	
	@GetMapping(value = "/get/product/{id}")
	@HystrixCommand(fallbackMethod = "fallback1")
	public Product getProductsOfCust(@PathVariable("id") Long pId){
		Customer c = repo.findById(pId).get();
		Long id = c.getProduct().getProductId();
		return consumer.getProduct(id);
	}
	
	public Product fallback1(){
		List l = new ArrayList<>();
		Product p = new Product();
		System.out.println("#####################CIRCUIT BREAKER ENABLED!!! No Response From Product Service at this moment. Product Service is down!!!"
				+ " Service will be back shortly - ");
		return p;
	}
	
	@HystrixCommand(fallbackMethod = "callServiceAndGetData_Fallback")
	@GetMapping
	public ResponseEntity<String> getProducts(){
		List<Product> p = consumer.getProducts();
		return ResponseEntity.ok().body("Get Products");
	}
	
	public ResponseEntity<String> callServiceAndGetData_Fallback() {
		System.out.println("Product Service is down!!! fallback route enabled...");
		return ResponseEntity.ok().body("CIRCUIT BREAKER ENABLED!!! No Response From Product Service at this moment. Product Service is down!!! " +
		" Service will be back shortly - ");
		}
	
}
