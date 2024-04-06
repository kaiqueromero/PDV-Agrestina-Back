package com.agrestina.repositories;

import com.agrestina.domain.user.User;
import com.agrestina.domain.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByLogin(String login);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :newPassword, u.userRole = :newUserRole, u.activeUser = :newActiveUser WHERE u.login = :login")
    void updateByLogin(String login, UserRole newUserRole, boolean newActiveUser, String newPassword);

}
