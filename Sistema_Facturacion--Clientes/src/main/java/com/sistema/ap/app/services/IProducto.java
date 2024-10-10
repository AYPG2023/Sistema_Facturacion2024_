package com.sistema.ap.app.services;

import java.util.List;
import com.sistema.ap.app.entity.Producto;

public interface IProducto {
    List<Producto> findAll();
    Producto save(Producto productos);
    Producto update(Integer productos_id, Producto productos); 
    Integer deleteById(Integer productos_id); 
}
