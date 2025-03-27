package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDTO;

import java.util.List;

public interface UserService {
    public List<User> showUsers();

    public void addUser(UserDTO userDTO);

    public User getUserById(int id);

    public void updateUser(int id, UserDTO userDTO);

    public User findUserByUsername(String username);

    public void deleteUser(User user);
}
