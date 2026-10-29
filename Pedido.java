import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Pedido {

    private final int numero;
    private final String nombreCliente;
    private final List<ItemPedido> items;
    private final String descripcionPedido;
    private final BigDecimal totalCobrado;
    private EstadoPedido estado = EstadoPedido.EN_ESPERA;

    public Pedido(int numero, String nombreCliente, List<ItemPedido> items) {
        this.numero = numero;
        this.nombreCliente = nombreCliente;
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
        this.descripcionPedido = items.stream()
                .map(ItemPedido::getNombre)
                .collect(Collectors.joining(", "));
        this.totalCobrado = items.stream()
                .map(ItemPedido::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getNumero() {
        return numero;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getDescripcionPedido() {
        return descripcionPedido;
    }

    public BigDecimal getTotalCobrado() {
        return totalCobrado;
    }

    public List<ItemPedido> getItems() {
        return items;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    /** Ticket detallado. */
    public String generarTicket() {
        String linea = "-----------------------------------------";
        StringBuilder sb = new StringBuilder();
        sb.append(linea).append(System.lineSeparator());
        sb.append(String.format("PEDIDO #%d   Cliente: %s%n", numero, nombreCliente));
        sb.append(linea).append(System.lineSeparator());
        for (ItemPedido item : items) {
            sb.append(String.format("  %-28s $%8.2f%n", item.getNombre(), item.getPrecio()));
        }
        sb.append(linea).append(System.lineSeparator());
        sb.append(String.format("  %-28s $%8.2f%n", "TOTAL", totalCobrado));
        sb.append(linea);
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("#%d | %-15s | %s | $%.2f | %s",
                numero, nombreCliente, descripcionPedido, totalCobrado, estado);
    }
}