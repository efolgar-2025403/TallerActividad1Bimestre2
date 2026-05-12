package com.everfolgar.kinalapp.controller.web;

import com.everfolgar.kinalapp.entity.Producto;
import com.everfolgar.kinalapp.service.IProductoService;
import com.everfolgar.kinalapp.service.IUsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tienda")
public class TiendaController {

    @Autowired
    private IProductoService productoService;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("/catalogo")
    public String mostrarCatalogo(Model model, HttpSession session) {
        // Obtener el usuario autenticado de Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Guardar en sesión para compatibilidad con el carrito
        session.setAttribute("clienteDpi", username);
        session.setAttribute("clienteNombre", username);

        List<Producto> productos = productoService.listarProductosDisponibles();
        List<CarritoController.ItemCarrito> carrito = getCarritoFromSession(session);

        model.addAttribute("productos", productos);
        model.addAttribute("clienteNombre", username);
        model.addAttribute("cantidadItems", getCantidadTotal(carrito));
        model.addAttribute("titulo", "Catálogo de Productos");
        return "tienda/catalogo";
    }

    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Integer codigoProducto,
                                   @RequestParam Integer cantidad,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Producto producto = productoService.obtenerProductoPorId(codigoProducto);

        if (producto == null || producto.getStock() < cantidad) {
            redirectAttributes.addFlashAttribute("mensaje", "Stock insuficiente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/tienda/catalogo";
        }

        List<CarritoController.ItemCarrito> carrito = getCarritoFromSession(session);

        CarritoController.ItemCarrito itemExistente = carrito.stream()
                .filter(i -> i.getCodigoProducto().equals(codigoProducto))
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            int nuevaCantidad = itemExistente.getCantidad() + cantidad;
            if (nuevaCantidad <= producto.getStock()) {
                itemExistente.setCantidad(nuevaCantidad);
                redirectAttributes.addFlashAttribute("mensaje", "Cantidad actualizada");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Stock insuficiente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            }
        } else {
            carrito.add(new CarritoController.ItemCarrito(
                    producto.getCodigoProducto(),
                    producto.getNombreProducto(),
                    producto.getPrecio(),
                    cantidad,
                    producto.getStock()
            ));
            redirectAttributes.addFlashAttribute("mensaje", "Producto agregado al carrito");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        }

        session.setAttribute("carrito", carrito);
        return "redirect:/tienda/catalogo";
    }

    @SuppressWarnings("unchecked")
    private List<CarritoController.ItemCarrito> getCarritoFromSession(HttpSession session) {
        List<CarritoController.ItemCarrito> carrito = (List<CarritoController.ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new java.util.ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }

    private int getCantidadTotal(List<CarritoController.ItemCarrito> carrito) {
        return carrito.stream().mapToInt(CarritoController.ItemCarrito::getCantidad).sum();
    }
}