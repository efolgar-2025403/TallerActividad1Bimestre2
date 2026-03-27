package com.everfolgar.kinalapp.controller;

import com.everfolgar.kinalapp.entity.Usuario;
import com.everfolgar.kinalapp.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @PostMapping
    public Usuario guardarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.guardarUsuario(usuario);
    }

    @GetMapping("/{id}")
    public Usuario obtenerUsuario(@PathVariable int id) {
        return usuarioService.obtenerUsuarioPorId(id);
    }

    @DeleteMapping("/{id}")
    public void eliminarUsuario(@PathVariable int id) {
        usuarioService.eliminarUsuario(id);
    }
    @PutMapping("/{id}")
    public Usuario actualizarUsuario(@PathVariable int id, @RequestBody Usuario usuario) {
        Usuario uExistente = usuarioService.obtenerUsuarioPorId(id);
        if(uExistente != null){
            uExistente.setUsername(usuario.getUsername());
            uExistente.setPassword(usuario.getPassword());
            uExistente.setEmail(usuario.getEmail());
            uExistente.setRol(usuario.getRol());
            uExistente.setEstado(usuario.getEstado());
            return usuarioService.guardarUsuario(uExistente);
        }
        return null; // o lanzar un error 404
    }
}