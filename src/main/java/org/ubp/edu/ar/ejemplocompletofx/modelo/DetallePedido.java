/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.ubp.edu.ar.ejemplocompletofx.modelo;

import org.ubp.edu.ar.ejemplocompletofx.dao.Dao;


/**
 *
 * @author agustin
 */
public class DetallePedido extends Modelo {

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecioVta() {
        return precioVta;
    }

    public void setPrecioVta(float precioVta) {
        this.precioVta = precioVta;
    }

    public Dao getDao() {
        return dao;
    }

    public void setDao(Dao dao) {
        this.dao = dao;
    }
    
    private Pedido pedido;
    private Producto producto;
    private float cantidad;
    private float precioVta;
    
    public float getSubtotal() {
        return precioVta * cantidad;
    } 
}
