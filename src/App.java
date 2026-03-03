import java.util.ArrayList;
import java.util.List;

import entidades.*;
import objetos.*;
import utils.*;

public class App {
    public static void main(String[] args) throws Exception {
        final int FILAS = 20;
        final int COLUMNAS = 20;
        final int NUM_ENEMIGO = 100;
        final int NUM_ALIADO = 1;
        final int NUM_MURO = 60;
        final int PROB_PEGAR_MURO = 70;
        final int NUM_ESCUDO = 5;
        final int NUM_ARMA = 3;
        final int NUM_ESTRELLA = 2;
        final int TURNOS_SPAWN_OBJETO = 150;
        final int VIDA_ALIADO = 5000;
        final int VIDA_ENEMIGO = 100;
        final int DANIO_ENEMIGO_MIN = 20;
        final int DANIO_ENEMIGO_MAX = 30;
        final int DANIO_BASE_ALIADO_MIN = 30;
        final int DANIO_BASE_ALIADO_MAX = 50;
        final int VISION_ALIADO = 5;
        final int VISION_ENEMIGO = 5;

        Entidad[][] tablero = new Entidad[FILAS][COLUMNAS];
        Objeto[][] objetos = new Objeto[FILAS][COLUMNAS];

        colocarBordes(FILAS, COLUMNAS, tablero);
        generarMuros(NUM_MURO, PROB_PEGAR_MURO, FILAS, COLUMNAS, tablero);
        tablero = rellenaEnemigo(NUM_ENEMIGO, FILAS, COLUMNAS, tablero, VIDA_ENEMIGO, DANIO_ENEMIGO_MIN, DANIO_ENEMIGO_MAX, VISION_ENEMIGO);
        tablero = rellenaAliado(NUM_ALIADO, FILAS, COLUMNAS, tablero, VIDA_ALIADO, DANIO_BASE_ALIADO_MIN, DANIO_BASE_ALIADO_MAX, VISION_ALIADO);
        generarObjetos(NUM_ESCUDO, NUM_ARMA, NUM_ESTRELLA, FILAS, COLUMNAS, tablero, objetos);

        // Ocultar cursor y limpiar pantalla al inicio
        System.out.print("\033[?25l\033[H\033[2J");
        System.out.flush();

        int turno = 0;

        while (true) {
            try {
                turno++;

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
                    e.actuar(tablero, objetos);
                }

                // Recogida de objetos por aliados
                for (int f = 0; f < FILAS; f++) {
                    for (int c = 0; c < COLUMNAS; c++) {
                        if (tablero[f][c] instanceof Aliado && objetos[f][c] != null) {
                            objetos[f][c].aplicar((Aliado) tablero[f][c]);
                            objetos[f][c] = null;
                        }
                    }
                }

                // Eliminar entidades muertas del tablero
                for (int f = 0; f < FILAS; f++) {
                    for (int c = 0; c < COLUMNAS; c++) {
                        Entidad e = tablero[f][c];
                        if (e != null && !e.estaVivo() && !(e instanceof Muro)) {
                            tablero[f][c] = null;
                        }
                    }
                }

                // Spawn de objeto aleatorio cada N turnos
                if (turno % TURNOS_SPAWN_OBJETO == 0) {
                    spawnObjetoRandom(FILAS, COLUMNAS, tablero, objetos);
                }

                // Contar entidades
                int numAliados = 0;
                int numEnemigos = 0;
                for (int f = 0; f < FILAS; f++) {
                    for (int c = 0; c < COLUMNAS; c++) {
                        Entidad e = tablero[f][c];
                        if (e instanceof Aliado) numAliados++;
                        else if (e instanceof Enemigo) numEnemigos++;
                    }
                }

                limpiarPantalla();
                mostrarTablero(FILAS, COLUMNAS, tablero, objetos, turno);

                // Mostrar contador
                System.out.println("\033[K");
                System.out.println(VERDE + " Aliados: " + numAliados + RESET
                        + "  |  " + ROJO + "Enemigos: " + numEnemigos + RESET + "\033[K");

                // Comprobar fin de partida
                if (numAliados == 0) {
                    System.out.println(ROJO + " Los enemigos han ganado! Todos los aliados han caido." + RESET);
                    break;
                }
                if (numEnemigos == 0) {
                    System.out.println(VERDE + " Los aliados han sobrevivido! Todos los enemigos han sido eliminados." + RESET);
                    break;
                }

                Thread.sleep(1);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        // Restaurar cursor al salir
        System.out.print("\033[?25h");
        System.out.flush();
    }

    public static final String RESET = "\u001B[0m";
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String AZUL = "\u001B[34m";
    // Colores para efecto estrella (ciclo rainbow)
    private static final String[] COLORES_ESTRELLA = {
        "\u001B[1;31m", // rojo brillante
        "\u001B[1;33m", // amarillo brillante
        "\u001B[1;32m", // verde brillante
        "\u001B[1;36m", // cyan brillante
        "\u001B[1;35m", // magenta brillante
        "\u001B[1;37m", // blanco brillante
    };

    public static void mostrarTablero(int filas, int columnas, Entidad[][] tablero, Objeto[][] objetos, int turno) {
        StringBuilder sb = new StringBuilder(filas * columnas * 15);
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                Entidad e = tablero[i][j];
                if (e != null) {
                    char c = e.getSimbolo();
                    if (c == 'X') {
                        sb.append(ROJO).append(" # ").append(RESET);
                    } else if (c == 'A') {
                        if (e instanceof Aliado && ((Aliado) e).getTurnosInvencible() > 0) {
                            // Efecto estrella: ciclar colores cada turno
                            String color = COLORES_ESTRELLA[turno % COLORES_ESTRELLA.length];
                            sb.append(color).append(" o ").append(RESET);
                        } else {
                            sb.append(VERDE).append(" o ").append(RESET);
                        }
                    } else if (c == 'M') {
                        sb.append(AMARILLO).append("[=]").append(RESET);
                    } else {
                        sb.append(" . ");
                    }
                } else if (objetos[i][j] != null) {
                    char s = objetos[i][j].getSimbolo();
                    if (s == 'S') {
                        sb.append(CYAN).append(" S ").append(RESET);
                    } else if (s == 'W') {
                        sb.append(MAGENTA).append(" W ").append(RESET);
                    } else if (s == '*') {
                        sb.append("\u001B[1;33m").append(" * ").append(RESET);
                    } else {
                        sb.append(" ").append(s).append(" ");
                    }
                } else {
                    sb.append(" . ");
                }
            }
            sb.append("\033[K\n");
        }
        System.out.print(sb);
        System.out.flush();
    }

    public static void generarObjetos(int numEscudo, int numArma, int numEstrella,
                                       int filas, int columnas, Entidad[][] tablero, Objeto[][] objetos) {
        colocarObjetosTipo(numEscudo, "escudo", filas, columnas, tablero, objetos);
        colocarObjetosTipo(numArma, "arma", filas, columnas, tablero, objetos);
        colocarObjetosTipo(numEstrella, "estrella", filas, columnas, tablero, objetos);
    }

    private static void colocarObjetosTipo(int cantidad, String tipo, int filas, int columnas,
                                            Entidad[][] tablero, Objeto[][] objetos) {
        int colocados = 0;
        while (colocados < cantidad) {
            int f = (int) (Math.random() * filas);
            int c = (int) (Math.random() * columnas);
            if (tablero[f][c] == null && objetos[f][c] == null) {
                Posicion pos = new Posicion(f, c);
                switch (tipo) {
                    case "escudo":
                        objetos[f][c] = new Escudo(pos);
                        break;
                    case "arma":
                        objetos[f][c] = new Arma(pos);
                        break;
                    case "estrella":
                        objetos[f][c] = new Estrella(pos);
                        break;
                }
                colocados++;
            }
        }
    }

    public static void spawnObjetoRandom(int filas, int columnas, Entidad[][] tablero, Objeto[][] objetos) {
        int intentos = 0;
        while (intentos < 100) {
            int f = (int) (Math.random() * filas);
            int c = (int) (Math.random() * columnas);
            if (tablero[f][c] == null && objetos[f][c] == null) {
                Posicion pos = new Posicion(f, c);
                double r = Math.random();
                if (r < 0.5) {
                    objetos[f][c] = new Escudo(pos);
                } else if (r < 0.8) {
                    objetos[f][c] = new Arma(pos);
                } else {
                    objetos[f][c] = new Estrella(pos);
                }
                return;
            }
            intentos++;
        }
    }

    public static Entidad[][] rellenaEnemigo(int num_enemigos, int filas, int columnas, Entidad[][] tabla,
                                              int vida, int danioMin, int danioMax, int vision) {
        for (int i = 0; i < num_enemigos; i++) {
            boolean esta = false;
            while (esta == false) {
                int auxfilas = (int) (Math.random() * filas);
                int auxcolumnas = (int) (Math.random() * columnas);
                if (tabla[auxfilas][auxcolumnas] == null) {
                    tabla[auxfilas][auxcolumnas] = new Enemigo(new Posicion(auxfilas, auxcolumnas), vida, danioMin, danioMax, vision);
                    esta = true;
                }
            }
        }
        return tabla;
    }

    public static Entidad[][] rellenaAliado(int num_Aliados, int filas, int columnas, Entidad[][] tabla,
                                             int vida, int danioBaseMin, int danioBaseMax, int vision) {
        for (int i = 0; i < num_Aliados; i++) {
            boolean esta = false;
            while (esta == false) {
                int auxfilas = (int) (Math.random() * filas);
                int auxcolumnas = (int) (Math.random() * columnas);
                if (tabla[auxfilas][auxcolumnas] == null) {
                    tabla[auxfilas][auxcolumnas] = new Aliado(new Posicion(auxfilas, auxcolumnas), vida, danioBaseMin, danioBaseMax, vision);
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
        double probCambioDir = 0.20;
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        int ultimaFila = -1, ultimaCol = -1;
        int dirActual = 0;
        int colocados = 0;

        while (colocados < numMuros) {
            if (ultimaFila != -1 && Math.random() < probPegar) {
                if (Math.random() < probCambioDir) {
                    dirActual = (int) (Math.random() * 4);
                }
                int fila = ultimaFila + dirs[dirActual][0];
                int col = ultimaCol + dirs[dirActual][1];
                if (fila > 0 && fila < filas - 1 && col > 0 && col < columnas - 1
                        && tabla[fila][col] == null) {
                    tabla[fila][col] = new Muro(new Posicion(fila, col));
                    ultimaFila = fila;
                    ultimaCol = col;
                    colocados++;
                } else {
                    dirActual = (int) (Math.random() * 4);
                }
            } else {
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
