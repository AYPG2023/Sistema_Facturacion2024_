package com.sistema.ap.app.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sistema.ap.app.entity.Factura;
import com.sistema.ap.app.entity.Cliente;
import com.sistema.ap.app.entity.Producto;
import com.sistema.ap.app.repository.ClienteRepository;
import com.sistema.ap.app.repository.FacturaRepository;
import com.sistema.ap.app.repository.ProductoRepository;
import com.sistema.facturacion.ap.dto.FacturaDTO;
import com.sistema.facturacion.ap.dto.ProductoFacturaDTO;
import com.sistema.ap.app.entity.ApiResponse;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Factura>>> getAllFacturas() {
        return ResponseEntity.ok(new ApiResponse<>(facturaRepository.findAll(), "Lista de facturas", true));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Factura>> createFactura(@RequestBody FacturaDTO facturaDTO) {
        // Verificar si el cliente existe
        Optional<Cliente> clienteOpt = clienteRepository.findById(facturaDTO.getClienteId());
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(null, "El cliente no existe", false));
        }

        // Verificar si la lista de productos no está vacía
        if (facturaDTO.getProductos() == null || facturaDTO.getProductos().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(null, "La lista de productos no puede estar vacía", false));
        }

        Cliente cliente = clienteOpt.get();

        // Inicializar el total en BigDecimal para cálculos precisos
        BigDecimal total = BigDecimal.ZERO;

        // Procesar productos: verificar stock, calcular total y reducir stock
        for (ProductoFacturaDTO prodFacturaDTO : facturaDTO.getProductos()) {
            Optional<Producto> productoOpt = productoRepository.findById(prodFacturaDTO.getProductoId());
            if (productoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(null, "Producto con ID " + prodFacturaDTO.getProductoId() + " no existe", false));
            }

            Producto producto = productoOpt.get();

            // Verificar si hay suficiente stock
            if (producto.getStock() < prodFacturaDTO.getCantidad()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(null, "Stock insuficiente para el producto: " + producto.getNombre(), false));
            }

            // Calcular el total de este producto (precio * cantidad)
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(prodFacturaDTO.getCantidad()));
            total = total.add(subtotal);  // Añadir el subtotal al total

            // Reducir el stock del producto
            producto.setStock(producto.getStock() - prodFacturaDTO.getCantidad());
            productoRepository.save(producto);  // Guardar el producto con el stock actualizado
        }

        // Crear la entidad Factura
        Factura nuevaFactura = new Factura();
        nuevaFactura.setCliente(cliente);
        nuevaFactura.setFecha(LocalDateTime.now());
        nuevaFactura.setTotal(total.doubleValue());  // Convertir BigDecimal a double
        nuevaFactura.setDireccionEmpresa("Dirección predeterminada de la empresa");

        // Guardar la factura
        Factura facturaGuardada = facturaRepository.save(nuevaFactura);

        // Devolver la respuesta con la factura creada, el cliente y el total calculado
        return ResponseEntity.ok(new ApiResponse<>(facturaGuardada, "Factura creada exitosamente. Cliente: " + cliente.getNombre() + ", Total: " + total, true));
    }
}
