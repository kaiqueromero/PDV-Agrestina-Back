package com.agrestina.repositories;

import com.agrestina.domain.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Client, String> {
}
