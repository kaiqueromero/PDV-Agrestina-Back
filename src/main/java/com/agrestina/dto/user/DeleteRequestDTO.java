package com.agrestina.dto.user;

import com.agrestina.domain.user.UserRole;

public record DeleteRequestDTO(String loginToDelete, UserRole userLoggedRole, String userLogged) {
}
