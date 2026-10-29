import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    private static void mostrarMenu() {
        System.out.println("\n===== CAFETERÍA =====");
        System.out.println("1. Tomar nuevo pedido");
        System.out.println("2. Entregar pedido");
        System.out.println("3. Ver pedidos en espera");
        System.out.println("4. Cancelar pedido (cliente no recogió)");
        System.out.println("5. Ver bitácora de actividades");
        System.out.println("6. Salir (cerrar caja)");
    }

    public static void main(String[] args) {
        Cafeteria cafeteria = new Cafeteria();
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        try {
            while (!salir) {
                mostrarMenu();
                int opcion = Entrada.leerEntero(scanner, "Elige una opción", 1, 6);

                switch (opcion) {
                    case 1:
                        cafeteria.levantarPedido(scanner);
                        break;
                    case 2:
                        if (!cafeteria.hayPendientes()) {
                            System.out.println("No hay pedidos en espera.");
                        } else {
                            cafeteria.mostrarPendientes();
                            String nombre = Entrada.leerTexto(scanner, "Nombre del cliente a llamar", 30);
                            cafeteria.entregarPedido(nombre);
                        }
                        break;
                    case 3:
                        cafeteria.mostrarPendientes();
                        break;
                    case 4:
                        if (!cafeteria.hayPendientes()) {
                            System.out.println("No hay pedidos en espera.");
                        } else {
                            cafeteria.mostrarPendientes();
                            String nombre = Entrada.leerTexto(scanner, "Nombre del cliente que no recogió", 30);
                            cafeteria.cancelarPedido(scanner, nombre);
                        }
                        break;
                    case 5:
                        cafeteria.mostrarBitacora();
                        break;
                    case 6:
                        salir = cafeteria.cerrarCaja();
                        break;
                    default:
                        break; // imposible: leerEntero ya limita 1-6
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nSe terminó la entrada de datos. Cerrando programa.");
        } finally {
            scanner.close();
        }
    }
}