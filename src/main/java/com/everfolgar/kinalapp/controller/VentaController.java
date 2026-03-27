package com.everfolgar.kinalapp.controller;

import com.everfolgar.kinalapp.entity.Venta;
import com.everfolgar.kinalapp.service.IVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private IVentaService ventaService;

    @GetMapping
    public List<Venta> listarVentas() {
        return ventaService.listarVentas();
    }

    @PostMapping
    public Venta guardarVenta(@RequestBody Venta venta) {
        return ventaService.guardarVenta(venta);
    }

    @GetMapping("/{id}")
    public Venta obtenerVenta(@PathVariable Integer id) {
        return ventaService.obtenerVentaPorId(id);
    }

    @PutMapping("/{id}")
    public Venta actualizarVenta(@PathVariable Integer id, @RequestBody Venta venta) {
        Venta vExistente = ventaService.obtenerVentaPorId(id);
        if (vExistente != null) {
            vExistente.setFechaVenta(venta.getFechaVenta());
            vExistente.setTotal(venta.getTotal());
            vExistente.setEstado(venta.getEstado());
            vExistente.setCliente(venta.getCliente());
            vExistente.setUsuario(venta.getUsuario());
            return ventaService.guardarVenta(vExistente);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void eliminarVenta(@PathVariable Integer id) {
        ventaService.eliminarVenta(id);
    }
}