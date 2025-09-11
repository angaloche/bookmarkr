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
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String author;
    private double price;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Book(final int id, final String name, final String author, double price) {
        super();
        this.id = id;
        this.name = name;
        this.author = author;
        this.price = price;
    }

    public Book() {
        super();
    }
}
