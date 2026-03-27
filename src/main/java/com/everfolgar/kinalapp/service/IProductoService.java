package com.everfolgar.kinalapp.service;

import com.everfolgar.kinalapp.entity.Producto;
import java.util.List;

public interface IProductoService {

    List<Producto> listarProductos();

    Producto guardarProducto(Producto producto);

    Producto obtenerProductoPorId(int id);

    void eliminarProducto(int id);
}