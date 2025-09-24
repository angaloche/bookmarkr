package com.bookstore.bookstore.service;

import java.util.List;
import java.util.Objects;

import com.bookstore.bookstore.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.bookstore.entity.MyBookList;
import com.bookstore.bookstore.repository.MyBookRepository;

@Service
public class MyBookService {
    @Autowired
    MyBookRepository myBookRepository;

    public void saveMyBooks(final MyBookList book) {
        myBookRepository.save(book);
    }

    public List<MyBookList> getAllBook() {
        return myBookRepository.findAll();
    }

    public List<MyBookList> getAllBooksForUser(final User user) {
        return myBookRepository.findByUser(user);
    }

    public void deleteById(final int id, final User user) {
        MyBookList book = myBookRepository.findById(id).orElse(null);
        if (book != null && Objects.equals(book.getUser().getId(), user.getId())) {
            myBookRepository.deleteById(id);
        }
    }

}