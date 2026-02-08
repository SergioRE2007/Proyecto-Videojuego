public class tabla {
    public static void mostrarTablero(char[][] tablero) {
    for (int i = 0; i < tablero.length; i++) {
        // Línea superior de bordes
        for (int j = 0; j < tablero[i].length; j++) {
            if (j == 0 && i == 0) {
                System.out.print("┌");
            } else if (j == 0 && i != 0) {
                System.out.print("├");
            }
            
            if (j == 0) {
                System.out.print("───");
            } else if (i == 0) {
                System.out.print("┬───");
            } else {
                System.out.print("┼───");
            }
            
            if (j == tablero[i].length - 1 && i == 0) {
                System.out.print("┐");
            } else if (j == tablero[i].length - 1 && i != 0) {
                System.out.print("┤");
            }
        }
        System.out.println();
        
        // Línea con contenido
        for (int j = 0; j < tablero[i].length; j++) {
            System.out.printf("│ %c ", tablero[i][j]);  // Char con espacio
            if (j == tablero[i].length - 1) {
                System.out.print("│");
            }
        }
        System.out.println();
        
        // Línea inferior (solo en última fila)
        if (i == tablero.length - 1) {
            for (int j = 0; j < tablero[i].length; j++) {
                if (j == 0) {
                    System.out.print("└");
                }
                if (j != tablero[i].length - 1) {
                    System.out.print("───┴");
                } else {
                    System.out.print("───┘");
                }
            }
            System.out.println();
        }
    }
}
}
