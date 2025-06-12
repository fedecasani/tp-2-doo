package org.ubp.edu.ar.ejemplocompletofx.util;

import org.ubp.edu.ar.ejemplocompletofx.modelo.Pedido;
import org.ubp.edu.ar.ejemplocompletofx.modelo.DetallePedido;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class GeneradorComprobanteTxt {

    public static void guardar(Pedido pedido) {
        try {
            // Crear carpeta si no existe
            File carpeta = new File("comprobantes");
            if (!carpeta.exists()) {
                carpeta.mkdir();
            }

            String nombreArchivo = "comprobantes/pedido_" + pedido.getNro() + ".txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            writer.write("PEDIDO N° " + pedido.getNro());
            writer.newLine();
            writer.write("Fecha: " + sdf.format(pedido.getFecha()));
            writer.newLine();
            writer.write("Cliente: " + pedido.getCliente());
            writer.newLine();
            writer.write("Vendedor: " + pedido.getVendedor());
            writer.newLine();
            writer.write("Medio de pago: " + pedido.getMedioPago());
            writer.newLine();
            writer.newLine();

            writer.write("DETALLE:");
            writer.newLine();
            for (DetallePedido det : pedido.getDetalles()) {
                writer.write("- " + det.getProducto() + " x " + det.getCantidad() + " @ $" + det.getPrecioVta() +
                        " = $" + (det.getCantidad() * det.getPrecioVta()));
                writer.newLine();
            }

            writer.newLine();
            writer.write("Total sin recargo: $" + String.format("%.2f", pedido.calcularTotalDetalle()));
            writer.newLine();
            writer.write("Total con recargo: $" + String.format("%.2f", pedido.calcularTotalConRecargo()));
            writer.newLine();

            writer.close();

            System.out.println("✅ Comprobante generado: " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("❌ Error al generar comprobante: " + e.getMessage());
        }
    }
}
