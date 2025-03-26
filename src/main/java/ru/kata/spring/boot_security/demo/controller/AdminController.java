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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
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
        System.out.println (userDTO);
        if (bindingResult.hasErrors ()) {
            Map<String, String> errors = new HashMap<> ();
            bindingResult.getFieldErrors ().forEach (error ->
                    errors.put (error.getField (), error.getDefaultMessage ()));
            return ResponseEntity.badRequest ().body (errors);
        }

        User user = new User ();
        user.setName (userDTO.getName ());
        user.setSurname (userDTO.getSurname ());
        user.setAge (userDTO.getAge ());
        user.setUsername (userDTO.getUsername ());
        user.setPassword (userDTO.getPassword ());

        Set<Role> roles = userDTO.getRoles ().stream ()
                .map (role -> roleService.findRoleByName (role.getName ()))
                .filter (Objects::nonNull)
                .collect (Collectors.toSet ());

        user.setRoles (roles);

        if (userService.findUserByUsername (userDTO.getUsername ()) != null) {
            Map<String, String> errors = new HashMap<> ();
            errors.put ("username", "Логин уже занят");
            return ResponseEntity.badRequest ().body (errors);
        }

        userService.addUser (user);
        return ResponseEntity.ok (user);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> editUserInfo(@PathVariable int id, @RequestBody @Valid UserDTO updatedUser, BindingResult bindingResult) {
        System.out.println (updatedUser);
        if (bindingResult.hasErrors ()) {
            // Собираем все ошибки в Map
            Map<String, String> errors = new HashMap<> ();
            bindingResult.getFieldErrors ().forEach (error ->
                    errors.put (error.getField (), error.getDefaultMessage ()));
            return ResponseEntity.badRequest ().body (errors);
        }
        User existingUser = userService.getUserById (id);

        if (!(updatedUser.getUsername ().equals (existingUser.getUsername ()))) {
            if (userService.findUserByUsername (updatedUser.getUsername ()) != null) {
                Map<String, String> errors = new HashMap<> ();
                errors.put ("username", "Это имя пользователя уже занято");
                return ResponseEntity.badRequest ().body (errors);
            }
        }

        existingUser.setName (updatedUser.getName ());
        existingUser.setSurname (updatedUser.getSurname ());
        existingUser.setAge (updatedUser.getAge ());
        existingUser.setUsername (updatedUser.getUsername ());

        // Если пароль не пустой, обновляем его
        if (updatedUser.getPassword () != null && !updatedUser.getPassword ().isEmpty ()) {
            existingUser.setPassword (updatedUser.getPassword ());
        }

        // Обновляем роли (загружаем их из базы перед присвоением)
        Set<Role> updatedRoles = updatedUser.getRoles ().stream ()
                .map (role -> roleService.findRoleByName (role.getName ())) // Загружаем роль из БД
                .filter (Objects::nonNull)
                .collect (Collectors.toSet ());

        existingUser.setRoles (updatedRoles);
        userService.updateUser (existingUser);
        return ResponseEntity.ok (existingUser);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUser (userService.getUserById (id));
        return ResponseEntity.ok ().build ();
    }
}
