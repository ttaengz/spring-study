package com.myspring.comment.service;

import com.myspring.comment.dao.UserDAO;
import com.myspring.comment.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createUser(UserModel user) {
        String encodedPw = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPw);

        userDAO.insertUser(user);
    }

    public UserModel getUser(String userId) {
        return userDAO.selectUser(userId);
    }
}
