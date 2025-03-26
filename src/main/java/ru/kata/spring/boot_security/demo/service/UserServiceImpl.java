package ru.kata.spring.boot_security.demo.service;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserRepo;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> showUsers() {
        return userRepo.findAll ();
    }

    @Transactional
    @Override
    public void addUser(User user) {
        user.setPassword (passwordEncoder.encode (user.getPassword ()));
        userRepo.save (user);
    }

    @Transactional
    @Override
    public void updateUser(User user) {
        user.setPassword (passwordEncoder.encode (user.getPassword ()));
        userRepo.save (user);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepo.findByUsername (username);
    }

    @Override
    public User getUserById(int id) {
        return userRepo.findById (id).orElseThrow (() -> new EntityNotFoundException ("Такого пользователя нет"));
    }

    @Transactional
    @Override
    public void deleteUser(User user) {
        if (!userRepo.existsById (user.getId ())) {
            throw new EntityNotFoundException ("Такого пользователя нет");
        }
        userRepo.delete (user);
    }
}
