import java.math.BigDecimal;

/**
 * Un producto dentro de un pedido: nombre y precio.
 */
public class ItemPedido {

    private final String nombre;
    private final BigDecimal precio;

    public ItemPedido(String nombre, BigDecimal precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }
}
