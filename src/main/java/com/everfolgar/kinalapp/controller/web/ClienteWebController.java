package com.everfolgar.kinalapp.controller.web;

import com.everfolgar.kinalapp.entity.Cliente;
import com.everfolgar.kinalapp.service.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/clientes")
public class ClienteWebController {

    @Autowired
    private IClienteService clienteService;

    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("titulo", "Gestión de Clientes");
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("titulo", "Nuevo Cliente");
        return "clientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardarCliente(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        try {
            clienteService.guardar(cliente);
            redirectAttributes.addFlashAttribute("mensaje", "Cliente guardado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/web/clientes";
    }

    @GetMapping("/editar/{dpi}")
    public String mostrarFormularioEditar(@PathVariable String dpi, Model model, RedirectAttributes redirectAttributes) {
        return clienteService.buscarPorDPI(dpi)
                .map(cliente -> {
                    model.addAttribute("cliente", cliente);
                    model.addAttribute("titulo", "Editar Cliente");
                    return "clientes/formulario";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensaje", "Cliente no encontrado");
                    redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                    return "redirect:/web/clientes";
                });
    }

    @GetMapping("/eliminar/{dpi}")
    public String eliminarCliente(@PathVariable String dpi, RedirectAttributes redirectAttributes) {
        try {
            clienteService.eliminar(dpi);
            redirectAttributes.addFlashAttribute("mensaje", "Cliente eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/web/clientes";
    }
}