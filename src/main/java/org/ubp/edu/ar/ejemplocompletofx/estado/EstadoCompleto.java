package org.ubp.edu.ar.ejemplocompletofx.estado;

public class EstadoCompleto implements EstadoPedido {

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
        return true;
    }

    @Override
    public String getNombre() {
        return "Completado";
    }
}
