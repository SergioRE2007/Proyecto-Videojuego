public class App {

    public static void main(String[] args) throws Exception {


        
        final int filas = 30;
        final int columnas = 60;
        final int NUM_ENEMIGO = 40;
        final int NUM_ALIADO = 40;
        final int NUM_MURO = 300;

        Entidad[][] tablero = new Entidad[filas][columnas];

        tablero = rellenaEnemigo(NUM_ENEMIGO, filas, columnas, tablero);
        tablero = rellenaAliado(NUM_ALIADO, filas, columnas, tablero);
        tablero = rellenaMuro(NUM_MURO, filas, columnas, tablero);

        // 1) TABLERO INICIAL
        char[][] tabla = rellenabi(filas, columnas, tablero);
        System.out.println("TABLERO INICIAL");
        mostrarTableroSimple(tabla);

        // 2) UN TURNO: todos actúan
        while (true) {
    try {
        // Mover
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                Entidad e = tablero[f][c];
                if (e instanceof Enemigo) {
                    ((Enemigo) e).actuar(tablero);
                } else if (e instanceof Aliado) {
                    ((Aliado) e).actuar(tablero);
                }
            }
        }

        // Pintar
        limpiarPantalla();
        char[][] tabla2 = rellenabi(filas, columnas, tablero);
        mostrarTableroSimple(tabla2);

        Thread.sleep(60);

    } catch (InterruptedException ex) {
        ex.printStackTrace();
    }
}
}

public static final String RESET = "\u001B[0m";
public static final String ROJO  = "\u001B[31m";
public static final String VERDE = "\u001B[32m";
public static final String AMARILLO = "\u001B[33m";

public static char[][] rellenabi(int filas, int columnas, Entidad todos[][]) {
        char[][] array = new char[filas][columnas];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (todos[i][j] != null) {
                    array[i][j] = todos[i][j].getSimbolo(); // 'E', 'A', 'X'
                } else {
                    array[i][j] = '.'; // vacío
                }
            }
        }
        return array;
    }

public static void mostrarTableroSimple(char[][] tablero) {
    for (int i = 0; i < tablero.length; i++) {
        for (int j = 0; j < tablero[i].length; j++) {
            char c = tablero[i][j];

            String salida;
            if (c == 'X') {                 // enemigo
                salida = ROJO + " X " + RESET;
            } else if (c == 'A') {          // aliado
                salida = VERDE + " A " + RESET;
            } else if (c == 'M') {          // muro
                salida = AMARILLO + " M " + RESET;
            } else {
                salida = " . ";
            }

            System.out.print(salida);
        }
        System.out.println();
    }
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

    public static Entidad[][] rellenaMuro(int num_Muros, int filas, int columnas, Entidad[][] tabla) {
        for (int i = 0; i < num_Muros; i++) {
            boolean esta = false;
            while (esta == false) {
                int auxfilas = (int) (Math.random() * filas);
                int auxcolumnas = (int) (Math.random() * columnas);
                if (tabla[auxfilas][auxcolumnas] == null) {
                    tabla[auxfilas][auxcolumnas] = new Muro(new Posicion(auxfilas, auxcolumnas));
                    esta = true;
                }
            }
        }
        return tabla;
    }

    public static void limpiarPantalla() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    
}
