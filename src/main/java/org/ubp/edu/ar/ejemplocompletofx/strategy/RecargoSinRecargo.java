package org.ubp.edu.ar.ejemplocompletofx.strategy;

public class RecargoSinRecargo implements RecargoStrategy {
    @Override
    public float calcularRecargo(float total) {
        return 0f;
    }
}
