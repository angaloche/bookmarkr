package com.bookstore.bookstore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.bookstore.entity.Book;
import com.bookstore.bookstore.repository.BookRepository;

@Service
public class BookService {
	@Autowired
	BookRepository bookRepo;

	public void save(Book b) {
		bookRepo.save(b);
	}

	public List<Book> getAllBook() {
		return bookRepo.findAll();
	}

	public Book getBookById(int id) {
		return bookRepo.findById(id).get();
	}

	public void deleteBookById(int id) {
		bookRepo.deleteById(id);
	}
}
