package com.agrestina.controllers;

import com.agrestina.domain.client.Client;
import com.agrestina.dto.client.ClientRequestDTO;
import com.agrestina.dto.client.ClientResponseDTO;
import com.agrestina.repositories.ClienteRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController()
@RequestMapping("client")
@Validated
public class ClientController {

    @Autowired
    ClienteRepository repository;

    @PostMapping
    public ResponseEntity postCLiente(@RequestBody @Valid ClientRequestDTO body){
    Client newCliente = new Client(body);

        try{

            this.repository.save(newCliente);
            log.info("Novo cliente cadastrado com sucesso. Cliente {} ", newCliente.getName());
            return ResponseEntity.ok().build();

        }catch(Exception e){
            log.info("Erro na insercao de novo cliente. Cliente {} {} ", newCliente.getName(),e.getMessage());
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping
    public ResponseEntity getAllClientes(){
        List<ClientResponseDTO> clienteList = this.repository.findAll().stream().map(ClientResponseDTO::new).toList();

        return ResponseEntity.ok(clienteList);
    }
}
