package com.everfolgar.kinalapp.controller.web;

import com.everfolgar.kinalapp.entity.Cliente;
import com.everfolgar.kinalapp.entity.Producto;
import com.everfolgar.kinalapp.entity.Usuario;
import com.everfolgar.kinalapp.entity.Venta;
import com.everfolgar.kinalapp.service.IProductoService;
import com.everfolgar.kinalapp.service.IUsuarioService;
import com.everfolgar.kinalapp.service.IVentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tienda")
public class CarritoController {

    @Autowired
    private IProductoService productoService;

    @Autowired
    private IVentaService ventaService;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("/carrito")
    public String verCarrito(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Obtener el usuario autenticado de Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        List<ItemCarrito> carrito = getCarritoFromSession(session);

        if (carrito.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "El carrito está vacío");
            redirectAttributes.addFlashAttribute("tipoMensaje", "info");
            return "redirect:/tienda/catalogo";
        }

        BigDecimal total = carrito.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Obtener lista de vendedores activos
        List<Usuario> vendedores = usuarioService.listarUsuarios()
                .stream()
                .filter(u -> u.getEstado() == 1)
                .collect(Collectors.toList());

        model.addAttribute("items", carrito);
        model.addAttribute("total", total);
        model.addAttribute("vendedores", vendedores);
        model.addAttribute("clienteNombre", username);
        model.addAttribute("titulo", "Carrito de Compras");
        return "tienda/carrito";
    }

    @PostMapping("/actualizar")
    public String actualizarCantidad(@RequestParam Integer codigoProducto,
                                     @RequestParam Integer cantidad,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        List<ItemCarrito> carrito = getCarritoFromSession(session);

        Producto producto = productoService.obtenerProductoPorId(codigoProducto);

        carrito.stream()
                .filter(i -> i.getCodigoProducto().equals(codigoProducto))
                .findFirst()
                .ifPresent(item -> {
                    if (cantidad <= 0) {
                        carrito.remove(item);
                        redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado del carrito");
                        redirectAttributes.addFlashAttribute("tipoMensaje", "success");
                    } else if (producto != null && cantidad <= producto.getStock()) {
                        item.setCantidad(cantidad);
                        redirectAttributes.addFlashAttribute("mensaje", "Cantidad actualizada");
                        redirectAttributes.addFlashAttribute("tipoMensaje", "success");
                    } else {
                        redirectAttributes.addFlashAttribute("mensaje", "Stock insuficiente");
                        redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                    }
                });

        session.setAttribute("carrito", carrito);
        return "redirect:/tienda/carrito";
    }

    @GetMapping("/eliminar/{codigoProducto}")
    public String eliminarItem(@PathVariable Integer codigoProducto,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        List<ItemCarrito> carrito = getCarritoFromSession(session);

        carrito.removeIf(i -> i.getCodigoProducto().equals(codigoProducto));
        session.setAttribute("carrito", carrito);

        redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado del carrito");
        redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        return "redirect:/tienda/carrito";
    }

    @PostMapping("/finalizar")
    public String finalizarCompra(@RequestParam(required = false) Integer codigoVendedor,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        List<ItemCarrito> carrito = getCarritoFromSession(session);

        if (carrito.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "El carrito está vacío");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/tienda/catalogo";
        }

        // Validar que se haya seleccionado un vendedor
        if (codigoVendedor == null) {
            redirectAttributes.addFlashAttribute("mensaje", "Debe seleccionar un vendedor");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/tienda/carrito";
        }

        try {
            // Validar stock antes de finalizar
            for (ItemCarrito item : carrito) {
                Producto producto = productoService.obtenerProductoPorId(item.getCodigoProducto());
                if (producto == null || producto.getStock() < item.getCantidad()) {
                    redirectAttributes.addFlashAttribute("mensaje",
                            "Stock insuficiente para: " + item.getNombreProducto());
                    redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                    return "redirect:/tienda/carrito";
                }
            }

            // Crear la venta
            Venta venta = new Venta();
            venta.setFechaVenta(new Date());
            venta.setEstado(1);

            // Calcular total
            BigDecimal total = carrito.stream()
                    .map(ItemCarrito::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            venta.setTotal(total);

            // Asignar cliente
            Cliente cliente = new Cliente();
            cliente.setDPICliente(username);
            venta.setCliente(cliente);

            // Asignar vendedor seleccionado
            Usuario vendedor = usuarioService.obtenerUsuarioPorId(codigoVendedor);
            if (vendedor == null) {
                redirectAttributes.addFlashAttribute("mensaje", "Vendedor no encontrado");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/tienda/carrito";
            }
            venta.setUsuario(vendedor);

            // Generar código de venta
            venta.setCodigoVenta(generarCodigoVenta());

            // Guardar venta
            ventaService.guardarVenta(venta);

            // Actualizar stock de productos
            for (ItemCarrito item : carrito) {
                Producto producto = productoService.obtenerProductoPorId(item.getCodigoProducto());
                producto.setStock(producto.getStock() - item.getCantidad());
                productoService.guardarProducto(producto);
            }

            // Limpiar carrito
            session.setAttribute("carrito", new ArrayList<ItemCarrito>());

            redirectAttributes.addFlashAttribute("mensaje",
                    "¡Compra finalizada con éxito! Total: Q " + total + " | Vendedor: " + vendedor.getUsername());
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/tienda/catalogo";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al procesar la compra: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/tienda/carrito";
        }
    }

    @GetMapping("/vaciar")
    public String vaciarCarrito(HttpSession session, RedirectAttributes redirectAttributes) {
        session.setAttribute("carrito", new ArrayList<ItemCarrito>());
        redirectAttributes.addFlashAttribute("mensaje", "Carrito vaciado correctamente");
        redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        return "redirect:/tienda/catalogo";
    }

    @SuppressWarnings("unchecked")
    private List<ItemCarrito> getCarritoFromSession(HttpSession session) {
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }

    private Integer generarCodigoVenta() {
        return (int) (System.currentTimeMillis() % 1000000);
    }

    // CLASE INTERNA ItemCarrito
    public static class ItemCarrito {
        private Integer codigoProducto;
        private String nombreProducto;
        private BigDecimal precio;
        private Integer cantidad;
        private Integer stockDisponible;
        private BigDecimal subtotal;

        public ItemCarrito() {}

        public ItemCarrito(Integer codigoProducto, String nombreProducto,
                           BigDecimal precio, Integer cantidad, Integer stockDisponible) {
            this.codigoProducto = codigoProducto;
            this.nombreProducto = nombreProducto;
            this.precio = precio;
            this.cantidad = cantidad;
            this.stockDisponible = stockDisponible;
            this.subtotal = precio.multiply(new BigDecimal(cantidad));
        }

        public BigDecimal getSubtotal() {
            return precio.multiply(new BigDecimal(cantidad));
        }

        // Getters y Setters
        public Integer getCodigoProducto() { return codigoProducto; }
        public void setCodigoProducto(Integer codigoProducto) { this.codigoProducto = codigoProducto; }
        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
        public BigDecimal getPrecio() { return precio; }
        public void setPrecio(BigDecimal precio) { this.precio = precio; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
            this.subtotal = this.precio.multiply(new BigDecimal(cantidad));
        }
        public Integer getStockDisponible() { return stockDisponible; }
        public void setStockDisponible(Integer stockDisponible) { this.stockDisponible = stockDisponible; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }
}