package com.sistema.ap.app.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sistema.ap.app.entity.Factura;
import com.sistema.ap.app.entity.FacturaProducto;
import com.sistema.ap.app.entity.Cliente;
import com.sistema.ap.app.entity.Producto;
import com.sistema.ap.app.repository.ClienteRepository;
import com.sistema.ap.app.repository.FacturaRepository;
import com.sistema.ap.app.repository.ProductoRepository;
import com.sistema.facturacion.ap.dto.FacturaDTO;
import com.sistema.facturacion.ap.dto.FacturaResponseDTO;
import com.sistema.facturacion.ap.dto.ProductoAdquiridoDTO; // Importa el nuevo DTO
import com.sistema.facturacion.ap.dto.ProductoFacturaDTO;

import jakarta.transaction.Transactional;
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

    // Obtener todas las facturas
    @GetMapping
    public ResponseEntity<ApiResponse<List<Factura>>> getAllFacturas() {
        List<Factura> facturas = facturaRepository.findAll();
        return ResponseEntity.ok(new ApiResponse<>(facturas, "Lista de facturas", true));
    }

 // Obtener factura por ID con productos asociados
    @GetMapping("/{id}")
    @Transactional // Asegura que hay una sesión activa para cargar productos
    public ResponseEntity<ApiResponse<FacturaResponseDTO>> getFacturaById(@PathVariable("id") Integer id) {
        // Usar el método que incluye JOIN FETCH para cargar los productos
        Optional<Factura> facturaOpt = facturaRepository.findByIdWithProductos(id);
        if (facturaOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(null, "Factura no encontrada", false));
        }

        Factura factura = facturaOpt.get();

        // Lista para productos adquiridos
        List<ProductoAdquiridoDTO> productosAdquiridos = new ArrayList<>();

        // Obtener los productos asociados a la factura
        for (FacturaProducto fp : factura.getProductos()) {
            Producto producto = fp.getProducto(); 
            if (producto != null) {
                productosAdquiridos.add(new ProductoAdquiridoDTO(
                    producto.getNombre(),    
                    producto.getPrecio(),    
                    fp.getCantidad()         
                ));
            }
        }

        // Crear el DTO de respuesta de la factura
        FacturaResponseDTO facturaResponse = new FacturaResponseDTO(
            factura.getId(),
            factura.getCliente().getNombre(),
            factura.getTotal(),
            factura.getFecha(),
            productosAdquiridos,
            factura.getDireccionEmpresa()
        );

        return ResponseEntity.ok(new ApiResponse<>(facturaResponse, "Factura obtenida con éxito", true));
    }



    // Crear nueva factura
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<FacturaResponseDTO>> createFactura(@RequestBody FacturaDTO facturaDTO) {
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
        BigDecimal total = BigDecimal.ZERO;
        Factura nuevaFactura = new Factura();
        nuevaFactura.setCliente(cliente);
        nuevaFactura.setFecha(LocalDateTime.now());
        nuevaFactura.setDireccionEmpresa("1 av 1-69 zona 15 Torre 360 Innova AP");

        // Lista para productos adquiridos
        List<ProductoAdquiridoDTO> productosAdquiridos = new ArrayList<>();

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
            total = total.add(subtotal);

            // Reducir el stock del producto
            producto.setStock(producto.getStock() - prodFacturaDTO.getCantidad());
            productoRepository.save(producto);

            // Agregar producto a la lista de productos adquiridos
            productosAdquiridos.add(new ProductoAdquiridoDTO(
                producto.getNombre(), // Nombre del producto
                producto.getPrecio(), // Precio del producto
                prodFacturaDTO.getCantidad() // Cantidad comprada (opcional)
            ));
        }

        // Establecer el total de la factura
        nuevaFactura.setTotal(total.doubleValue());

        // Guardar la factura
        Factura facturaGuardada = facturaRepository.save(nuevaFactura);

     // Crear respuesta DTO con la información requerida
        FacturaResponseDTO respuestaFactura = new FacturaResponseDTO(
            facturaGuardada.getId(),
            cliente.getNombre(),
            facturaGuardada.getTotal(),
            facturaGuardada.getFecha(),
            productosAdquiridos,  // Aquí faltaba la coma
            facturaGuardada.getDireccionEmpresa() // Incluir la dirección correctamente
        );


        // Devolver la respuesta con la factura creada
        return ResponseEntity.ok(new ApiResponse<>(respuestaFactura, "Factura creada exitosamente. Cliente: " + cliente.getNombre() + ", Total: " + total, true));
    }

    // Método para obtener todos los productos
    @GetMapping("/productos")
    public ResponseEntity<ApiResponse<List<Producto>>> getAllProductos() {
        List<Producto> productos = productoRepository.findAll();
        return ResponseEntity.ok(new ApiResponse<>(productos, "Lista de productos", true));
    }
}
