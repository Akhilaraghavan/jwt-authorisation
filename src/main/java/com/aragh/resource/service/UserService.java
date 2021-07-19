package com.aragh.resource.service;

import com.aragh.resource.dto.UserDto;
import com.aragh.resource.model.User;
import com.aragh.resource.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ModelMapper modelMapper;
    private UserRepository userRepository;

    @Autowired
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder, ModelMapper modelMapper, UserRepository userRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public User saveDto(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        return userRepository.save(user);
    }
}
