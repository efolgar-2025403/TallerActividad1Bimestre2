package com.everfolgar.kinalapp.repository;

import com.everfolgar.kinalapp.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    @Query("SELECT p FROM Producto p WHERE p.estado = 1 AND p.stock > 0")
    List<Producto> findProductosDisponibles();
}