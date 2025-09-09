package com.bookstore.bookstore.service;

import java.util.List;

import com.bookstore.bookstore.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.bookstore.entity.Book;
import com.bookstore.bookstore.repository.BookRepository;

@Service
public class BookService {
    @Autowired
    BookRepository bookRepository;

    public void save(final Book b) {
        bookRepository.save(b);
    }

    public Book getBookById(final int id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
    }

    public void deleteBookById(final int id) {
        bookRepository.deleteById(id);
    }

    public List<Book> getAllBookForUser(final User user) {
        return bookRepository.findByUser(user);
    }
}
