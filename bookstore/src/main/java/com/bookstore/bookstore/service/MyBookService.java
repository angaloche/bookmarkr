package com.bookstore.bookstore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.bookstore.entity.MyBookList;
import com.bookstore.bookstore.repository.MyBookRepository;

@Service
public class MyBookService {
	@Autowired
	MyBookRepository myBookRepo;

	public void saveMyBooks(MyBookList b) {
		myBookRepo.save(b);
	}

	public List<MyBookList> getAllBook() {
		return myBookRepo.findAll();
	}

	public void deleteById(int id) {
		myBookRepo.deleteById(id);
	}
}