
public class App {

    public static void main(String[] args) throws Exception {
        Enemigo[] enemigos = new Enemigo[10];
        aliado[] aliado = new aliado[20];
        roca[] roca = new roca[30];
        int filas = 10;
        int columnas = 40;
        for (int i = 0; i < enemigos.length; i++) {
            enemigos[i] = new Enemigo(new Posicion((int) (Math.random() * filas), (int) (Math.random() * columnas)));
        }
        for (int i = 0; i < aliado.length; i++) {
            aliado[i] = new aliado(new Posicion((int) (Math.random() * filas), (int) (Math.random() * columnas)));
        }
        for (int i = 0; i < roca.length; i++) {
            roca[i] = new roca(new Posicion((int) (Math.random() * filas), (int) (Math.random() * columnas)));
        }
        char[][] tabla = rellenabi(filas, columnas, enemigos, aliado, roca);
        mostrarTablero(tabla);
    }

    public static char[][] rellenabi(int filas, int columnas, Enemigo enemigos[], aliado aliados[], roca roca[]) {
        char[][] array = new char[filas][columnas];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                array[i][j] = ' '; // vacío
                boolean aux = false;
                for (int j2 = 0; j2 < enemigos.length && !aux; j2++) {
                    if (i == enemigos[j2].getPosicion().getFila() && j == enemigos[j2].getPosicion().getColumna()) {
                        array[i][j] = enemigos[j2].getSimbolo(); // 'E'
                        aux = true;
                    }
                }
                for (int j2 = 0; j2 < aliados.length && !aux; j2++) {
                    if (i == aliados[j2].getPosicion().getFila() && j == aliados[j2].getPosicion().getColumna()) {
                        array[i][j] = aliados[j2].getSimbolo(); // 'X'
                        aux = true;
                    }
                }
                for (int j2 = 0; j2 < roca.length && !aux; j2++) {
                    if (i == roca[j2].getPosicion().getFila() && j == roca[j2].getPosicion().getColumna()) {
                        array[i][j] = roca[j2].getSimbolo(); // 'H'
                        aux = true;
                    }
                }
            }
        }
        return array;
    }

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
                System.out.printf("│ %c ", tablero[i][j]); // Char con espacio
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
