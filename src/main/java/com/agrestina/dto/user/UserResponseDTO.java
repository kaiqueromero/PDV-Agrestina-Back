package com.agrestina.dto.user;

import com.agrestina.domain.client.Client;
import com.agrestina.domain.user.User;
import com.agrestina.domain.user.UserRole;

public record UserResponseDTO(String id, String name, String login, UserRole userRole, boolean activeUser) {
    public UserResponseDTO(User user){
        this(user.getId(), user.getName(), user.getLogin(), user.getUserRole(), user.isActiveUser());
    }
}
