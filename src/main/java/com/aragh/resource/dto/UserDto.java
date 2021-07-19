package com.aragh.resource.dto;

import com.aragh.resource.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserDto {

    private String username;
    private String email;
    private String password;
    private List<Role> roles;

}
