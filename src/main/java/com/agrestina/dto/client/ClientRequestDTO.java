package com.agrestina.dto.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record ClientRequestDTO(

        @NotBlank
        String name,

        @NotNull
        String document,

        @NotNull
        String address
) {

        public  ClientRequestDTO {
                if (name == null || name.isBlank()) {
                        log.info("Nome não pode ser nulo ou em branco");
                        throw new IllegalArgumentException("Nome não pode ser nulo ou em branco");
                }

                if (document == null || document.isBlank()) {
                        throw new IllegalArgumentException("Documento não pode ser nulo ou em branco");
                }

                if (address == null || address.isBlank()) {
                        throw new IllegalArgumentException("Endereco não pode ser nulo ou em branco");
                }
        }
}
