package com.agrestina.controllers;

import com.agrestina.domain.user.User;
import com.agrestina.domain.user.UserRole;
import com.agrestina.dto.user.*;
import com.agrestina.infra.security.TokenService;
import com.agrestina.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body){

        try {
            User user = this.repository.findByLogin(body.login())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            if(!user.isActiveUser()){
                log.info("Usuario inativo. Usuario = {} ", body.login());
                return ResponseEntity.badRequest().body("Usuario inativo");
            }
            if(passwordEncoder.matches(body.password(), user.getPassword())) {
                String token = this.tokenService.generateToken(user);
                log.info("Login efetuado com sucesso. Login = {} ", body.login());
                return ResponseEntity.ok(new LoginResponseDTO(user.getName(), token, user.getUserRole().toString()));
            }

            log.info("Usuario e/ou senha invalida. Login = {} ", body.login());
            //return ResponseEntity.badRequest().build();
            return ResponseEntity.badRequest().body("Usuario e/ou senha invalida");

        } catch (RuntimeException e) {
            log.info("Login nao encontrado. Login = {}", body.login());
            return ResponseEntity
                    .badRequest()
                    .body("Login nao encontrado");
        }
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
            newUser.setActiveUser(body.activeUser());
            this.repository.save(newUser);

            String token = this.tokenService.generateToken(newUser);
            log.info("Novo login registrado com sucesso. Login {} ", newUser.getLogin());
            return ResponseEntity.ok(new LoginResponseDTO(newUser.getName(), token, newUser.getUserRole().toString()));
        }

        log.info("Erro ao cadastrar novo login. Erro de validacao e/ou login ja registrado. Login {}", body.login());
        //return ResponseEntity.badRequest().build();
        return ResponseEntity.badRequest().body("Erro ao cadastrar novo login. Erro de validacao e/ou login ja registrado");
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteUser(@RequestBody DeleteRequestDTO body) {

        //String login = tokenService.validateToken(body.token()); //verificar uso no futuro, obtem o login atraves do token

        try {
            User user = this.repository.findByLogin(body.loginToDelete())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            if (body.userLoggedRole().equals(UserRole.ADMIN)) {
            //if (user.getUserRole().equals(UserRole.ADMIN)) {
                this.repository.delete(user);
                log.info("Usuário deletado com sucesso. Login do usuário deletado = {}. Usuário que efetuou a exclusão = {}", body.loginToDelete(), body.userLogged());
                return ResponseEntity.ok("Usuário deletado com sucesso");
            } else {
                log.info("Usuario sem permissao para excluir. Tentativa no login = {}. Usuario logado = {}", body.loginToDelete(), body.userLogged());
                return ResponseEntity.badRequest().body("Usuário sem permissão para excluir");
            }
        } catch (IllegalArgumentException e) {
            log.info("O login informado não foi encontrado para ser deletado. Tentativa no login = {}. Usuario logado = {}", body.loginToDelete(), body.userLogged());
            return ResponseEntity.badRequest().body("O Login informado não foi encontrado para ser deletado");
        }
    }

    @PutMapping("/update")
    public ResponseEntity updateUser(@RequestBody UpdateRequestDTO body){

        try {
            User user = this.repository.findByLogin(body.login())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            if (body.userLoggedRole().equals(UserRole.ADMIN)) {

                User updateUser = new User();
                //updateUser.setName(body.name()); //ALTERAR O NOME PODE SER UM PROBLEMA
                //updateUser.setLogin(body.login()); //ALTERAR O LOGIN PODE SER UM PROBLEMA!!
                updateUser.setPassword(passwordEncoder.encode(body.password()));
                updateUser.setUserRole(body.userRole());
                updateUser.setActiveUser(body.activeUser());

                this.repository.updateByLogin(body.login(), body.userRole(), body.activeUser(), passwordEncoder.encode(body.password()));

                log.info("Usuário alterado com sucesso. Login do usuário alterado = {}. Usuário que efetuou a alteracao = {}", body.login(), body.userLogged());
                return ResponseEntity.ok("Usuário alterado com sucesso");
            } else {
                log.info("Usuario sem permissao para alterar. Tentativa no login = {}. Usuario logado = {}", body.login(), body.userLogged());
                return ResponseEntity.badRequest().body("Usuário sem permissão para alterar");
            }
        } catch (IllegalArgumentException e) {
            log.info("O login informado não foi encontrado para ser alterado. Tentativa no login = {}. Usuario logado = {}", body.login(), body.userLogged());
            return ResponseEntity.badRequest().body("O Login informado não foi encontrado para ser alterado");
        }
    }

    @GetMapping("/{userLogin}")
    public ResponseEntity getUserByLogin(@PathVariable String userLogin) {
        Optional<User> userOptional = this.repository.findByLogin(userLogin);
        if (userOptional.isPresent()) {
            UserResponseDTO userResponseDTO = new UserResponseDTO(userOptional.get());
            log.info("Pesquisa de login efetuada com sucesso. Login = {}", userLogin);
            return ResponseEntity.ok(userResponseDTO);
        } else {
            log.info("Pesquisa com erro, login nao encontrado. Login = {}", userLogin);
            return ResponseEntity.badRequest().body("Login nao encontrado");
        }
    }

    @GetMapping
    public ResponseEntity getAllUsers(){
        List<UserResponseDTO> userList = this.repository.findAll().stream().map(UserResponseDTO::new).toList();

        return ResponseEntity.ok(userList);
    }


}
