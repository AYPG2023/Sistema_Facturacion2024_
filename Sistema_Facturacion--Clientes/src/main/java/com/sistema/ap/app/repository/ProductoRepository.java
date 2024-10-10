package com.sistema.ap.app.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sistema.ap.app.entity.Factura;
import com.sistema.ap.app.entity.Producto;

@Repository
public interface ProductoRepository extends CrudRepository<Producto, Integer> {
}
