package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDTO;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<User> getAllUsers() {
        return userService.showUsers ();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        return ResponseEntity.ok (userService.getUserById (id));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors ()) {
            Map<String, String> errors = new HashMap<> ();
            bindingResult.getFieldErrors ().forEach (error ->
                    errors.put (error.getField (), error.getDefaultMessage ()));
            return ResponseEntity.badRequest ().body (errors);
        }

        userService.addUser (userDTO);
        return ResponseEntity.ok (userDTO);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> editUserInfo(@PathVariable int id, @RequestBody @Valid UserDTO updatedUser, BindingResult bindingResult) {
        if (bindingResult.hasErrors ()) {
            Map<String, String> errors = new HashMap<> ();
            bindingResult.getFieldErrors ().forEach (error ->
                    errors.put (error.getField (), error.getDefaultMessage ()));
            return ResponseEntity.badRequest ().body (errors);
        }
        userService.updateUser (id, updatedUser);
        return ResponseEntity.ok (updatedUser);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUser (userService.getUserById (id));
        return ResponseEntity.ok ().build ();
    }

}
