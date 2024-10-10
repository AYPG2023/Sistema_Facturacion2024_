package com.sistema.ap.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@jakarta.persistence.Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Cambiado a IDENTITY para autoincremento
    @Column(name = "id")
    private Integer id; // Cambiado a Integer para autoincremento

    @Column(nullable = false)
    private String nombre;

    @Column(name = "correo_electronico", length = 100, nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String telefono;
}
