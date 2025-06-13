package org.ubp.edu.ar.ejemplocompletofx.modelo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.ubp.edu.ar.ejemplocompletofx.dao.Dao;
import org.ubp.edu.ar.ejemplocompletofx.dao.PedidoDao;
import org.ubp.edu.ar.ejemplocompletofx.dto.PedidoDto;
import org.ubp.edu.ar.ejemplocompletofx.factories.FabricaDao;
import org.ubp.edu.ar.ejemplocompletofx.strategy.RecargoStrategy;
import org.ubp.edu.ar.ejemplocompletofx.strategy.RecargoCredito;
import org.ubp.edu.ar.ejemplocompletofx.strategy.RecargoSinRecargo;
import org.ubp.edu.ar.ejemplocompletofx.estado.*;


public class Pedido extends Modelo {

    private int nro;
    private Date fecha;
    private Cliente cliente;
    private Vendedor vendedor;
    private List<DetallePedido> detalles = new ArrayList<>();
    private ModelMapper mapper = new ModelMapper();

    private RecargoStrategy estrategiaRecargo = new RecargoSinRecargo(); // default
    private MedioPago medioPago;

    public Pedido() {
        this.dao = FabricaDao.fabricar("PedidoDao");
    }

    public Pedido(int nro, Date fecha, Cliente cliente, Vendedor vendedor) {
        this.dao = new PedidoDao();
        this.nro = nro;
        this.fecha = fecha;
        this.cliente = cliente;
        this.vendedor = vendedor;
        detalles = new ArrayList<>();
    }

    public int getNro() { return nro; }
    public void setNro(int nro) { this.nro = nro; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Vendedor getVendedor() { return vendedor; }
    public void setVendedor(Vendedor vendedor) { this.vendedor = vendedor; }

    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }

    public ModelMapper getMapper() { return mapper; }
    public void setMapper(ModelMapper mapper) { this.mapper = mapper; }

    public Dao getDao() { return dao; }
    public void setDao(Dao dao) { this.dao = dao; }

    public void setMedioPago(MedioPago medioPago) {
        this.medioPago = medioPago;
        switch (medioPago) {
            case CREDITO -> this.estrategiaRecargo = new RecargoCredito();
            default -> this.estrategiaRecargo = new RecargoSinRecargo();
        }
    }

    public MedioPago getMedioPago() {
        return this.medioPago;
    }

    public float calcularRecargo() {
        return this.estrategiaRecargo.calcularRecargo(this.calcularTotalDetalle());
    }

    public float calcularTotalConRecargo() {
        return this.calcularTotalDetalle() + this.calcularRecargo();
    }

    public List<Pedido> listarTodos() {
        List<PedidoDto> pedidosDto = this.dao.listarTodos();
        return Arrays.asList(this.mapper.map(pedidosDto, Pedido[].class));
    }

    public List<Pedido> listarPorNro(int nro) {
        List<PedidoDto> pedidosDto = this.dao.listarPorCriterio(new PedidoDto(nro));
        return Arrays.asList(this.mapper.map(pedidosDto, Pedido[].class));
    }

    public void buscarDetalles() {
        PedidoDto pedidoDto = mapper.map(this, PedidoDto.class);
        pedidoDto = (PedidoDto) this.dao.buscar(pedidoDto);
        List<DetallePedido> dets = new ArrayList<>(Arrays.asList(this.mapper.map(pedidoDto.getDetalles(), DetallePedido[].class)));
        this.detalles = dets;
    }

    public boolean agregarItemDetallePedido(Producto prod, float cantidad, float precVta) {
        DetallePedido det = new DetallePedido();
        det.setCantidad(cantidad);
        det.setPedido(this);
        det.setPrecioVta(precVta);
        det.setProducto(prod);
        Optional<DetallePedido> detOpt = this.detalles
                .stream()
                .filter(x -> x.getProducto().getCodBarra().equals(prod.getCodBarra())).findFirst();
        if (!detOpt.isPresent()) {
            this.detalles.add(det);
            return true;
        }
        return false;
    }

    public float calcularTotalDetalle() {
        Double total = this.detalles.stream()
                .mapToDouble(det -> (det.getPrecioVta() * det.getCantidad()))
                .sum();
        return total.floatValue();
    }

    public boolean guardar() {
        PedidoDto pedidoDto = this.mapper.map(this, PedidoDto.class);
        return this.dao.insertar(pedidoDto);
    }

    public boolean modificar() {
        PedidoDto pedidoDto = this.mapper.map(this, PedidoDto.class);
        return this.dao.modificar(pedidoDto);
    }

    public boolean eliminar() {
        PedidoDto pedidoDto = this.mapper.map(this, PedidoDto.class);
        return this.dao.borrar(pedidoDto);
    }
private EstadoPedido estado = new EstadoNuevo(); // por defecto

public EstadoPedido getEstado() {
    return estado;
}

public void setEstado(EstadoPedido estado) {
    this.estado = estado;
}

public boolean puedeModificar() {
    return estado.puedeModificar();
}

public boolean puedeCancelar() {
    return estado.puedeCancelar();
}

public boolean puedeEmitirComprobante() {
    return estado.puedeEmitirComprobante();
}
}
