package com.bookstore.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
	MyBookService myBookListService;

	@GetMapping("/")
	public String home() {
		return "home";
	}

	@GetMapping("/book_register")
	public String bookRegister() {
		return "bookregister";
	}

	@GetMapping("/available_books")
	public ModelAndView availableBook() {
		List<Book> listOfBook = bookService.getAllBook();
		ModelAndView mav = new ModelAndView();
		mav.setViewName("booklist");
		mav.addObject("book", listOfBook);
		return mav;
		// or
		// return new ModelAndView("booklist", "book", listOfBook);
	}

	@PostMapping("/save")
	public String addBook(@ModelAttribute Book b) {
		bookService.save(b);
		return "redirect:/available_books";
	}

	@GetMapping("/my_books")
	public ModelAndView getMyBook() {
		List<MyBookList> listOfBook = myBookListService.getAllBook();
		return new ModelAndView("mybooks", "mybook", listOfBook);
	}

	@RequestMapping("/mylist/{id}")
	public String getMyList(@PathVariable("id") int id) {
		Book b = bookService.getBookById(id);
		MyBookList ml = new MyBookList(b.getId(), b.getName(), b.getAuthor(), b.getPrice());
		myBookListService.saveMyBooks(ml);
		return "redirect:/my_books";
	}

	@RequestMapping("/deletebook/{id}")
	public String deleteBookById(@PathVariable("id") int id) {
		bookService.deleteBookById(id);
		return "redirect:/available_books";
	}

	@RequestMapping("/editBook/{id}")
	public String editBook(@PathVariable("id") int id, Model model) {
		Book b = bookService.getBookById(id);
		model.addAttribute("book", b);
		return "editbook";
	}
}
