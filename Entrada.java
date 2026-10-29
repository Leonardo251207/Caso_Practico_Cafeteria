import java.math.BigDecimal;
import java.util.Scanner;

public final class Entrada {

    private Entrada() {
    }

    public static String leerTexto(Scanner sc, String mensaje, int maxCaracteres) {
        while (true) {
            System.out.print(mensaje + ": ");
            String texto = sc.nextLine().trim();
            if (texto.isEmpty()) {
                System.out.println("  [!] No puede estar vacío.");
            } else if (texto.length() > maxCaracteres) {
                System.out.println("  [!] Máximo " + maxCaracteres + " caracteres.");
            } else {
                return texto;
            }
        }
    }

    public static int leerEntero(Scanner sc, String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje + " (" + min + "-" + max + "): ");
            String texto = sc.nextLine().trim();
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.println("  [!] Debe estar entre " + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Escribe un número entero válido.");
            }
        }
    }

    public static BigDecimal leerPrecio(Scanner sc, String mensaje, BigDecimal max) {
        while (true) {
            System.out.print(mensaje + ": $");
            String texto = sc.nextLine().trim().replace(',', '.');
            if (texto.startsWith("$")) {
                texto = texto.substring(1);
            }
            if (texto.startsWith("-")) {
                System.out.println("  [!] El precio no puede ser negativo.");
            } else if (!texto.matches("\\d+(\\.\\d{1,2})?")) {
                System.out.println("  [!] Precio inválido. Ejemplos: 45 o 45.50");
            } else {
                BigDecimal precio = new BigDecimal(texto).setScale(2);
                if (precio.signum() <= 0) {
                    System.out.println("  [!] El precio debe ser mayor que 0.");
                } else if (precio.compareTo(max) > 0) {
                    System.out.println("  [!] El precio máximo permitido es $" + max + ".");
                } else {
                    return precio;
                }
            }
        }
    }

    public static boolean confirmar(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje + " (s/n): ");
            String r = sc.nextLine().trim().toLowerCase();
            if (r.equals("s") || r.equals("si") || r.equals("sí")) {
                return true;
            }
            if (r.equals("n") || r.equals("no")) {
                return false;
            }
            System.out.println("  [!] Responde s o n.");
        }
    }
}