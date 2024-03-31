package com.agrestina.dto.client;

import com.agrestina.domain.client.Client;

public record ClientResponseDTO(String id, String name, String document, String address) {
    public ClientResponseDTO(Client client){
        this(client.getId(), client.getName(), client.getDocument(), client.getAddress());
    }
}
