package org.ubp.edu.ar.ejemplocompletofx.controladores;

import java.net.URL;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import net.synedra.validatorfx.Validator;
import org.ubp.edu.ar.ejemplocompletofx.estado.EstadoCancelado;
import org.ubp.edu.ar.ejemplocompletofx.estado.EstadoCompleto;
import org.ubp.edu.ar.ejemplocompletofx.estado.EstadoNuevo;
import org.ubp.edu.ar.ejemplocompletofx.estado.EstadoPedido;
import org.ubp.edu.ar.ejemplocompletofx.modelo.*;
import org.ubp.edu.ar.ejemplocompletofx.util.GeneradorComprobanteDB;
import org.ubp.edu.ar.ejemplocompletofx.util.GeneradorComprobanteTxt;

public class EditarPedidoController extends Controller implements Initializable {

    private Vendedor vendedor;
    private Cliente cliente;
    private Producto producto;
    private Pedido pedido;

    @FXML private TextField txtNro;
    @FXML private DatePicker txtFecha;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtTotal;
    @FXML private TextField txtTotalConRecargo;
    @FXML private TextField txtPagoCliente;
    @FXML private TextField txtVuelto;
    @FXML private Button btnGuardar;
    @FXML private Button btnAgregarItem;
    @FXML private Button btnQuitarItem;
    @FXML private Button btnCerrarEditarPedido;
    @FXML private TableView<DetallePedido> tableView;
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private ComboBox<Vendedor> cmbVendedor;
    @FXML private ComboBox<Producto> cmbProducto;
    @FXML private ComboBox<MedioPago> cmbMedioPago;
    @FXML private ComboBox<EstadoPedido> cmbEstado;

    private ObservableList<DetallePedido> datos = null;
    private ObservableList<Cliente> datosCmbCliente = null;
    private ObservableList<Vendedor> datosCmbVendedor = null;
    private ObservableList<Producto> datosCmbProducto = null;
    private Controller otherCtrl;
    private Validator validador;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cliente = new Cliente();
        vendedor = new Vendedor();
        producto = new Producto();
        
        cmbEstado.setItems(FXCollections.observableArrayList(
    new EstadoCancelado(),
    new EstadoCompleto(),
    new EstadoNuevo()
        ));


        cmbMedioPago.setItems(FXCollections.observableArrayList(MedioPago.values()));
        cmbMedioPago.getSelectionModel().select(MedioPago.CONTADO);

        txtPagoCliente.setDisable(true);
        txtVuelto.setDisable(true);

        this.txtPrecio.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d+\\.\\d+")) {
                txtPrecio.setText(newValue.replaceAll("[^\\d+\\.\\d+]", ""));
            }
        });

        this.validador = new Validator();
        this.validador.createCheck()
                .dependsOn("precioVta", this.txtPrecio.textProperty())
                .withMethod(context -> {
                    String texto = context.get("precioVta");
                    if (texto == null || texto.isEmpty()) {
                        context.error("Campo requerido");
                    }
                })
                .decorates(this.txtPrecio)
                .immediate();

        this.txtCantidad.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d+\\.\\d+")) {
                txtCantidad.setText(newValue.replaceAll("[^\\d+\\.\\d+]", ""));
            }
        });

        this.validador.createCheck()
                .dependsOn("cantidad", this.txtCantidad.textProperty())
                .withMethod(context -> {
                    String texto = context.get("cantidad");
                    if (texto == null || texto.isEmpty()) {
                        context.error("Campo requerido");
                    }
                })
                .decorates(this.txtCantidad)
                .immediate();

        if (this.tableView != null) {
            this.configureTable();
            this.loadData();
            this.btnGuardar.setDisable(false);
            if (this.tableView.getItems().isEmpty()) {
                this.btnGuardar.setDisable(true);
            }
        }
    }

    @FXML
    private void actualizarVuelto() {
        if (cmbMedioPago.getSelectionModel().getSelectedItem() == MedioPago.CONTADO) {
            try {
                float pago = Float.parseFloat(txtPagoCliente.getText());
                float totalConRecargo = pedido.calcularTotalConRecargo();
                float vuelto = pago - totalConRecargo;
                txtVuelto.setText(String.format("%.2f", Math.max(vuelto, 0)));
            } catch (NumberFormatException e) {
                txtVuelto.setText("0.00");
            }
        } else {
            txtPagoCliente.setText("");
            txtVuelto.setText("");
        }
    }

    @Override
    public void loadData() {
        this.progress.setVisible(true);
        List<Cliente> clientes = this.cliente.listarTodos();
        List<Vendedor> vendedores = this.vendedor.listarTodos();
        List<Producto> productos = this.producto.listarTodos();
        this.datosCmbCliente = FXCollections.observableList(clientes);
        this.cmbCliente.setItems(this.datosCmbCliente);
        this.datosCmbVendedor = FXCollections.observableList(vendedores);
        this.cmbVendedor.setItems(this.datosCmbVendedor);
        this.datosCmbProducto = FXCollections.observableList(productos);
        this.cmbProducto.setItems(this.datosCmbProducto);
        this.progress.setVisible(false);
    }

    private void configureTable() {
        TableColumn<DetallePedido, String> tcProd = (TableColumn<DetallePedido, String>) this.tableView.getColumns().get(0);
        tcProd.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProducto().toString()));
        TableColumn<DetallePedido, Float> tcCant = (TableColumn<DetallePedido, Float>) this.tableView.getColumns().get(1);
        tcCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        TableColumn<DetallePedido, Float> tcPrecVta = (TableColumn<DetallePedido, Float>) this.tableView.getColumns().get(2);
        tcPrecVta.setCellValueFactory(new PropertyValueFactory<>("precioVta"));
        TableColumn<DetallePedido, String> tcSubtotal = (TableColumn<DetallePedido, String>) this.tableView.getColumns().get(3);
        tcSubtotal.setCellValueFactory(new PropertyValueFactory<>("Subtotal"));
    }

    public void passData(Pedido pedido, Controller otherCtrl) {
        this.otherCtrl = otherCtrl;
        this.txtNro.setText(Integer.toString(pedido.getNro()));
        this.txtFecha.setValue(new java.sql.Date(pedido.getFecha().getTime()).toLocalDate());
        this.cmbCliente.setValue(pedido.getCliente());
        this.cmbVendedor.setValue(pedido.getVendedor());
        this.pedido = pedido;
        this.pedido.buscarDetalles();
        this.datos = FXCollections.observableList(this.pedido.getDetalles());
        this.tableView.setItems(this.datos);
        this.cmbMedioPago.getSelectionModel().select(pedido.getMedioPago() != null ? pedido.getMedioPago() : MedioPago.CONTADO);
        this.cmbEstado.getSelectionModel().select(pedido.getEstado());
        actualizarTotalConRecargo();
    }

    @FXML
    private void alCambiarMedioPago(ActionEvent event) {
        MedioPago seleccionado = cmbMedioPago.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            pedido.setMedioPago(seleccionado);
            actualizarTotalConRecargo();
            boolean esContado = seleccionado == MedioPago.CONTADO;
            txtPagoCliente.setDisable(!esContado);
            txtVuelto.setDisable(!esContado);
            if (!esContado) {
                txtPagoCliente.setText("");
                txtVuelto.setText("");
            }
        }
    }

    private void actualizarTotalConRecargo() {
        this.txtTotal.setText(String.format("%.2f", pedido.calcularTotalDetalle()));
        this.txtTotalConRecargo.setText(String.format("%.2f", pedido.calcularTotalConRecargo()));
        actualizarVuelto();
    }

    @FXML
    private void agregarItemDetalle() {
        if (validador.containsErrors() || this.cmbProducto.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, null, "Info", "Para agregar un nuevo item debe seleccionar un Producto y los demás campos requeridos");
            return;
        }
        Producto prod = this.cmbProducto.getSelectionModel().getSelectedItem();
        String cantStr = this.txtCantidad.getText();
        String precStr = this.txtPrecio.getText();
        float cant = Float.parseFloat(cantStr);
        float prec = Float.parseFloat(precStr);
        if (this.pedido.agregarItemDetallePedido(prod, cant, prec)) {
            this.datos = FXCollections.observableList(this.pedido.getDetalles());
            this.tableView.setItems(this.datos);
        } else {
            showAlert(Alert.AlertType.WARNING, null, "Info", "Ese producto ya esta en el detalle");
        }
        this.txtCantidad.setText("");
        this.txtPrecio.setText("");
        this.cmbProducto.setValue(null);
        this.tableView.getSelectionModel().clearSelection();
        this.btnGuardar.setDisable(this.tableView.getItems().isEmpty());
        actualizarTotalConRecargo();
    }

    @FXML
    private void quitarItemDetalle() {
        DetallePedido det = this.tableView.getSelectionModel().getSelectedItem();
        if (det != null) {
            this.tableView.getItems().remove(det);
            this.tableView.getSelectionModel().clearSelection();
        }
        this.txtCantidad.setText("");
        this.txtPrecio.setText("");
        this.cmbProducto.setValue(null);
        this.btnGuardar.setDisable(this.tableView.getItems().isEmpty());
        actualizarTotalConRecargo();
    }

    @FXML
    private void guardarPedido() {
        if (this.txtFecha.getValue() == null || this.cmbCliente.getSelectionModel().getSelectedItem() == null || this.cmbVendedor.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, null, "Info", "Debe seleccionar una fecha, un cliente y un vendedor");
            return;
        }
        if (this.datos.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, null, "Info", "No puede guardar un pedido sin un detalle");
            return;
        }
        boolean resp;
        this.pedido.setCliente(this.cmbCliente.getSelectionModel().getSelectedItem());
        this.pedido.setVendedor(this.cmbVendedor.getSelectionModel().getSelectedItem());
        this.pedido.setDetalles(this.datos);
        this.pedido.setFecha(Date.from(this.txtFecha.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        this.pedido.setMedioPago(this.cmbMedioPago.getSelectionModel().getSelectedItem());
        this.pedido.setEstado(this.cmbEstado.getSelectionModel().getSelectedItem());
        resp = this.pedido.getNro() > -1 ? this.pedido.modificar() : this.pedido.guardar();
        if (resp) {
            this.otherCtrl.loadData();
            showAlert(Alert.AlertType.INFORMATION, null, "Info", "Pedido guardado con exito");
            this.btnCerrarEditarPedido.fire();
            GeneradorComprobanteTxt.guardar(pedido);
            GeneradorComprobanteDB.guardar(pedido);
        } else {
            showAlert(Alert.AlertType.ERROR, null, "Info", "El Pedido no pudo ser guardado");
        }
    }

    @FXML
    private void alCambiarProducto(ActionEvent event) {
        Producto prod = this.cmbProducto.getSelectionModel().getSelectedItem();
        this.txtCantidad.setText("");
        this.txtPrecio.setText("");
        if (prod != null) {
            this.txtCantidad.setText("1.0");
            this.txtPrecio.setText(String.valueOf(prod.getPrecio()));
        }
    }

    @FXML
    private void obtenerDetallePedidoSeleccionado(MouseEvent evt) {
        if (evt.getClickCount() == 1) {
            DetallePedido det = this.tableView.getSelectionModel().getSelectedItem();
            if (det != null) {
                this.cmbProducto.setValue(det.getProducto());
                this.txtCantidad.setText(String.valueOf(det.getCantidad()));
                this.txtPrecio.setText(String.valueOf(det.getPrecioVta()));
            }
        }
    }
}