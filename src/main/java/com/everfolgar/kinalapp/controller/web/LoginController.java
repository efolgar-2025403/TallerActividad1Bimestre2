package com.everfolgar.kinalapp.controller.web;

import com.everfolgar.kinalapp.entity.Cliente;
import com.everfolgar.kinalapp.service.IClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tienda")
public class LoginController {

    @Autowired
    private IClienteService clienteService;

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("titulo", "Acceso a Tienda");
        return "tienda/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String dpi,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        return clienteService.buscarPorDPI(dpi)
                .filter(c -> c.getEstado() == 1)
                .map(cliente -> {
                    session.setAttribute("clienteDpi", cliente.getDPICliente());
                    session.setAttribute("clienteNombre", cliente.getNombreCliente() + " " + cliente.getApellidoCliente());
                    session.setAttribute("carrito", new java.util.ArrayList<ItemCarrito>());
                    redirectAttributes.addFlashAttribute("mensaje", "¡Bienvenido " + cliente.getNombreCliente() + "!");
                    redirectAttributes.addFlashAttribute("tipoMensaje", "success");
                    return "redirect:/tienda/catalogo";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensaje", "DPI no encontrado o cliente inactivo");
                    redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                    return "redirect:/tienda/login";
                });
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("mensaje", "Sesión cerrada correctamente");
        redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        return "redirect:/tienda/login";
    }

    // Clase interna para el carrito
    public static class ItemCarrito {
        private Integer codigoProducto;
        private String nombreProducto;
        private java.math.BigDecimal precio;
        private Integer cantidad;
        private Integer stockDisponible;

        public ItemCarrito() {}

        public ItemCarrito(Integer codigoProducto, String nombreProducto,
                           java.math.BigDecimal precio, Integer cantidad, Integer stockDisponible) {
            this.codigoProducto = codigoProducto;
            this.nombreProducto = nombreProducto;
            this.precio = precio;
            this.cantidad = cantidad;
            this.stockDisponible = stockDisponible;
        }

        public java.math.BigDecimal getSubtotal() {
            return precio.multiply(new java.math.BigDecimal(cantidad));
        }

        // Getters y Setters
        public Integer getCodigoProducto() { return codigoProducto; }
        public void setCodigoProducto(Integer codigoProducto) { this.codigoProducto = codigoProducto; }
        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
        public java.math.BigDecimal getPrecio() { return precio; }
        public void setPrecio(java.math.BigDecimal precio) { this.precio = precio; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public Integer getStockDisponible() { return stockDisponible; }
        public void setStockDisponible(Integer stockDisponible) { this.stockDisponible = stockDisponible; }
    }
}