package com.everfolgar.kinalapp.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/web/programador")
public class ProgramadorWebController {

    @GetMapping
    public String mostrarPerfil(Model model) {

        // Información personal
        model.addAttribute("nombre", "Ever Folgar");
        model.addAttribute("titulo", "Estudiante de Perito en Informática / Desarrollador Junior");
        model.addAttribute("descripcion", "Apasionado por la programación y desarrollo de aplicaciones web y de escritorio. Interesado en buenas prácticas y proyectos educativos.");
        model.addAttribute("email", "efolgar-2025403@kinal.edu.gt");
        model.addAttribute("telefono", "+502 5943-2674");
        model.addAttribute("github", "https://github.com/efolgar-2025403");

        // Habilidades técnicas
        List<String> habilidades = new ArrayList<>();
        habilidades.add("Java");
        habilidades.add("Spring Boot");
        habilidades.add("Spring MVC");
        habilidades.add("Thymeleaf");
        habilidades.add("JPA / Hibernate");
        habilidades.add("MySQL / SQL");
        habilidades.add("MongoDB (básico)");
        habilidades.add("HTML5 / CSS3");
        habilidades.add("Bootstrap");
        habilidades.add("JavaScript");
        habilidades.add("Git / GitHub");
        habilidades.add("MVC");
        habilidades.add("Diseño de bases de datos");
        habilidades.add("CRUD multi-entidad");

        model.addAttribute("habilidades", habilidades);
        model.addAttribute("proyectos", obtenerProyectos());
        model.addAttribute("experiencias", obtenerExperiencias());
        model.addAttribute("titulo", "Perfil del Programador");

        return "programador/perfil";
    }

    private List<ProyectoDTO> obtenerProyectos() {
        List<ProyectoDTO> proyectos = new ArrayList<>();

        proyectos.add(new ProyectoDTO(
                "KinalApp - Sistema de Ventas",
                "Aplicación web completa para gestión de clientes, productos, usuarios y ventas. Incluye interfaz Dark & Gold con Thymeleaf y Bootstrap 5.",
                "Java, Spring Boot, Thymeleaf, MySQL, JPA, Bootstrap 5"
        ));

        proyectos.add(new ProyectoDTO(
                "Sistema MULTI-CRUD Lácteos y Huevos",
                "Aplicación en consola y web para gestionar productos lácteos y huevos con todas las operaciones CRUD y patrón Singleton. Basado en MVC.",
                "Java, Spring Boot, Thymeleaf, MySQL, MVC, Singleton"
        ));

        proyectos.add(new ProyectoDTO(
                "CRUD de Gestión de Clientes",
                "Sistema de gestión de clientes con CRUD completo usando Java, Spring Boot y Thymeleaf. Incluye validaciones y diseño responsive.",
                "Java, Spring Boot, Thymeleaf, MySQL, MVC"
        ));

        proyectos.add(new ProyectoDTO(
                "Maison Folgar – Perfumería Web",
                "Proyecto web educativo con catálogo de productos, filtrado por marca y precios en quetzales. Navegación básica y estilo oscuro de lujo.",
                "HTML5, CSS3, JavaScript, Thymeleaf, Spring Boot, Bootstrap"
        ));

        proyectos.add(new ProyectoDTO(
                "Funciones SQL y Reportes",
                "Desarrollo de funciones y consultas SQL para bases de datos de gestión escolar y comercial. Manejo de DDL, DML y normalización.",
                "MySQL, SQL, DML, DDL, Normalización"
        ));

        return proyectos;
    }

    private List<ExperienciaDTO> obtenerExperiencias() {
        List<ExperienciaDTO> experiencias = new ArrayList<>();
        experiencias.add(new ExperienciaDTO(
                "Proyectos Académicos",
                "Centro Educativo Técnico Laboral Kinal",
                "2024 - Actual",
                "Desarrollo de sistemas de gestión y CRUDs usando Java, Spring Boot y Thymeleaf."
        ));
        experiencias.add(new ExperienciaDTO(
                "Prácticas y talleres",
                "entro Educativo Técnico Laboral Kinal",
                "2023 - 2024",
                "Participación en talleres de programación, bases de datos y desarrollo web."
        ));
        return experiencias;
    }

    // ===== CLASES INTERNAS (DTO) =====

    public static class ProyectoDTO {
        private String nombre;
        private String descripcion;
        private String tecnologias;

        public ProyectoDTO(String nombre, String descripcion, String tecnologias) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.tecnologias = tecnologias;
        }

        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }
        public String getTecnologias() { return tecnologias; }
    }

    public static class ExperienciaDTO {
        private String cargo;
        private String empresa;
        private String periodo;
        private String descripcion;

        public ExperienciaDTO(String cargo, String empresa, String periodo, String descripcion) {
            this.cargo = cargo;
            this.empresa = empresa;
            this.periodo = periodo;
            this.descripcion = descripcion;
        }

        public String getCargo() { return cargo; }
        public String getEmpresa() { return empresa; }
        public String getPeriodo() { return periodo; }
        public String getDescripcion() { return descripcion; }
    }
}