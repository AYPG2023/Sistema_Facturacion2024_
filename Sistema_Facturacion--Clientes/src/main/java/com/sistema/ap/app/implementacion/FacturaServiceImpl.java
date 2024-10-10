package com.sistema.ap.app.implementacion;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistema.ap.app.entity.Cliente;
import com.sistema.ap.app.entity.Factura;
import com.sistema.ap.app.entity.Producto;
import com.sistema.ap.app.repository.ClienteRepository;
import com.sistema.ap.app.repository.FacturaRepository;
import com.sistema.ap.app.repository.ProductoRepository;
import com.sistema.ap.app.services.IFacturaService;

@Service
public class FacturaServiceImpl implements IFacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Factura> findAll() {
        return StreamSupport
            .stream(facturaRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());
    }

    @Override
    public Factura save(Factura factura) {
        return facturaRepository.save(factura);
    }

    @Override
    public Factura findById(Integer id) {
        return facturaRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Integer id) {
        facturaRepository.deleteById(id);
    }

    public Cliente obtenerCliente(Integer idCliente) {
        return clienteRepository.findById(idCliente).orElse(null);
    }

    public Producto obtenerProducto(Integer idProducto) {
        return productoRepository.findById(idProducto).orElse(null);
    }
}
