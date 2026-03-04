import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entidades.*;
import objetos.*;
import utils.*;

public class App {
    public static void main(String[] args) throws Exception {
        // ==================== GENERAL ====================
        final long SEMILLA = -1;          // Semilla del RNG: -1 = aleatorio, cualquier otro valor = partida reproducible
        final int VELOCIDAD_MS = 100;     // Milisegundos entre frames (menor = más rápido)

        // ==================== MAPA ====================
        final int FILAS = 20;             // Alto del tablero en casillas (incluye bordes)
        final int COLUMNAS = 30;          // Ancho del tablero en casillas (incluye bordes)
        final String TIPO_MAPA = "arena"; // Tipo de generación: "abierto", "salas", "laberinto", "arena"
        final int NUM_MURO = 60;          // Cantidad de muros interiores (usado en "abierto" y "salas")
        final int PROB_PEGAR_MURO = 70;   // Probabilidad (%) de que un muro nuevo se pegue al anterior

        // ==================== ALIADOS ====================
        final int NUM_ALIADO = 10;            // Cantidad de aliados al inicio
        final int VIDA_ALIADO = 100;          // Puntos de vida de cada aliado
        final int DANIO_BASE_ALIADO_MIN = 30; // Daño mínimo del contraataque del aliado
        final int DANIO_BASE_ALIADO_MAX = 50; // Daño máximo del contraataque del aliado
        final int VISION_ALIADO = 5;          // Casillas de visión del aliado (detección de enemigos y objetos)

        // ==================== ENEMIGO NORMAL ====================
        final int NUM_ENEMIGO = 1;        // Cantidad de enemigos normales al inicio
        final int VIDA_ENEMIGO = 1000;    // Puntos de vida del enemigo normal
        final int DANIO_ENEMIGO_MIN = 50; // Daño mínimo por ataque
        final int DANIO_ENEMIGO_MAX = 100;// Daño máximo por ataque
        final int VISION_ENEMIGO = 5;     // Casillas de visión (persigue aliados dentro de este radio)

        // ==================== ENEMIGO TANQUE ====================
        final int NUM_ENEMIGO_TANQUE = 1;  // Cantidad de tanques al inicio
        final int VIDA_TANQUE = 3000;      // Puntos de vida (muy resistente)
        final int DANIO_TANQUE_MIN = 100;  // Daño mínimo por ataque (golpe fuerte)
        final int DANIO_TANQUE_MAX = 200;  // Daño máximo por ataque
        final int VISION_TANQUE = 10;      // Casillas de visión (detecta desde lejos)

        // ==================== ENEMIGO RÁPIDO ====================
        final int NUM_ENEMIGO_RAPIDO = 2;  // Cantidad de enemigos rápidos al inicio
        final int VIDA_RAPIDO = 500;       // Puntos de vida (frágil pero veloz)
        final int DANIO_RAPIDO_MIN = 20;   // Daño mínimo por ataque (débil)
        final int DANIO_RAPIDO_MAX = 40;   // Daño máximo por ataque
        final int VISION_RAPIDO = 7;       // Casillas de visión

        // ==================== TRAMPAS ====================
        final int NUM_TRAMPA = 15;         // Cantidad de trampas ocultas en el mapa
        final int DANIO_TRAMPA = 80;       // Daño que inflige cada turno al pisar una trampa

        // ==================== OBJETOS ====================
        final int TURNOS_SPAWN_OBJETO = 150; // Cada cuántos turnos aparece un objeto aleatorio nuevo
        final int NUM_ESCUDO = 5;            // Escudos iniciales (absorben daño antes que la vida)
        final int NUM_ARMA = 3;              // Armas iniciales (aumentan el daño de contraataque)
        final int NUM_ESTRELLA = 0;          // Estrellas iniciales (invencibilidad temporal + persigue enemigos)
        final int NUM_VELOCIDAD = 3;         // Potenciadores de velocidad iniciales (2 movimientos por turno)
        final int DURACION_VELOCIDAD = 30;   // Turnos que dura el efecto de velocidad
        final int NUM_POCION = 3;            // Pociones de curación iniciales
        final int CURACION_POCION = 50;      // Puntos de vida que restaura cada poción

        // ==========================================================
        Rng.rng = (SEMILLA == -1) ? new Random() : new Random(SEMILLA);

        Entidad[][] tablero = new Entidad[FILAS][COLUMNAS];
        Objeto[][] objetos = new Objeto[FILAS][COLUMNAS];
        Trampa[][] trampas = new Trampa[FILAS][COLUMNAS];

        colocarBordes(FILAS, COLUMNAS, tablero);
        generarMapa(TIPO_MAPA, NUM_MURO, PROB_PEGAR_MURO, FILAS, COLUMNAS, tablero);
        tablero = rellenaEnemigo(NUM_ENEMIGO, FILAS, COLUMNAS, tablero, VIDA_ENEMIGO, DANIO_ENEMIGO_MIN, DANIO_ENEMIGO_MAX, VISION_ENEMIGO);
        rellenaEnemigoTipo(NUM_ENEMIGO_TANQUE, "tanque", FILAS, COLUMNAS, tablero, VIDA_TANQUE, DANIO_TANQUE_MIN, DANIO_TANQUE_MAX, VISION_TANQUE);
        rellenaEnemigoTipo(NUM_ENEMIGO_RAPIDO, "rapido", FILAS, COLUMNAS, tablero, VIDA_RAPIDO, DANIO_RAPIDO_MIN, DANIO_RAPIDO_MAX, VISION_RAPIDO);
        tablero = rellenaAliado(NUM_ALIADO, FILAS, COLUMNAS, tablero, VIDA_ALIADO, DANIO_BASE_ALIADO_MIN, DANIO_BASE_ALIADO_MAX, VISION_ALIADO);
        generarObjetos(NUM_ESCUDO, NUM_ARMA, NUM_ESTRELLA, NUM_VELOCIDAD, DURACION_VELOCIDAD, NUM_POCION, CURACION_POCION, FILAS, COLUMNAS, tablero, objetos);
        generarTrampas(NUM_TRAMPA, DANIO_TRAMPA, FILAS, COLUMNAS, tablero, objetos, trampas);

        // Setear trampas a los aliados para que las esquiven
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                if (tablero[f][c] instanceof Aliado) {
                    tablero[f][c].setTrampas(trampas);
                }
            }
        }

        // Ocultar cursor y limpiar pantalla al inicio
        System.out.print("\033[?25l\033[H\033[2J");
        System.out.flush();

        int turno = 0;
        int enemigosEliminados = 0;
        int objetosRecogidos = 0;
        long tiempoInicio = System.currentTimeMillis();

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

                // Daño por trampas
                for (int f = 0; f < FILAS; f++) {
                    for (int c = 0; c < COLUMNAS; c++) {
                        if (tablero[f][c] != null && trampas[f][c] != null
                                && !(tablero[f][c] instanceof Muro)) {
                            tablero[f][c].recibirDanio(trampas[f][c].getDanio());
                        }
                    }
                }

                // Recogida de objetos por aliados
                for (int f = 0; f < FILAS; f++) {
                    for (int c = 0; c < COLUMNAS; c++) {
                        if (tablero[f][c] instanceof Aliado && objetos[f][c] != null) {
                            objetos[f][c].aplicar((Aliado) tablero[f][c]);
                            objetos[f][c] = null;
                            objetosRecogidos++;
                        }
                    }
                }

                // Eliminar entidades muertas del tablero
                for (int f = 0; f < FILAS; f++) {
                    for (int c = 0; c < COLUMNAS; c++) {
                        Entidad e = tablero[f][c];
                        if (e != null && !e.estaVivo() && !(e instanceof Muro)) {
                            if (e instanceof Enemigo) enemigosEliminados++;
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
                mostrarTablero(FILAS, COLUMNAS, tablero, objetos, trampas, turno);

                // HUD de estadísticas
                long tiempoMs = System.currentTimeMillis() - tiempoInicio;
                int segundosTotales = (int) (tiempoMs / 1000);
                int minutos = segundosTotales / 60;
                int segundos = segundosTotales % 60;
                String tiempo = String.format("%02d:%02d", minutos, segundos);

                String colorAliados = numAliados <= 2 ? ROJO : VERDE;

                int anchoHud = COLUMNAS * 3;
                String lineaH = "═".repeat(anchoHud - 2);

                String linea1 = String.format("  Turno: %d  │  Tiempo: %s  │  Velocidad: %dms",
                        turno, tiempo, VELOCIDAD_MS);
                String linea2 = String.format("  Aliados: %s%d/%d%s  │  Enemigos: %s%d%s  │  Eliminados: %s%d%s  │  Objetos: %s%d%s",
                        colorAliados, numAliados, NUM_ALIADO, RESET,
                        ROJO, numEnemigos, RESET,
                        VERDE, enemigosEliminados, RESET,
                        CYAN, objetosRecogidos, RESET);

                String leyenda = "  " + VERDE + "o" + RESET + "=Aliado  "
                        + ROJO + "#" + RESET + "=Enemigo  "
                        + "\u001B[1;31m" + "T" + RESET + "=Tanque  "
                        + AMARILLO + "¤" + RESET + "=Rápido  "
                        + AMARILLO + "[=]" + RESET + "=Muro  "
                        + "\u001B[37m" + "^" + RESET + "=Trampa  "
                        + CYAN + "S" + RESET + "=Escudo  "
                        + MAGENTA + "W" + RESET + "=Arma  "
                        + "\u001B[1;33m" + "*" + RESET + "=Estrella  "
                        + AZUL + "V" + RESET + "=Vel  "
                        + "\u001B[1;32m" + "+" + RESET + "=Poción";

                System.out.println("╔" + lineaH + "╗\033[K");
                System.out.println("║" + linea1 + espacios(anchoHud - 2 - longitudVisible(linea1)) + "║\033[K");
                System.out.println("║" + linea2 + espacios(anchoHud - 2 - longitudVisible(linea2)) + "║\033[K");
                System.out.println("║" + leyenda + espacios(anchoHud - 2 - longitudVisible(leyenda)) + "║\033[K");
                System.out.println("╚" + lineaH + "╝\033[K");

                // Comprobar fin de partida
                if (numAliados == 0 && numEnemigos == 0) {
                    System.out.println(AMARILLO + " Empate! Ambos bandos han caido al mismo tiempo." + RESET);
                    break;
                }
                if (numAliados == 0) {
                    System.out.println(ROJO + " Los enemigos han ganado! Todos los aliados han caido." + RESET);
                    break;
                }
                if (numEnemigos == 0) {
                    System.out.println(VERDE + " Los aliados han sobrevivido! Todos los enemigos han sido eliminados." + RESET);
                    break;
                }

                Thread.sleep(VELOCIDAD_MS);
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

    public static void mostrarTablero(int filas, int columnas, Entidad[][] tablero, Objeto[][] objetos, Trampa[][] trampas, int turno) {
        StringBuilder sb = new StringBuilder(filas * columnas * 15);
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                Entidad e = tablero[i][j];
                if (e != null) {
                    char c = e.getSimbolo();
                    if (c == 'T') {
                        sb.append("\u001B[1;31m").append(" T ").append(RESET);
                    } else if (c == 'R') {
                        sb.append(AMARILLO).append(" ¤ ").append(RESET);
                    } else if (c == 'X') {
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
                    } else if (s == 'V') {
                        sb.append(AZUL).append(" V ").append(RESET);
                    } else if (s == '*') {
                        sb.append("\u001B[1;33m").append(" * ").append(RESET);
                    } else if (s == '+') {
                        sb.append("\u001B[1;32m").append(" + ").append(RESET);
                    } else {
                        sb.append(" ").append(s).append(" ");
                    }
                } else if (trampas[i][j] != null) {
                    sb.append("\u001B[37m").append(" ^ ").append(RESET);
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
                                       int numVelocidad, int duracionVelocidad,
                                       int numPocion, int curacionPocion,
                                       int filas, int columnas, Entidad[][] tablero, Objeto[][] objetos) {
        colocarObjetosTipo(numEscudo, "escudo", 0, filas, columnas, tablero, objetos);
        colocarObjetosTipo(numArma, "arma", 0, filas, columnas, tablero, objetos);
        colocarObjetosTipo(numEstrella, "estrella", 0, filas, columnas, tablero, objetos);
        colocarObjetosTipo(numVelocidad, "velocidad", duracionVelocidad, filas, columnas, tablero, objetos);
        colocarObjetosTipo(numPocion, "pocion", curacionPocion, filas, columnas, tablero, objetos);
    }

    private static void colocarObjetosTipo(int cantidad, String tipo, int param, int filas, int columnas,
                                            Entidad[][] tablero, Objeto[][] objetos) {
        int colocados = 0;
        while (colocados < cantidad) {
            int f = Rng.rng.nextInt(filas);
            int c = Rng.rng.nextInt(columnas);
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
                    case "velocidad":
                        objetos[f][c] = new Velocidad(pos, param);
                        break;
                    case "pocion":
                        objetos[f][c] = new Pocion(pos, param);
                        break;
                }
                colocados++;
            }
        }
    }

    public static void spawnObjetoRandom(int filas, int columnas, Entidad[][] tablero, Objeto[][] objetos) {
        int intentos = 0;
        while (intentos < 100) {
            int f = Rng.rng.nextInt(filas);
            int c = Rng.rng.nextInt(columnas);
            if (tablero[f][c] == null && objetos[f][c] == null) {
                Posicion pos = new Posicion(f, c);
                double r = Rng.rng.nextDouble();
                if (r < 0.30) {
                    objetos[f][c] = new Escudo(pos);
                } else if (r < 0.50) {
                    objetos[f][c] = new Arma(pos);
                } else if (r < 0.70) {
                    objetos[f][c] = new Velocidad(pos, 30);
                } else if (r < 0.85) {
                    objetos[f][c] = new Pocion(pos, 50);
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
                int auxfilas = Rng.rng.nextInt(filas);
                int auxcolumnas = Rng.rng.nextInt(columnas);
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
                int auxfilas = Rng.rng.nextInt(filas);
                int auxcolumnas = Rng.rng.nextInt(columnas);
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

    public static void generarMapa(String tipo, int numMuros, int probPegarPct, int filas, int columnas, Entidad[][] tabla) {
        switch (tipo) {
            case "abierto":
                generarMuros(numMuros, probPegarPct, filas, columnas, tabla);
                break;
            case "salas":
                generarMapaSalas(filas, columnas, tabla, numMuros);
                break;
            case "laberinto":
                generarMapaLaberinto(filas, columnas, tabla);
                break;
            case "arena":
                generarMapaArena(filas, columnas, tabla);
                break;
            default:
                generarMuros(numMuros, probPegarPct, filas, columnas, tabla);
                break;
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
            if (ultimaFila != -1 && Rng.rng.nextDouble() < probPegar) {
                if (Rng.rng.nextDouble() < probCambioDir) {
                    dirActual = Rng.rng.nextInt(4);
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
                    dirActual = Rng.rng.nextInt(4);
                }
            } else {
                int f = 1 + Rng.rng.nextInt(filas - 2);
                int c = 1 + Rng.rng.nextInt(columnas - 2);
                if (tabla[f][c] == null) {
                    tabla[f][c] = new Muro(new Posicion(f, c));
                    ultimaFila = f;
                    ultimaCol = c;
                    dirActual = Rng.rng.nextInt(4);
                    colocados++;
                }
            }
        }
    }

    public static void generarMapaSalas(int filas, int columnas, Entidad[][] tabla, int topeMuros) {
        int murosColocados = 0;
        int numSalas = 3 + Rng.rng.nextInt(4); // 3-6 salas

        for (int s = 0; s < numSalas && murosColocados < topeMuros; s++) {
            int anchoSala = 3 + Rng.rng.nextInt(5); // 3-7
            int altoSala = 3 + Rng.rng.nextInt(5);  // 3-7
            int inicioF = 1 + Rng.rng.nextInt(filas - 2 - altoSala);
            int inicioC = 1 + Rng.rng.nextInt(columnas - 2 - anchoSala);

            // Elegir 1-2 puertas en lados aleatorios
            int numPuertas = 1 + Rng.rng.nextInt(2);
            int[][] puertas = new int[numPuertas][2];
            for (int p = 0; p < numPuertas; p++) {
                int lado = Rng.rng.nextInt(4);
                switch (lado) {
                    case 0: // arriba
                        puertas[p] = new int[]{inicioF, inicioC + 1 + Rng.rng.nextInt(anchoSala - 2)};
                        break;
                    case 1: // abajo
                        puertas[p] = new int[]{inicioF + altoSala - 1, inicioC + 1 + Rng.rng.nextInt(anchoSala - 2)};
                        break;
                    case 2: // izquierda
                        puertas[p] = new int[]{inicioF + 1 + Rng.rng.nextInt(altoSala - 2), inicioC};
                        break;
                    case 3: // derecha
                        puertas[p] = new int[]{inicioF + 1 + Rng.rng.nextInt(altoSala - 2), inicioC + anchoSala - 1};
                        break;
                }
            }

            // Colocar paredes de la sala
            for (int f = inicioF; f < inicioF + altoSala && f < filas - 1; f++) {
                for (int c = inicioC; c < inicioC + anchoSala && c < columnas - 1; c++) {
                    // Solo paredes (borde de la sala)
                    boolean esBorde = (f == inicioF || f == inicioF + altoSala - 1 || c == inicioC || c == inicioC + anchoSala - 1);
                    if (!esBorde) continue;

                    // Comprobar si es puerta
                    boolean esPuerta = false;
                    for (int[] puerta : puertas) {
                        if (f == puerta[0] && c == puerta[1]) {
                            esPuerta = true;
                            break;
                        }
                    }
                    if (esPuerta) continue;

                    if (tabla[f][c] == null && murosColocados < topeMuros) {
                        tabla[f][c] = new Muro(new Posicion(f, c));
                        murosColocados++;
                    }
                }
            }
        }
    }

    public static void generarMapaLaberinto(int filas, int columnas, Entidad[][] tabla) {
        // Rellenar todo el interior con muros
        for (int f = 1; f < filas - 1; f++) {
            for (int c = 1; c < columnas - 1; c++) {
                if (tabla[f][c] == null) {
                    tabla[f][c] = new Muro(new Posicion(f, c));
                }
            }
        }

        // Dimensiones del grid de celdas (celdas en posiciones impares relativas al interior)
        int celdasF = (filas - 2) / 2;
        int celdasC = (columnas - 2) / 2;
        boolean[][] visitado = new boolean[celdasF][celdasC];
        int[][] pila = new int[celdasF * celdasC][2];
        int topePila = 0;

        // Empezar en celda (0,0)
        visitado[0][0] = true;
        pila[topePila++] = new int[]{0, 0};
        // Abrir la celda inicial
        int realF = 1 + 0 * 2;
        int realC = 1 + 0 * 2;
        tabla[realF][realC] = null;

        int[][] dirsCelda = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (topePila > 0) {
            int[] actual = pila[topePila - 1];
            int cf = actual[0], cc = actual[1];

            // Buscar vecinos no visitados
            int[] vecinosF = new int[4];
            int[] vecinosC = new int[4];
            int numVecinos = 0;
            for (int[] d : dirsCelda) {
                int nf = cf + d[0], nc = cc + d[1];
                if (nf >= 0 && nf < celdasF && nc >= 0 && nc < celdasC && !visitado[nf][nc]) {
                    vecinosF[numVecinos] = nf;
                    vecinosC[numVecinos] = nc;
                    numVecinos++;
                }
            }

            if (numVecinos == 0) {
                topePila--;
            } else {
                int idx = Rng.rng.nextInt(numVecinos);
                int nf = vecinosF[idx], nc = vecinosC[idx];
                visitado[nf][nc] = true;

                // Abrir celda destino
                int destF = 1 + nf * 2;
                int destC = 1 + nc * 2;
                tabla[destF][destC] = null;

                // Abrir pared entre celdas
                int paredF = 1 + cf * 2 + (nf - cf);
                int paredC = 1 + cc * 2 + (nc - cc);
                if (paredF > 0 && paredF < filas - 1 && paredC > 0 && paredC < columnas - 1) {
                    tabla[paredF][paredC] = null;
                }

                pila[topePila++] = new int[]{nf, nc};
            }
        }

        // Eliminar ~30% de muros restantes para abrir espacio
        for (int f = 1; f < filas - 1; f++) {
            for (int c = 1; c < columnas - 1; c++) {
                if (tabla[f][c] instanceof Muro && Rng.rng.nextDouble() < 0.30) {
                    tabla[f][c] = null;
                }
            }
        }
    }

    public static void generarMapaArena(int filas, int columnas, Entidad[][] tabla) {
        // Rectángulo centrado ~60% del tamaño
        int altoArena = (int) (filas * 0.6);
        int anchoArena = (int) (columnas * 0.6);
        int inicioF = (filas - altoArena) / 2;
        int inicioC = (columnas - anchoArena) / 2;
        int finF = inicioF + altoArena - 1;
        int finC = inicioC + anchoArena - 1;

        // Posiciones de las 4 entradas (centro de cada lado)
        int entradaArriba = (inicioC + finC) / 2;
        int entradaAbajo = (inicioC + finC) / 2;
        int entradaIzq = (inicioF + finF) / 2;
        int entradaDer = (inicioF + finF) / 2;

        for (int f = inicioF; f <= finF; f++) {
            for (int c = inicioC; c <= finC; c++) {
                boolean esBorde = (f == inicioF || f == finF || c == inicioC || c == finC);
                if (!esBorde) continue;
                if (f < 1 || f >= filas - 1 || c < 1 || c >= columnas - 1) continue;

                // Dejar huecos para entradas (3 casillas de ancho)
                if (f == inicioF && c >= entradaArriba - 1 && c <= entradaArriba + 1) continue;
                if (f == finF && c >= entradaAbajo - 1 && c <= entradaAbajo + 1) continue;
                if (c == inicioC && f >= entradaIzq - 1 && f <= entradaIzq + 1) continue;
                if (c == finC && f >= entradaDer - 1 && f <= entradaDer + 1) continue;

                if (tabla[f][c] == null) {
                    tabla[f][c] = new Muro(new Posicion(f, c));
                }
            }
        }

        // Algunos muros sueltos dentro y fuera para variedad
        int murosExtra = (filas + columnas) / 2;
        int colocados = 0;
        while (colocados < murosExtra) {
            int f = 1 + Rng.rng.nextInt(filas - 2);
            int c = 1 + Rng.rng.nextInt(columnas - 2);
            if (tabla[f][c] == null) {
                tabla[f][c] = new Muro(new Posicion(f, c));
                colocados++;
            }
        }
    }

    public static void limpiarPantalla() {
        System.out.print("\033[H");
    }

    // Calcula la longitud visible de un string (sin contar códigos ANSI)
    public static int longitudVisible(String s) {
        return s.replaceAll("\u001B\\[[;\\d]*m", "").length();
    }

    // Genera N espacios
    public static String espacios(int n) {
        return n > 0 ? " ".repeat(n) : "";
    }

    public static void rellenaEnemigoTipo(int num, String tipo, int filas, int columnas, Entidad[][] tabla,
                                              int vida, int danioMin, int danioMax, int vision) {
        for (int i = 0; i < num; i++) {
            boolean esta = false;
            while (!esta) {
                int auxfilas = Rng.rng.nextInt(filas);
                int auxcolumnas = Rng.rng.nextInt(columnas);
                if (tabla[auxfilas][auxcolumnas] == null) {
                    Posicion pos = new Posicion(auxfilas, auxcolumnas);
                    switch (tipo) {
                        case "tanque":
                            tabla[auxfilas][auxcolumnas] = new EnemigoTanque(pos, vida, danioMin, danioMax, vision);
                            break;
                        case "rapido":
                            tabla[auxfilas][auxcolumnas] = new EnemigoRapido(pos, vida, danioMin, danioMax, vision);
                            break;
                    }
                    esta = true;
                }
            }
        }
    }

    public static void generarTrampas(int num, int danio, int filas, int columnas,
                                         Entidad[][] tablero, Objeto[][] objetos, Trampa[][] trampas) {
        int colocadas = 0;
        while (colocadas < num) {
            int f = 1 + Rng.rng.nextInt(filas - 2);
            int c = 1 + Rng.rng.nextInt(columnas - 2);
            if (tablero[f][c] == null && objetos[f][c] == null && trampas[f][c] == null) {
                trampas[f][c] = new Trampa(new Posicion(f, c), danio);
                colocadas++;
            }
        }
    }

    public static Entidad[][] copiarTablero(Entidad[][] original, int FILAS, int COLUMNAS) {
        Entidad[][] copia = new Entidad[FILAS][COLUMNAS];
        for (int f = 0; f < FILAS; f++) {
            System.arraycopy(original[f], 0, copia[f], 0, COLUMNAS);
        }
        return copia;
    }
}
