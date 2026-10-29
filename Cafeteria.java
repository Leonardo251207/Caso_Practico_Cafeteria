import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Cafeteria {

    private static final int MAX_NOMBRE_CLIENTE = 30;
    private static final int MAX_NOMBRE_PRODUCTO = 28;
    private static final int MAX_PRODUCTOS = 20;
    private static final BigDecimal MAX_PRECIO = new BigDecimal("5000.00");
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final LinkedList<Pedido> pedidosEnEspera = new LinkedList<>();
    private final List<Pedido> historial = new ArrayList<>();
    private final List<String> bitacora = new ArrayList<>();
    private int siguienteNumero = 1;

    public Cafeteria() {
        registrar("CAJA ABIERTA");
    }

    public boolean hayPendientes() {
        return !pedidosEnEspera.isEmpty();
    }

    /**
     * Captura un pedido con varios productos (nombre + precio de cada uno),
     * muestra el ticket, pide confirmación y lo agrega al final de la lista.
     */
    public void levantarPedido(Scanner scanner) {
        String cliente = Entrada.leerTexto(scanner, "Nombre del cliente", MAX_NOMBRE_CLIENTE);
        int cantidad = Entrada.leerEntero(scanner, "¿Cuántos productos va a pedir?", 1, MAX_PRODUCTOS);

        List<ItemPedido> items = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++) {
            System.out.println("Producto " + i + " de " + cantidad);
            String producto = Entrada.leerTexto(scanner, "  Nombre del producto", MAX_NOMBRE_PRODUCTO);
            BigDecimal precio = Entrada.leerPrecio(scanner, "  Precio", MAX_PRECIO);
            items.add(new ItemPedido(producto, precio));
        }

        Pedido pedido = new Pedido(siguienteNumero, cliente, items);
        System.out.println();
        System.out.println(pedido.generarTicket());

        if (!Entrada.confirmar(scanner, "¿Confirmar y cobrar?")) {
            System.out.println("Pedido descartado, no se cobró nada.");
            registrar("PEDIDO DESCARTADO antes de cobrar (cliente: " + cliente + ")");
            return;
        }

        siguienteNumero++;
        pedidosEnEspera.addLast(pedido); // al final: ya está cobrado y entra a la espera
        historial.add(pedido);
        registrar(String.format("PEDIDO #%d COBRADO - %s - %s - $%.2f",
                pedido.getNumero(), cliente, pedido.getDescripcionPedido(), pedido.getTotalCobrado()));
        System.out.println("Cobrado $" + pedido.getTotalCobrado() + ". Pedido en espera.");
    }

    /**
     * Anuncia por "bocina" el pedido del cliente y lo quita de la lista,
     * sin importar en qué posición estuviera.
     */
    public void entregarPedido(String nombreCliente) {
        Pedido pedido = buscarPorNombre(nombreCliente);
        if (pedido == null) {
            System.out.println("No se encontró ningún pedido pendiente a nombre de \"" + nombreCliente + "\".");
            return;
        }
        System.out.println("\n*** BOCINA: \"¡" + pedido.getNombreCliente()
                + ", tu pedido está listo! (" + pedido.getDescripcionPedido() + ")\" ***\n");
        pedidosEnEspera.remove(pedido);
        pedido.setEstado(EstadoPedido.ENTREGADO);
        registrar("PEDIDO #" + pedido.getNumero() + " ENTREGADO - " + pedido.getNombreCliente());
    }

    /**
     * Cambia el estado a CANCELADO cuando el cliente ya no va a recoger.
     * Pide confirmación para evitar errores.
     */
    public void cancelarPedido(Scanner scanner, String nombreCliente) {
        Pedido pedido = buscarPorNombre(nombreCliente);
        if (pedido == null) {
            System.out.println("No se encontró ningún pedido pendiente a nombre de \"" + nombreCliente + "\".");
            return;
        }
        System.out.println(pedido.generarTicket());
        if (!Entrada.confirmar(scanner, "¿Marcar como NO RECOGIDO y retirarlo de la espera?")) {
            System.out.println("Operación cancelada, el pedido sigue en espera.");
            return;
        }
        pedidosEnEspera.remove(pedido);
        pedido.setEstado(EstadoPedido.CANCELADO);
        registrar("PEDIDO #" + pedido.getNumero() + " CANCELADO (cliente no recogió) - " + pedido.getNombreCliente());
        System.out.println("Pedido #" + pedido.getNumero() + " marcado como no recogido.");
    }

    /** Muestra los pedidos en espera, en orden de llegada. */
    public void mostrarPendientes() {
        if (pedidosEnEspera.isEmpty()) {
            System.out.println("No hay pedidos en espera.");
            return;
        }
        System.out.println("--- Pedidos en espera (" + pedidosEnEspera.size() + ") ---");
        int i = 1;
        for (Pedido p : pedidosEnEspera) {
            System.out.println(i++ + ". " + p);
        }
    }

    /** Muestra la bitácora cronológica y un resumen por estado. */
    public void mostrarBitacora() {
        String linea = "=============================================";
        System.out.println(linea);
        System.out.println("      BITÁCORA DE ACTIVIDADES");
        System.out.println(linea);
        for (String evento : bitacora) {
            System.out.println(evento);
        }
        System.out.println("---------------------------------------------");
        System.out.println("RESUMEN");
        System.out.println("  Pedidos cobrados:   " + historial.size());
        System.out.println("  En espera:          " + contar(EstadoPedido.EN_ESPERA));
        System.out.printf("  Entregados:         %d  ($%.2f)%n",
                contar(EstadoPedido.ENTREGADO), sumar(EstadoPedido.ENTREGADO));
        // Sin monto: un pedido no recogido ya fue pagado, así que no es pérdida para la cafetería.
        System.out.printf("  Cancelados:         %d%n", contar(EstadoPedido.CANCELADO));
        System.out.println(linea);
    }

    /**
     * Intenta cerrar la caja. Si hay pedidos pendientes lo rechaza.
     * @return true si se pudo cerrar.
     */
    public boolean cerrarCaja() {
        if (!pedidosEnEspera.isEmpty()) {
            System.out.println("\nNo se puede cerrar la caja: hay " + pedidosEnEspera.size()
                    + " pedido(s) pendiente(s).");
            System.out.println("Entrega o cancela estos pedidos primero:");
            mostrarPendientes();
            registrar("INTENTO DE CIERRE RECHAZADO (" + pedidosEnEspera.size() + " pendientes)");
            return false;
        }
        registrar("CAJA CERRADA");
        System.out.println("\nCorte de caja:");
        mostrarBitacora();
        System.out.println("¡Hasta mañana!");
        return true;
    }

    /** Primer pedido en espera (el más antiguo) con ese nombre, sin distinguir mayúsculas. */
    private Pedido buscarPorNombre(String nombre) {
        String buscado = nombre.trim();
        Iterator<Pedido> it = pedidosEnEspera.iterator();
        while (it.hasNext()) {
            Pedido p = it.next();
            if (p.getNombreCliente().equalsIgnoreCase(buscado)) {
                return p;
            }
        }
        return null;
    }

    private void registrar(String mensaje) {
        bitacora.add("[" + LocalDateTime.now().format(FORMATO) + "] " + mensaje);
    }

    private long contar(EstadoPedido estado) {
        return historial.stream().filter(p -> p.getEstado() == estado).count();
    }

    private BigDecimal sumar(EstadoPedido estado) {
        return historial.stream()
                .filter(p -> p.getEstado() == estado)
                .map(Pedido::getTotalCobrado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}