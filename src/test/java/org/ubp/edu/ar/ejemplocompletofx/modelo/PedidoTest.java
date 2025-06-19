package org.ubp.edu.ar.ejemplocompletofx.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PedidoTest {

    @Test
    public void testCalcularTotalDetalle() {
        Pedido pedido = new Pedido();

        Producto prod1 = new Producto("Botines", "123456", 1000, 10);
        Producto prod2 = new Producto("Pelota", "789012", 2000, 5);

        pedido.agregarItemDetallePedido(prod1, 2, 1000);
        pedido.agregarItemDetallePedido(prod2, 1, 2000);

        float esperado = 4000;
        assertEquals(esperado, pedido.calcularTotalDetalle(), 0.01);
    }

    @Test
    public void testAgregarItemRepetido() {
        Pedido pedido = new Pedido();
        Producto prod1 = new Producto("Pelota", "789012", 2000, 5);

        boolean primero = pedido.agregarItemDetallePedido(prod1, 1, 2000);
        boolean segundo = pedido.agregarItemDetallePedido(prod1, 2, 2000);

        assertTrue(primero);
        assertFalse(segundo); // ya estaba agregado
    }
}
