package com.sistema.facturacion.ap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

	
	private String productos_id;
    private String nombre;
    private double precio;
    private int stock;
}
