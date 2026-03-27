package com.everfolgar.kinalapp.service;

import com.everfolgar.kinalapp.entity.DetalleVenta;
import java.util.List;

public interface IDetalleVentaService {
    List<DetalleVenta> listarDetalles();
    DetalleVenta guardarDetalle(DetalleVenta detalle);
    DetalleVenta obtenerDetallePorId(Integer id);
    void eliminarDetalle(Integer id);
}
