
public class App {

    public static void main(String[] args) throws Exception {

        final int filas = 20;
        final int columnas = 60;
        final int NUM_ENEMIGO = 40;
        final int NUM_ALIADO = 40;
        final int NUM_MURO = 50;

        // 🎲 Crear tablero
        Entidad[][] tablero = new Entidad[filas][columnas];

        // 🎯 Crear entidades (todas son Entidad)
        tablero = rellenaEnemigo(NUM_ENEMIGO, filas, columnas, tablero);
        tablero = rellenaAliado(NUM_ALIADO, filas, columnas, tablero);
        tablero = rellenaMuro(NUM_MURO, filas, columnas, tablero);
        char[][] tabla = rellenabi(filas, columnas, tablero);
        mostrarTableroSimple(tabla);
    }

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
                System.out.print(" " + tablero[i][j] + " ");
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

}
