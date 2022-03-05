package com.bel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bel.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

}
