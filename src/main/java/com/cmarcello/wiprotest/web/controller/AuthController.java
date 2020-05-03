package com.cmarcello.wiprotest.web.controller;

import com.cmarcello.wiprotest.web.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    @GetMapping("/validateLogin")
    public UserDto validateLogin() {
        return new UserDto("User successfully authenticated");
    }
}
