package com.agrestina.domain.client;

import com.agrestina.dto.client.ClientRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "client")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String document;

    @Column(nullable = false)
    private String address;


    public Client(ClientRequestDTO data){
        this.name = data.name();
        this.document = data.document();
        this.address = data.address();
    }
}