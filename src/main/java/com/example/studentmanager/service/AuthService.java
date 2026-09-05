package com.example.studentmanager.service;

import com.example.studentmanager.dao.UserDao;
import com.example.studentmanager.model.User;
import com.example.studentmanager.util.PasswordHasher;

import java.util.Optional;

public class AuthService {
    private final UserDao userDao;

    public AuthService() {
        this.userDao = new UserDao();
    }

    public boolean register(String username, String password) {
        if (userDao.findByUsername(username).isPresent()) {
            return false; // User already exists
        }
        String hash = PasswordHasher.hashPassword(password);
        return userDao.insertUser(username, hash);
    }

    public User login(String username, String password) throws Exception {
        Optional<User> optionalUser = userDao.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new Exception("Unknown user.");
        }
        User user = optionalUser.get();
        if (!PasswordHasher.checkPassword(password, user.passwordHash())) {
            throw new Exception("Wrong password.");
        }
        return user;
    }
}
