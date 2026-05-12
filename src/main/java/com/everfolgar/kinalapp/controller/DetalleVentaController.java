package com.everfolgar.kinalapp.controller;

import com.everfolgar.kinalapp.entity.DetalleVenta;
import com.everfolgar.kinalapp.service.IDetalleVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalle-ventas")
public class DetalleVentaController {

    @Autowired
    private IDetalleVentaService detalleService;

    @GetMapping
    public List<DetalleVenta> listar() {
        return detalleService.listarDetalles();
    }

    @PostMapping
    public DetalleVenta guardar(@RequestBody DetalleVenta detalle) {
        return detalleService.guardarDetalle(detalle);
    }

    @GetMapping("/{id}")
    public DetalleVenta obtener(@PathVariable Integer id) {
        return detalleService.obtenerDetallePorId(id);
    }

    @PutMapping("/{id}")
    public DetalleVenta actualizarDetalle(@PathVariable Integer id, @RequestBody DetalleVenta detalle) {

        DetalleVenta detalleExistente = detalleService.obtenerDetallePorId(id);

        if (detalleExistente != null) {

            detalleExistente.setCantidad(detalle.getCantidad());
            detalleExistente.setVenta(detalle.getVenta());
            detalleExistente.setProducto(detalle.getProducto());

            return detalleService.guardarDetalle(detalleExistente);
        }

        return null;
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        detalleService.eliminarDetalle(id);
    }
}