package com.bookstore.bookstore.repository;

import com.bookstore.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.bookstore.entity.MyBookList;

import java.util.List;

@Repository
public interface MyBookRepository extends JpaRepository<MyBookList, Integer> {
    List<MyBookList> findByUser(User user);

}
