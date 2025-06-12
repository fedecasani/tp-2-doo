package org.ubp.edu.ar.ejemplocompletofx.strategy;

public class RecargoCredito implements RecargoStrategy {
    @Override
    public float calcularRecargo(float total) {
        return total * 0.10f;
    }
}
