package com.everfolgar.kinalapp.service;

import com.everfolgar.kinalapp.entity.Producto;
import com.everfolgar.kinalapp.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductoService implements IProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @Override
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public Producto obtenerProductoPorId(int id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminarProducto(int id) {
        productoRepository.deleteById(id);
    }
}