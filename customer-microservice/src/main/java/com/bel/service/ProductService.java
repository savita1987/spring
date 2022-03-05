package com.bel.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bel.model.Product;
import com.bel.repository.ProductRepository;

@Service
@Transactional
public class ProductService {
	
	@Autowired
	private ProductRepository repo;
	
	public List<Product> getProducts(){
		return repo.findAll();
	}
	
	public List<Product> saveProduct(Product p){
		repo.save(p);
		return repo.findAll();
	}
	
	public Product get(Long id) {
		return repo.findById(id).get();
	}
	
	public Product updateProduct(Long id,Product p) {
		return repo.save(p);
	}

	public void delete(Long id) {
		repo.deleteById(id);
	}

}
