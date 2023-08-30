package com.myspring.comment.controller;

import com.myspring.comment.model.UserModel;
import com.myspring.comment.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserContoller {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/users/join")
    public String getJoinForm(Model model) {
        model.addAttribute("user", new UserModel());
        return "join_form";
    }

    @PostMapping(value = "/users")
    public String postUser(@Valid UserModel user, Model model) {

        try {
            userService.createUser(user);
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("user", user);
            model.addAttribute("errMessage", "이미 사용중인 사용자 ID입니다.");
            return "join_form";
        }

        return "redirect:/";
    }

    @GetMapping("/users/login")
    public String login() {
        return "login_form";
    }
}
