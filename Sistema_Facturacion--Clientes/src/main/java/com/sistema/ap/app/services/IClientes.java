package com.sistema.ap.app.services;

import java.util.List;
import com.sistema.ap.app.entity.Cliente;

public interface IClientes {
    List<Cliente> findAll();
    Cliente save(Cliente cliente);
    Cliente update(Integer id, Cliente cliente);  
    Integer deleteById(Integer id);  
}
