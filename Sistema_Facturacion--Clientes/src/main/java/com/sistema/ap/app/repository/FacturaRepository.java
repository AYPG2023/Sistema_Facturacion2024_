package com.sistema.ap.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sistema.ap.app.entity.Factura;
import com.sistema.ap.app.entity.FacturaProducto;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {
	  
}
