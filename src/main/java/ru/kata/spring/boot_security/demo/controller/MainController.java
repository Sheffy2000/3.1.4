package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
public class MainController {

    private final UserService userService;

    @Autowired
    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String adminPanel(Model model) {
        Authentication auth = SecurityContextHolder.getContext ().getAuthentication ();
        String username = auth.getName ();
        User activeUser = userService.findUserByUsername (username);
        model.addAttribute ("activeUser", activeUser);
        return "adminPage"; // Возвращает HTML-шаблон (admin.html)
    }

}