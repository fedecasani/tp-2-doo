package org.ubp.edu.ar.ejemplocompletofx.estado;

public interface EstadoPedido {
    boolean puedeModificar();
    boolean puedeCancelar();
    boolean puedeEmitirComprobante();
    String getNombre(); // opcional, útil para debug o UI
}
