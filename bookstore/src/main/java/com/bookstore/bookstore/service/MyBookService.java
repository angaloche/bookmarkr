package com.bookstore.bookstore.service;

import java.util.List;
import com.bookstore.bookstore.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.bookstore.entity.MyBookList;
import com.bookstore.bookstore.repository.MyBookRepository;

@Service
public class MyBookService {
	@Autowired
	MyBookRepository myBookRepo;

	public void saveMyBooks(MyBookList book) {
		myBookRepo.save(book);
	}

	public List<MyBookList> getAllBook() {
		return myBookRepo.findAll();
	}

	public List<MyBookList> getAllMyBooks(User user) {
		return myBookRepo.findByUser(user);
	}

	public void deleteById(int id, User user) {
		MyBookList book = myBookRepo.findById(id).orElse(null);
		if (book != null && book.getUser().getId() == user.getId()) {
			myBookRepo.deleteById(id);
		}
	}

}