package ru.kata.spring.boot_security.demo.security;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.dao.UserRepo;
import ru.kata.spring.boot_security.demo.model.User;

@Service
public class SecurityUserDetailService implements UserDetailsService {
    private final UserRepo userRepo;

    @Autowired
    public SecurityUserDetailService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername (username);

        if (user == null) {
            System.out.println ("User not found: " + username);
            throw new UsernameNotFoundException ("User not found: " + username);
        }
        return user;
    }
}
