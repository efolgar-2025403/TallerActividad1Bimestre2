package com.everfolgar.kinalapp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_ventas")
public class DetalleVenta {

    @Id
    @Column(name = "codigo_detalle")
    private Integer codigoDetalle;

    @Column
    private Integer cantidad;

    @Column(precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "codigo_venta")
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "codigo_producto")
    private Producto producto;

    public DetalleVenta() {
    }

    public DetalleVenta(Integer codigoDetalle, Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal, Venta venta, Producto producto) {
        this.codigoDetalle = codigoDetalle;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.venta = venta;
        this.producto = producto;
    }

    public Integer getCodigoDetalle() { return codigoDetalle; }
    public void setCodigoDetalle(Integer codigoDetalle) { this.codigoDetalle = codigoDetalle; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
}