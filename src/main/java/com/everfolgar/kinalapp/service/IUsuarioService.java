package com.everfolgar.kinalapp.service;

import com.everfolgar.kinalapp.entity.Usuario;
import java.util.List;

public interface IUsuarioService {

    List<Usuario> listarUsuarios();

    Usuario guardarUsuario(Usuario usuario);

    Usuario obtenerUsuarioPorId(int id);

    void eliminarUsuario(int id);
}