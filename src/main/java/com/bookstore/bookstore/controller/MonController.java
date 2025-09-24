package com.bookstore.bookstore.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MonController {

    @GetMapping("/profile")
    public String profile(final @AuthenticationPrincipal UserDetails userDetails, final Model model) {
        model.addAttribute("username", userDetails.getUsername());
        return "profile";
    }
}