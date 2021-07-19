package com.aragh.resource.controller;

import com.aragh.resource.dto.UserDto;
import com.aragh.resource.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public Integer saveUser(@RequestBody UserDto userDto) {
        return userService.saveDto(userDto).getId();
    }

}
