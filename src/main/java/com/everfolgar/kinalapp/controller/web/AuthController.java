package com.everfolgar.kinalapp.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/tienda/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {

        if (error != null) {
            model.addAttribute("mensaje", "Usuario o contraseña incorrectos");
            model.addAttribute("tipoMensaje", "error");
        }

        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesión correctamente");
            model.addAttribute("tipoMensaje", "success");
        }

        model.addAttribute("titulo", "Iniciar Sesión");
        return "login/login";
    }

    @GetMapping("/error/403")
    public String accesoDenegado(Model model) {
        model.addAttribute("titulo", "Acceso Denegado");
        model.addAttribute("mensaje", "No tienes permisos para acceder a esta página");
        model.addAttribute("tipoMensaje", "error");
        return "error/403";
    }
}