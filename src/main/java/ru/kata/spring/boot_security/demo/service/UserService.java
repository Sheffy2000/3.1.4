package ru.kata.spring.boot_security.demo.service;


import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {
    public List<User> showUsers();

    public void addUser(User user);

    public User getUserById(int id);

    public void updateUser(User user);

    public User findUserByUsername(String username);

    public void deleteUser(User user);
}
