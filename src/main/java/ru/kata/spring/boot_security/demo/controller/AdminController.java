package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.dao.RoleRepo;
import ru.kata.spring.boot_security.demo.dao.UserRepo;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;

@Controller
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(value = "/admin")
    public String printWelcomeForAdmin(Model model) {
        List<User> allUsers = userService.showUsers ();
        Authentication auth = SecurityContextHolder.getContext ().getAuthentication ();
        String username = auth.getName ();
        User activeUser = userService.findUserByUsername (username);
        model.addAttribute ("users", allUsers);
        model.addAttribute ("roles", roleService.findAllRoles());
        model.addAttribute ("user", new User ());
        model.addAttribute ("activeUser", activeUser);
        return "AdminPage";
    }

    @GetMapping(value = "/addUser")
    public String showUserInfo(Model model) {
        model.addAttribute ("user", new User ());
        model.addAttribute ("roles", roleService.findAllRoles());
        return "userFormForAdd";
    }

    @PostMapping(value = "/addUser")
    public String addUser(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors ()) {
            model.addAttribute ("roles", roleService.findAllRoles());
            model.addAttribute ("user", user);
            return "AdminPage";
        }
        userService.addUser (user);
        return "redirect:/admin";
    }

    @PostMapping(value = "/editUser")
    public String updateUser(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors ()) {
            List<User> allUsers = userService.showUsers ();
            Authentication auth = SecurityContextHolder.getContext ().getAuthentication ();
            String username = auth.getName ();
            User activeUser = userService.findUserByUsername (username);
            model.addAttribute ("users", allUsers);
            model.addAttribute ("roles", roleService.findAllRoles());
            model.addAttribute ("user", user);
            model.addAttribute ("openEditModal", true);
            model.addAttribute ("activeUser", activeUser);
            return "AdminPage";
        }
        userService.updateUser (user);
        return "redirect:/admin";
    }

    @GetMapping("/editUser")
    public String updateInfo(@RequestParam("id") int id, Model model) {
        User user = userService.getUserById (id);
        List<User> allUsers = userService.showUsers ();
        Authentication auth = SecurityContextHolder.getContext ().getAuthentication ();
        String username = auth.getName ();
        User activeUser = userService.findUserByUsername (username);
        model.addAttribute ("user", user);
        model.addAttribute ("roles", roleService.findAllRoles());
        model.addAttribute ("openEditModal", true);
        model.addAttribute ("activeUser", activeUser);
        model.addAttribute ("users", allUsers);

        return "adminPage";
    }

    @GetMapping("deleteUser")
    public String deleteUser(@RequestParam("id") int id, Model model) {
        User user = userService.getUserById (id);
        List<User> allUsers = userService.showUsers ();
        Authentication auth = SecurityContextHolder.getContext ().getAuthentication ();
        String username = auth.getName ();
        User activeUser = userService.findUserByUsername (username);
        model.addAttribute ("user", user);
        model.addAttribute ("roles", roleService.findAllRoles());
        model.addAttribute ("openDeleteModal", true);
        model.addAttribute ("activeUser", activeUser);
        model.addAttribute ("users", allUsers);

        return "adminPage";
    }

    @PostMapping("deleteUser")
    public String deleteUser(@ModelAttribute User user) {
        userService.deleteUser (user);
        return "redirect:/admin";
    }
}
