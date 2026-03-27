package com.everfolgar.kinalapp.service;

import com.everfolgar.kinalapp.entity.Venta;
import com.everfolgar.kinalapp.entity.Cliente;
import com.everfolgar.kinalapp.entity.Usuario;
import com.everfolgar.kinalapp.repository.VentaRepository;
import com.everfolgar.kinalapp.repository.ClienteRepository;
import com.everfolgar.kinalapp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VentaService implements IVentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    @Override
    public Venta guardarVenta(Venta venta) {

        Cliente clienteReal = clienteRepository
                .findById(venta.getCliente().getDPICliente())
                .orElse(null);

        Usuario usuarioReal = usuarioRepository
                .findById(venta.getUsuario().getCodigoUsuario())
                .orElse(null);

        venta.setCliente(clienteReal);
        venta.setUsuario(usuarioReal);

        return ventaRepository.save(venta);
    }

    @Override
    public Venta obtenerVentaPorId(Integer id) {
        return ventaRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminarVenta(Integer id) {
        ventaRepository.deleteById(id);
    }
}