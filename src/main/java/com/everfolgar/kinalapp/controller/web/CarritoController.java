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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
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
        String clienteDpi = (String) session.getAttribute("clienteDpi");

        if (clienteDpi == null) {
            redirectAttributes.addFlashAttribute("mensaje", "Debe iniciar sesión para acceder al carrito");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/tienda/login";
        }

        List<LoginController.ItemCarrito> carrito = getCarritoFromSession(session);

        if (carrito.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "El carrito está vacío");
            redirectAttributes.addFlashAttribute("tipoMensaje", "info");
            return "redirect:/tienda/catalogo";
        }

        BigDecimal total = carrito.stream()
                .map(LoginController.ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Obtener lista de vendedores activos
        List<Usuario> vendedores = usuarioService.listarUsuarios()
                .stream()
                .filter(u -> u.getEstado() == 1)
                .collect(Collectors.toList());

        model.addAttribute("items", carrito);
        model.addAttribute("total", total);
        model.addAttribute("vendedores", vendedores);
        model.addAttribute("clienteNombre", session.getAttribute("clienteNombre"));
        model.addAttribute("titulo", "Carrito de Compras");
        return "tienda/carrito";
    }

    @PostMapping("/actualizar")
    public String actualizarCantidad(@RequestParam Integer codigoProducto,
                                     @RequestParam Integer cantidad,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        List<LoginController.ItemCarrito> carrito = getCarritoFromSession(session);

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
        List<LoginController.ItemCarrito> carrito = getCarritoFromSession(session);

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
        String clienteDpi = (String) session.getAttribute("clienteDpi");
        List<LoginController.ItemCarrito> carrito = getCarritoFromSession(session);

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
            for (LoginController.ItemCarrito item : carrito) {
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
                    .map(LoginController.ItemCarrito::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            venta.setTotal(total);

            // Asignar cliente
            Cliente cliente = new Cliente();
            cliente.setDPICliente(clienteDpi);
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
            for (LoginController.ItemCarrito item : carrito) {
                Producto producto = productoService.obtenerProductoPorId(item.getCodigoProducto());
                producto.setStock(producto.getStock() - item.getCantidad());
                productoService.guardarProducto(producto);
            }

            // Limpiar carrito
            session.setAttribute("carrito", new java.util.ArrayList<LoginController.ItemCarrito>());

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
        session.setAttribute("carrito", new java.util.ArrayList<LoginController.ItemCarrito>());
        redirectAttributes.addFlashAttribute("mensaje", "Carrito vaciado correctamente");
        redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        return "redirect:/tienda/catalogo";
    }

    @SuppressWarnings("unchecked")
    private List<LoginController.ItemCarrito> getCarritoFromSession(HttpSession session) {
        List<LoginController.ItemCarrito> carrito = (List<LoginController.ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new java.util.ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }

    private Integer generarCodigoVenta() {
        return (int) (System.currentTimeMillis() % 1000000);
    }
}