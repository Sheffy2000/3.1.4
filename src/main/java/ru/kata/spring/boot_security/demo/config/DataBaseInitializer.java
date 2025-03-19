package ru.kata.spring.boot_security.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.RoleRepo;
import ru.kata.spring.boot_security.demo.dao.UserRepo;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataBaseInitializer {

    private final RoleRepo roleRepo;

    private final UserRepo userRepo;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataBaseInitializer(RoleRepo roleRepo, UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct  // Этот метод будет вызван после создания компонента
    public void init() {
        // Создаем роли, если они еще не существуют
        createRoleIfNotExists ("USER");
        createRoleIfNotExists ("ADMIN");

        // Создаем пользователей
        createUserIfNotExists ("user", "user", "USER");
        createUserIfNotExists ("admin", "admin", "USER", "ADMIN");
    }

    // Реализованный метод для создания роли, если она не существует
    private void createRoleIfNotExists(String roleName) {
        if (roleRepo.findByName (roleName) == null) {
            roleRepo.save (new Role (roleName));  // Создаем и сохраняем роль
        }
    }

    private void createUserIfNotExists(String username, String password, String... roleNames) {
        if (userRepo.findByUsername (username) == null) {
            User user = new User ();

            user.setUsername (username);
            user.setPassword (passwordEncoder.encode (password));
            // Заполняем обязательные поля, требуемые валидацией
            user.setName ("Имя_" + username);      // Заполняем поле "name"
            user.setSurname ("Фамилия_" + username); // Заполняем поле "surname"
            user.setAge (25);
            Set<Role> roles = new HashSet<> ();
            for (String roleName : roleNames) {
                Role role = roleRepo.findByName (roleName);
                if (role != null) {
                    roles.add (role);
                }
            }
            user.setRoles (roles);
            userRepo.save (user);
        }
    }


}
