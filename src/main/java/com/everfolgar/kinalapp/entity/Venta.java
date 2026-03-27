package com.everfolgar.kinalapp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @Column(name = "codigo_venta")
    private Integer codigoVenta;

    @Column
    private Date fechaVenta;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @Column
    private Integer estado;

    @ManyToOne
    @JoinColumn(name = "dpi_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "codigo_usuario")
    private Usuario usuario;

    public Venta() {
    }

    public Venta(Integer codigoVenta, Date fechaVenta, BigDecimal total, Integer estado, Cliente cliente, Usuario usuario) {
        this.codigoVenta = codigoVenta;
        this.fechaVenta = fechaVenta;
        this.total = total;
        this.estado = estado;
        this.cliente = cliente;
        this.usuario = usuario;
    }

    public Integer getCodigoVenta() { return codigoVenta; }
    public void setCodigoVenta(Integer codigoVenta) { this.codigoVenta = codigoVenta; }

    public Date getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(Date fechaVenta) { this.fechaVenta = fechaVenta; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}