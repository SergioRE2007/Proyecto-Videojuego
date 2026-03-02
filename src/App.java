import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        final int FILAS = 20;
        final int COLUMNAS = 40;
        final int NUM_ENEMIGO = 20;
        final int NUM_ALIADO = 100;
        final int NUM_MURO = 60;
        final int PROB_PEGAR_MURO = 70; // % de que un muro se pegue a otro existente

        Entidad[][] tablero = new Entidad[FILAS][COLUMNAS];
        colocarBordes(FILAS, COLUMNAS, tablero);
        generarMuros(NUM_MURO, PROB_PEGAR_MURO, FILAS, COLUMNAS, tablero);
        tablero = rellenaEnemigo(NUM_ENEMIGO, FILAS, COLUMNAS, tablero);
        tablero = rellenaAliado(NUM_ALIADO, FILAS, COLUMNAS, tablero);

        // Ocultar cursor y limpiar pantalla al inicio
        System.out.print("\033[?25l\033[H\033[2J");
        System.out.flush();

        while (true) {
            try {
                // Recoger todas las entidades ANTES de moverlas
                List<Entidad> entidades = new ArrayList<>();
                for (int f = 0; f < FILAS; f++) {
                    for (int c = 0; c < COLUMNAS; c++) {
                        Entidad e = tablero[f][c];
                        if (e instanceof Enemigo || e instanceof Aliado) {
                            entidades.add(e);
                        }
                    }
                }

                for (Entidad e : entidades) {
                    // Comprobar que la entidad sigue viva en el tablero
                    Posicion p = e.getPosicion();
                    if (tablero[p.getFila()][p.getColumna()] != e) continue;

                    if (e instanceof Enemigo) {
                        ((Enemigo) e).actuar(tablero);
                    } else if (e instanceof Aliado) {
                        ((Aliado) e).actuar(tablero);
                    }
                }

                limpiarPantalla();
                char[][] tabla2 = rellenabi(FILAS, COLUMNAS, tablero);
                mostrarTableroSimple(tabla2);
                Thread.sleep(150);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static final String RESET = "\u001B[0m";
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";

    public static char[][] rellenabi(int filas, int columnas, Entidad todos[][]) {
        char[][] array = new char[filas][columnas];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (todos[i][j] != null) {
                    array[i][j] = todos[i][j].getSimbolo();
                } else {
                    array[i][j] = '.';
                }
            }
        }
        return array;
    }

    public static void mostrarTableroSimple(char[][] tablero) {
        // Construir todo el frame en memoria y mandarlo de golpe
        StringBuilder sb = new StringBuilder(tablero.length * tablero[0].length * 15);
        for (int i = 0; i < tablero.length; i++) {
            for (int j = 0; j < tablero[i].length; j++) {
                char c = tablero[i][j];
                if (c == 'X') {
                    sb.append(ROJO).append(" # ").append(RESET);
                } else if (c == 'A') {
                    sb.append(VERDE).append(" o ").append(RESET);
                } else if (c == 'M') {
                    sb.append(AMARILLO).append("[=]").append(RESET);
                } else {
                    sb.append(" . ");
                }
            }
            // Limpiar el resto de la linea por si la anterior era mas larga
            sb.append("\033[K\n");
        }
        System.out.print(sb);
        System.out.flush();
    }

    public static Entidad[][] rellenaEnemigo(int num_enemigos, int filas, int columnas, Entidad[][] tabla) {
        for (int i = 0; i < num_enemigos; i++) {
            boolean esta = false;
            while (esta == false) {
                int auxfilas = (int) (Math.random() * filas);
                int auxcolumnas = (int) (Math.random() * columnas);
                if (tabla[auxfilas][auxcolumnas] == null) {
                    tabla[auxfilas][auxcolumnas] = new Enemigo(new Posicion(auxfilas, auxcolumnas));
                    esta = true;
                }
            }
        }
        return tabla;
    }

    public static Entidad[][] rellenaAliado(int num_Aliados, int filas, int columnas, Entidad[][] tabla) {
        for (int i = 0; i < num_Aliados; i++) {
            boolean esta = false;
            while (esta == false) {
                int auxfilas = (int) (Math.random() * filas);
                int auxcolumnas = (int) (Math.random() * columnas);
                if (tabla[auxfilas][auxcolumnas] == null) {
                    tabla[auxfilas][auxcolumnas] = new Aliado(new Posicion(auxfilas, auxcolumnas));
                    esta = true;
                }
            }
        }
        return tabla;
    }

    public static void colocarBordes(int filas, int columnas, Entidad[][] tabla) {
        for (int c = 0; c < columnas; c++) {
            tabla[0][c] = new Muro(new Posicion(0, c));
            tabla[filas - 1][c] = new Muro(new Posicion(filas - 1, c));
        }
        for (int f = 1; f < filas - 1; f++) {
            tabla[f][0] = new Muro(new Posicion(f, 0));
            tabla[f][columnas - 1] = new Muro(new Posicion(f, columnas - 1));
        }
    }

    public static void generarMuros(int numMuros, int probPegarPct, int filas, int columnas, Entidad[][] tabla) {
        double probPegar = probPegarPct / 100.0;
        double probCambioDir = 0.20; // 20% de cambiar de direccion
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // arriba, abajo, izq, der

        // Ultimo muro colocado y su direccion actual
        int ultimaFila = -1, ultimaCol = -1;
        int dirActual = 0;
        int colocados = 0;

        while (colocados < numMuros) {
            if (ultimaFila != -1 && Math.random() < probPegar) {
                // 20% de cambiar direccion
                if (Math.random() < probCambioDir) {
                    dirActual = (int) (Math.random() * 4);
                }
                // Continuar en la direccion actual desde el ultimo muro
                int fila = ultimaFila + dirs[dirActual][0];
                int col = ultimaCol + dirs[dirActual][1];
                if (fila > 0 && fila < filas - 1 && col > 0 && col < columnas - 1
                        && tabla[fila][col] == null) {
                    tabla[fila][col] = new Muro(new Posicion(fila, col));
                    ultimaFila = fila;
                    ultimaCol = col;
                    colocados++;
                } else {
                    // Si no puede, cambiar direccion e intentar de nuevo
                    dirActual = (int) (Math.random() * 4);
                }
            } else {
                // Nueva semilla con direccion random
                int f = 1 + (int) (Math.random() * (filas - 2));
                int c = 1 + (int) (Math.random() * (columnas - 2));
                if (tabla[f][c] == null) {
                    tabla[f][c] = new Muro(new Posicion(f, c));
                    ultimaFila = f;
                    ultimaCol = c;
                    dirActual = (int) (Math.random() * 4);
                    colocados++;
                }
            }
        }
    }

    public static void limpiarPantalla() {
        // Solo mover cursor al inicio, NO borrar la pantalla
        System.out.print("\033[H");
    }

    public static Entidad[][] copiarTablero(Entidad[][] original, int FILAS, int COLUMNAS) {
        Entidad[][] copia = new Entidad[FILAS][COLUMNAS];
        for (int f = 0; f < FILAS; f++) {
            System.arraycopy(original[f], 0, copia[f], 0, COLUMNAS);
        }
        return copia;
    }
}
