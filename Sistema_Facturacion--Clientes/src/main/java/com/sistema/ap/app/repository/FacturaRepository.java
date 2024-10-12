package com.sistema.ap.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param; // Importa el Param correcto

import com.sistema.ap.app.entity.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    
	@Query("SELECT f FROM Factura f WHERE f.id = :facturaId")
	Optional<Factura> findByIdWithProductos(@Param("facturaId") Integer facturaId);
}