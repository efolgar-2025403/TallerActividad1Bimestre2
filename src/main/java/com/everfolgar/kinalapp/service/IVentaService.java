package com.everfolgar.kinalapp.service;

import com.everfolgar.kinalapp.entity.Venta;
import java.util.List;

public interface IVentaService {
    List<Venta> listarVentas();
    Venta guardarVenta(Venta venta);
    Venta obtenerVentaPorId(Integer id);
    void eliminarVenta(Integer id);
}