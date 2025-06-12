package org.ubp.edu.ar.ejemplocompletofx.util;

import org.ubp.edu.ar.ejemplocompletofx.modelo.Pedido;
import org.ubp.edu.ar.ejemplocompletofx.modelo.DetallePedido;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;

public class GeneradorComprobanteDB {

    public static void guardar(Pedido pedido) {
        String url = "jdbc:mysql://localhost:3306/ejemplo-completo-fx-2024";
        String user = "root";
        String password = ""; // Cambiá esto si tenés contraseña

        // ❗ CAMBIO: columnas corregidas a coincidir con las reales
        String sql = "INSERT INTO comprobante (nroPedido, fecha, cliente, vendedor, medioPago, detalle, total, totalConRecargo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            StringBuilder detalles = new StringBuilder();
            for (DetallePedido d : pedido.getDetalles()) {
                detalles.append(d.getProducto())
                        .append(" x ")
                        .append(d.getCantidad())
                        .append(" @ $")
                        .append(d.getPrecioVta())
                        .append("\n");
            }

            ps.setInt(1, pedido.getNro());
            ps.setString(2, sdf.format(pedido.getFecha()));
            ps.setString(3, pedido.getCliente().toString());
            ps.setString(4, pedido.getVendedor().toString());
            ps.setString(5, pedido.getMedioPago().toString());
            ps.setString(6, detalles.toString());
            ps.setDouble(7, pedido.calcularTotalDetalle());
            ps.setDouble(8, pedido.calcularTotalConRecargo());

            ps.executeUpdate();
            System.out.println("✅ Comprobante guardado en DB con éxito");

        } catch (Exception e) {
            System.err.println("❌ Error al guardar comprobante en DB: " + e.getMessage());
        }
    }
}
