package com.bookstore.bookstore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class MyBookList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	private String author;
	private double price;
	private String notes;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public MyBookList(final int id, final String name, final String author, final double price,final String notes) {
		super();
		this.id = id;
		this.name = name;
		this.author = author;
		this.price = price;
		this.notes = notes;
	}

	public MyBookList() {
		super();
		// TODO Auto-generated constructor stub
	}

}