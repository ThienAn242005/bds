package com.homeverse.identity.mapper;

import com.homeverse.identity.dto.request.UserRegisterDTO;
import com.homeverse.identity.dto.response.UserResponseDTO;
import com.homeverse.identity.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Autowired
    private ModelMapper modelMapper;

    public User toEntity(UserRegisterDTO dto) {
        User user = modelMapper.map(dto, User.class);
        // Map Role từ String sang Enum
        user.setRole(User.Role.valueOf(dto.getRole().toUpperCase()));
        return user;
    }

    public UserResponseDTO toResponse(User entity) {
        return modelMapper.map(entity, UserResponseDTO.class);
    }
}