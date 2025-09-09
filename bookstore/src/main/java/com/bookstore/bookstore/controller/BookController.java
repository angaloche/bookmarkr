package com.bookstore.bookstore.controller;

import java.util.List;

import com.bookstore.bookstore.entity.User;
import com.bookstore.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.bookstore.bookstore.entity.Book;
import com.bookstore.bookstore.entity.MyBookList;
import com.bookstore.bookstore.service.BookService;
import com.bookstore.bookstore.service.MyBookService;

@Controller
public class BookController {
    @Autowired
    BookService bookService;

    @Autowired
    MyBookService mybookService;

    @Autowired
    UserRepository userRepository;


    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/book_register")
    public String bookRegister() {
        return "bookregister";
    }

    @GetMapping("/available_books")
    public ModelAndView availableBook(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        List<Book> listOfBook = bookService.getAllBook(user);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("booklist");
        mav.addObject("book", listOfBook);
        return mav;
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername());
    }

    @PostMapping("/save")
    public String addBook(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute Book b) {
        b.setUser(getUser(userDetails));
        bookService.save(b);
        return "redirect:/available_books";
    }

    @GetMapping("/my_books")
    public ModelAndView getMyBooks(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getUser(userDetails);
        return new ModelAndView("mybooks", "mybook", mybookService.getAllMyBooks(user));
    }

    @RequestMapping("/mylist/{id}")
    public String getMyList(@PathVariable("id") int id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        MyBookList myBookList = new MyBookList();
        Book book = bookService.getBookById(id);
        myBookList.setUser(user);
        myBookList.setAuthor(book.getAuthor());
        myBookList.setName(book.getName());
        myBookList.setPrice(book.getPrice());

        mybookService.saveMyBooks(myBookList);
        return "redirect:/my_books";
    }

    @RequestMapping("/deletebook/{id}")
    public String deleteBookById(@PathVariable("id") int id, @AuthenticationPrincipal UserDetails userDetails
    ) {
        bookService.deleteBookById(id);
        return "redirect:/available_books";
    }

    @RequestMapping("/deleteMyList/{id}")
    public String deleteMyList(@PathVariable("id") int id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        mybookService.deleteById(id, user);
        return "redirect:/my_books";
    }

    @RequestMapping("/editBook/{id}")
    public String editBook(@PathVariable("id") int id, Model model) {
        Book b = bookService.getBookById(id);
        model.addAttribute("book", b);
        return "editbook";
    }
}
