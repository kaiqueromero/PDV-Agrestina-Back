package com.agrestina.dto.user;

import com.agrestina.domain.user.UserRole;

public record RegisterRequestDTO (String name, String login, String password, UserRole userRole, boolean activeUser) {
}
