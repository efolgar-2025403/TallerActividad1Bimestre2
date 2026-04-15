package com.everfolgar.kinalapp.controller.web;

import com.everfolgar.kinalapp.entity.Producto;
import com.everfolgar.kinalapp.entity.Usuario;
import com.everfolgar.kinalapp.service.IProductoService;
import com.everfolgar.kinalapp.service.IUsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tienda")
public class TiendaController {

    @Autowired
    private IProductoService productoService;

    @Autowired
    private IUsuarioService usuarioService;  // NUEVO

    @GetMapping("/catalogo")
    public String mostrarCatalogo(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String clienteDpi = (String) session.getAttribute("clienteDpi");

        if (clienteDpi == null) {
            redirectAttributes.addFlashAttribute("mensaje", "Debe iniciar sesión para acceder a la tienda");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/tienda/login";
        }

        List<Producto> productos = productoService.listarProductosDisponibles();
        List<LoginController.ItemCarrito> carrito = getCarritoFromSession(session);

        model.addAttribute("productos", productos);
        model.addAttribute("clienteNombre", session.getAttribute("clienteNombre"));
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

        List<LoginController.ItemCarrito> carrito = getCarritoFromSession(session);

        LoginController.ItemCarrito itemExistente = carrito.stream()
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
            carrito.add(new LoginController.ItemCarrito(
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
    private List<LoginController.ItemCarrito> getCarritoFromSession(HttpSession session) {
        List<LoginController.ItemCarrito> carrito = (List<LoginController.ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new java.util.ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }

    private int getCantidadTotal(List<LoginController.ItemCarrito> carrito) {
        return carrito.stream().mapToInt(LoginController.ItemCarrito::getCantidad).sum();
    }
}