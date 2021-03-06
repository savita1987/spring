package com.bel.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data	//For getters and setters
@NoArgsConstructor

public class Customer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long custId;
	private String custName;
	
	@OneToOne(cascade = {CascadeType.ALL, CascadeType.MERGE},mappedBy = "customer")
	private Product product;

}
