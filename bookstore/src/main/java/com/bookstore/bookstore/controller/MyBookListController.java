package com.bookstore.bookstore.controller;

import com.bookstore.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.bookstore.bookstore.service.MyBookService;

@Controller
public class MyBookListController {
    @Autowired
    MyBookService myBookService;
    @Autowired
    UserRepository userRepository;

    /*@RequestMapping("/deleteMyList/{id}")
    public String deleteMyListById(@PathVariable("id") int id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        myBookService.deleteById(id, user);
        return "redirect:/my_books";
    }*/
}
