package com.sistema.ap.app.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sistema.ap.app.entity.Producto;
import com.sistema.ap.app.services.IProducto;


@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    protected IProducto productosService;

    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        return ResponseEntity.ok(productosService.findAll());
    }

    @PostMapping("/save")
    public ResponseEntity<Producto> save(@RequestBody Producto productos) {
        return ResponseEntity.ok(productosService.save(productos));
    }

    @PutMapping(path = "/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Producto> update(@PathVariable("id") Integer id, @RequestBody Producto productos) {
        Producto updatedProductos = productosService.update(id, productos);
        if (updatedProductos != null) {
            return ResponseEntity.ok(updatedProductos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) { // Usa Integer
        Integer response = productosService.deleteById(id);
        if (response == 204) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}
