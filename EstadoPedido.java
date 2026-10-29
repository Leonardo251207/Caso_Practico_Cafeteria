/**
 * Ciclo de vida de un pedido.
 */
public enum EstadoPedido {
    EN_ESPERA("En espera"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado (no recogido)");

    private final String etiqueta;

    EstadoPedido(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}