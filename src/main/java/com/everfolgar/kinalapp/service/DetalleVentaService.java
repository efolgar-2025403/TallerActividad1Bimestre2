package com.everfolgar.kinalapp.service;

import com.everfolgar.kinalapp.entity.*;
import com.everfolgar.kinalapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DetalleVentaService implements IDetalleVentaService {

    @Autowired
    private DetalleVentaRepository detalleRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<DetalleVenta> listarDetalles() {
        return detalleRepository.findAll();
    }

    @Override
    public DetalleVenta guardarDetalle(DetalleVenta detalle) {

        Venta ventaReal = ventaRepository
                .findById(detalle.getVenta().getCodigoVenta())
                .orElse(null);

        Producto productoReal = productoRepository
                .findById(detalle.getProducto().getCodigoProducto())
                .orElse(null);

        detalle.setPrecioUnitario(productoReal.getPrecio());

        detalle.setSubtotal(
                productoReal.getPrecio().multiply(
                        new BigDecimal(detalle.getCantidad())
                )
        );

        detalle.setVenta(ventaReal);
        detalle.setProducto(productoReal);

        return detalleRepository.save(detalle);
    }

    @Override
    public DetalleVenta obtenerDetallePorId(Integer id) {
        return detalleRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminarDetalle(Integer id) {
        detalleRepository.deleteById(id);
    }
}