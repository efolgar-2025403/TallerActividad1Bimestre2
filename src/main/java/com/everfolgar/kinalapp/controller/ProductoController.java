package com.everfolgar.kinalapp.controller;

import com.everfolgar.kinalapp.entity.Producto;
import com.everfolgar.kinalapp.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private IProductoService productoService;

    @GetMapping
    public List<Producto> listarProductos() {
        return productoService.listarProductos();
    }

    @PostMapping
    public Producto guardarProducto(@RequestBody Producto producto) {
        return productoService.guardarProducto(producto);
    }

    @GetMapping("/{id}")
    public Producto obtenerProducto(@PathVariable int id) {
        return productoService.obtenerProductoPorId(id);
    }

    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable int id, @RequestBody Producto producto) {
        Producto pExistente = productoService.obtenerProductoPorId(id);
        if(pExistente != null){
            pExistente.setNombreProducto(producto.getNombreProducto());
            pExistente.setDescripcion(producto.getDescripcion());
            pExistente.setPrecio(producto.getPrecio());
            pExistente.setStock(producto.getStock());
            pExistente.setEstado(producto.getEstado());
            return productoService.guardarProducto(pExistente);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable int id) {
        productoService.eliminarProducto(id);
    }
}