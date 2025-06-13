package org.ubp.edu.ar.ejemplocompletofx.estado;

public class EstadoCancelado implements EstadoPedido {

    @Override
    public boolean puedeModificar() {
        return false;
    }

    @Override
    public boolean puedeCancelar() {
        return false;
    }

    @Override
    public boolean puedeEmitirComprobante() {
        return false;
    }

    @Override
    public String getNombre() {
        return "Cancelado";
    }
    
    @Override
    public String toString() {
        return getNombre();
    }
}
