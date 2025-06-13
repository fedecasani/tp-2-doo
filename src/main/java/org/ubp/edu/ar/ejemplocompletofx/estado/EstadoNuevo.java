package org.ubp.edu.ar.ejemplocompletofx.estado;

public class EstadoNuevo implements EstadoPedido {

    @Override
    public boolean puedeModificar() {
        return true;
    }

    @Override
    public boolean puedeCancelar() {
        return true;
    }

    @Override
    public boolean puedeEmitirComprobante() {
        return false;
    }

    @Override
    public String getNombre() {
        return "Nuevo";
    }
}
