package com.everfolgar.kinalapp.controller.web;

import com.everfolgar.kinalapp.entity.Venta;
import com.everfolgar.kinalapp.service.IClienteService;
import com.everfolgar.kinalapp.service.IUsuarioService;
import com.everfolgar.kinalapp.service.IVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/web/ventas")
public class VentaWebController {

    @Autowired
    private IVentaService ventaService;

    @Autowired
    private IClienteService clienteService;

    @Autowired
    private IUsuarioService usuarioService;

    // Este método maneja la conversión de fechas
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @GetMapping
    public String listarVentas(Model model) {
        model.addAttribute("ventas", ventaService.listarVentas());
        model.addAttribute("titulo", "Gestión de Ventas");
        return "ventas/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Venta venta = new Venta();
        venta.setFechaVenta(new Date());
        venta.setEstado(1);
        venta.setTotal(BigDecimal.ZERO);

        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        model.addAttribute("titulo", "Nueva Venta");
        return "ventas/formulario";
    }

    @PostMapping("/guardar")
    public String guardarVenta(@ModelAttribute Venta venta, RedirectAttributes redirectAttributes) {
        try {
            // Validaciones básicas
            if (venta.getTotal() == null) {
                venta.setTotal(BigDecimal.ZERO);
            }

            if (venta.getFechaVenta() == null) {
                venta.setFechaVenta(new Date());
            }

            // Asegurar que el DPI del cliente se guarde correctamente
            if (venta.getCliente() != null && venta.getCliente().getDPICliente() != null) {
                venta.getCliente().setDPICliente(venta.getCliente().getDPICliente().trim());
            }

            ventaService.guardarVenta(venta);
            redirectAttributes.addFlashAttribute("mensaje", "Venta guardada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            e.printStackTrace();
        }
        return "redirect:/web/ventas";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Venta venta = ventaService.obtenerVentaPorId(id);
        if (venta != null) {
            if (venta.getTotal() == null) {
                venta.setTotal(BigDecimal.ZERO);
            }

            model.addAttribute("venta", venta);
            model.addAttribute("clientes", clienteService.listarTodos());
            model.addAttribute("usuarios", usuarioService.listarUsuarios());
            model.addAttribute("titulo", "Editar Venta");
            return "ventas/formulario";
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Venta no encontrada");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/web/ventas";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarVenta(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            ventaService.eliminarVenta(id);
            redirectAttributes.addFlashAttribute("mensaje", "Venta eliminada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/web/ventas";
    }
}