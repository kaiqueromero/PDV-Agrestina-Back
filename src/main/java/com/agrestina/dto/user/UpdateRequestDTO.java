package com.agrestina.dto.user;

import com.agrestina.domain.user.UserRole;

public record UpdateRequestDTO(String login, String password, UserRole userRole, boolean activeUser, String userLogged, UserRole userLoggedRole) {
}
