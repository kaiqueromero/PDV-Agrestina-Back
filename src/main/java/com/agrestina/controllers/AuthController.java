package com.agrestina.controllers;

import com.agrestina.domain.user.User;
import com.agrestina.dto.user.LoginRequestDTO;
import com.agrestina.dto.user.RegisterRequestDTO;
import com.agrestina.dto.user.ResponseDTO;
import com.agrestina.infra.security.TokenService;
import com.agrestina.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body){
        User user = this.repository.findByLogin(body.login()).orElseThrow(() -> new RuntimeException("User not found"));
        if(passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            log.info("Login efetuado com sucesso. Login {} ", body.login());
            return ResponseEntity.ok(new ResponseDTO(user.getName(), token, user.getUserRole().toString()));
        }

        log.info("Tentativa de login com falha. Login {} ", body.login());
        //return ResponseEntity.badRequest().build();
        return ResponseEntity.badRequest().body("Tentativa de login com falha");
    }


    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO body){
        Optional<User> user = this.repository.findByLogin(body.login());

        if(user.isEmpty()) {
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setLogin(body.login());
            newUser.setName(body.name());
            newUser.setUserRole(body.userRole());
            this.repository.save(newUser);

            String token = this.tokenService.generateToken(newUser);
            log.info("Novo login registrado com sucesso. Login {} ", newUser.getLogin());
            return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token, newUser.getUserRole().toString()));
        }

        log.info("Erro ao cadastrar novo login. Erro de validacao e/ou login ja registrado. Login {}", body.login());
        //return ResponseEntity.badRequest().build();
        return ResponseEntity.badRequest().body("Erro ao cadastrar novo login. Erro de validacao e/ou login ja registrado");
    }
}
