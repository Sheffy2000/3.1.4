package ru.kata.spring.boot_security.demo.service;


import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserRepo;
import ru.kata.spring.boot_security.demo.exeption.UserAlreadyExistsException;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDTO;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;

    private final UserRepo userRepo;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, RoleService roleService, ModelMapper modelMapper) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<User> showUsers() {
        return userRepo.findAll ();
    }

    @Transactional
    @Override
    public void addUser(UserDTO userDTO) {
        User user = modelMapper.map (userDTO, User.class);

        Set<Role> roles = userDTO.getRoles ().stream ()
                .map (role -> roleService.findRoleByName (role.getName ()))
                .filter (Objects::nonNull)
                .collect (Collectors.toSet ());

        user.setRoles (roles);
        user.setPassword (passwordEncoder.encode (user.getPassword ()));

        if (this.findUserByUsername (user.getUsername ()) != null) {
            throw new UserAlreadyExistsException ("Такой пользователь уже существует");
        }

        userRepo.save (user);
    }

    @Transactional
    @Override
    public void updateUser(int id, UserDTO updatedUser) {
        User existingUser = this.getUserById (updatedUser.getId ());

        if (!existingUser.getUsername ().equals (updatedUser.getUsername ()) &&
                this.findUserByUsername (updatedUser.getUsername ()) != null) {
            throw new UserAlreadyExistsException ("Это имя пользователя уже занято");
        }

        if (updatedUser.getPassword () == null) {
            updatedUser.setPassword (existingUser.getPassword ());
        }

        modelMapper.map (updatedUser, existingUser);

        Set<Role> updatedRoles = updatedUser.getRoles ().stream ()
                .map (role -> roleService.findRoleByName (role.getName ()))
                .filter (Objects::nonNull)
                .collect (Collectors.toSet ());

        existingUser.setRoles (updatedRoles);

        existingUser.setPassword (passwordEncoder.encode (existingUser.getPassword ()));
        userRepo.save (existingUser);
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
