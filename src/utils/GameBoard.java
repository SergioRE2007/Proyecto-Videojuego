package utils;

import entidades.*;
import objetos.*;

public class GameBoard {
    private Entidad[][] entidades;
    private Objeto[][] objetos;
    private Trampa[][] trampas;
    private int filas, columnas;

    public GameBoard(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.entidades = new Entidad[filas][columnas];
        this.objetos = new Objeto[filas][columnas];
        this.trampas = new Trampa[filas][columnas];
    }

    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }

    public Entidad getEntidad(int f, int c) { return entidades[f][c]; }
    public void setEntidad(int f, int c, Entidad e) { entidades[f][c] = e; }

    public Objeto getObjeto(int f, int c) { return objetos[f][c]; }
    public void setObjeto(int f, int c, Objeto o) { objetos[f][c] = o; }

    public Trampa getTrampa(int f, int c) { return trampas[f][c]; }
    public void setTrampa(int f, int c, Trampa t) { trampas[f][c] = t; }

    // ==================== Colocacion de bordes ====================

    public void colocarBordes() {
        for (int c = 0; c < columnas; c++) {
            entidades[0][c] = new Muro(new Posicion(0, c));
            entidades[filas - 1][c] = new Muro(new Posicion(filas - 1, c));
        }
        for (int f = 1; f < filas - 1; f++) {
            entidades[f][0] = new Muro(new Posicion(f, 0));
            entidades[f][columnas - 1] = new Muro(new Posicion(f, columnas - 1));
        }
    }

    // ==================== Generacion de mapa ====================

    public void generarMapa(GameConfig config) {
        switch (config.tipoMapa) {
            case "abierto":
                generarMuros(config.numMuro, config.probPegarMuro);
                break;
            case "salas":
                generarMapaSalas(config.numMuro);
                break;
            case "laberinto":
                generarMapaLaberinto();
                break;
            case "arena":
                generarMapaArena();
                break;
            default:
                generarMuros(config.numMuro, config.probPegarMuro);
                break;
        }
    }

    private void generarMuros(int numMuros, int probPegarPct) {
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
                        && entidades[fila][col] == null) {
                    entidades[fila][col] = new Muro(new Posicion(fila, col));
                    ultimaFila = fila;
                    ultimaCol = col;
                    colocados++;
                } else {
                    dirActual = Rng.rng.nextInt(4);
                }
            } else {
                int f = 1 + Rng.rng.nextInt(filas - 2);
                int c = 1 + Rng.rng.nextInt(columnas - 2);
                if (entidades[f][c] == null) {
                    entidades[f][c] = new Muro(new Posicion(f, c));
                    ultimaFila = f;
                    ultimaCol = c;
                    dirActual = Rng.rng.nextInt(4);
                    colocados++;
                }
            }
        }
    }

    private void generarMapaSalas(int topeMuros) {
        int murosColocados = 0;
        int numSalas = 3 + Rng.rng.nextInt(4);

        for (int s = 0; s < numSalas && murosColocados < topeMuros; s++) {
            int anchoSala = 3 + Rng.rng.nextInt(5);
            int altoSala = 3 + Rng.rng.nextInt(5);
            int inicioF = 1 + Rng.rng.nextInt(filas - 2 - altoSala);
            int inicioC = 1 + Rng.rng.nextInt(columnas - 2 - anchoSala);

            int numPuertas = 1 + Rng.rng.nextInt(2);
            int[][] puertas = new int[numPuertas][2];
            for (int p = 0; p < numPuertas; p++) {
                int lado = Rng.rng.nextInt(4);
                switch (lado) {
                    case 0:
                        puertas[p] = new int[]{inicioF, inicioC + 1 + Rng.rng.nextInt(anchoSala - 2)};
                        break;
                    case 1:
                        puertas[p] = new int[]{inicioF + altoSala - 1, inicioC + 1 + Rng.rng.nextInt(anchoSala - 2)};
                        break;
                    case 2:
                        puertas[p] = new int[]{inicioF + 1 + Rng.rng.nextInt(altoSala - 2), inicioC};
                        break;
                    case 3:
                        puertas[p] = new int[]{inicioF + 1 + Rng.rng.nextInt(altoSala - 2), inicioC + anchoSala - 1};
                        break;
                }
            }

            for (int f = inicioF; f < inicioF + altoSala && f < filas - 1; f++) {
                for (int c = inicioC; c < inicioC + anchoSala && c < columnas - 1; c++) {
                    boolean esBorde = (f == inicioF || f == inicioF + altoSala - 1 || c == inicioC || c == inicioC + anchoSala - 1);
                    if (!esBorde) continue;

                    boolean esPuerta = false;
                    for (int[] puerta : puertas) {
                        if (f == puerta[0] && c == puerta[1]) {
                            esPuerta = true;
                            break;
                        }
                    }
                    if (esPuerta) continue;

                    if (entidades[f][c] == null && murosColocados < topeMuros) {
                        entidades[f][c] = new Muro(new Posicion(f, c));
                        murosColocados++;
                    }
                }
            }
        }
    }

    private void generarMapaLaberinto() {
        for (int f = 1; f < filas - 1; f++) {
            for (int c = 1; c < columnas - 1; c++) {
                if (entidades[f][c] == null) {
                    entidades[f][c] = new Muro(new Posicion(f, c));
                }
            }
        }

        int celdasF = (filas - 2) / 2;
        int celdasC = (columnas - 2) / 2;
        boolean[][] visitado = new boolean[celdasF][celdasC];
        int[][] pila = new int[celdasF * celdasC][2];
        int topePila = 0;

        visitado[0][0] = true;
        pila[topePila++] = new int[]{0, 0};
        int realF = 1 + 0 * 2;
        int realC = 1 + 0 * 2;
        entidades[realF][realC] = null;

        int[][] dirsCelda = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (topePila > 0) {
            int[] actual = pila[topePila - 1];
            int cf = actual[0], cc = actual[1];

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

                int destF = 1 + nf * 2;
                int destC = 1 + nc * 2;
                entidades[destF][destC] = null;

                int paredF = 1 + cf * 2 + (nf - cf);
                int paredC = 1 + cc * 2 + (nc - cc);
                if (paredF > 0 && paredF < filas - 1 && paredC > 0 && paredC < columnas - 1) {
                    entidades[paredF][paredC] = null;
                }

                pila[topePila++] = new int[]{nf, nc};
            }
        }

        for (int f = 1; f < filas - 1; f++) {
            for (int c = 1; c < columnas - 1; c++) {
                if (entidades[f][c] instanceof Muro && Rng.rng.nextDouble() < 0.30) {
                    entidades[f][c] = null;
                }
            }
        }
    }

    private void generarMapaArena() {
        int altoArena = (int) (filas * 0.6);
        int anchoArena = (int) (columnas * 0.6);
        int inicioF = (filas - altoArena) / 2;
        int inicioC = (columnas - anchoArena) / 2;
        int finF = inicioF + altoArena - 1;
        int finC = inicioC + anchoArena - 1;

        int entradaArriba = (inicioC + finC) / 2;
        int entradaAbajo = (inicioC + finC) / 2;
        int entradaIzq = (inicioF + finF) / 2;
        int entradaDer = (inicioF + finF) / 2;

        for (int f = inicioF; f <= finF; f++) {
            for (int c = inicioC; c <= finC; c++) {
                boolean esBorde = (f == inicioF || f == finF || c == inicioC || c == finC);
                if (!esBorde) continue;
                if (f < 1 || f >= filas - 1 || c < 1 || c >= columnas - 1) continue;

                if (f == inicioF && c >= entradaArriba - 1 && c <= entradaArriba + 1) continue;
                if (f == finF && c >= entradaAbajo - 1 && c <= entradaAbajo + 1) continue;
                if (c == inicioC && f >= entradaIzq - 1 && f <= entradaIzq + 1) continue;
                if (c == finC && f >= entradaDer - 1 && f <= entradaDer + 1) continue;

                if (entidades[f][c] == null) {
                    entidades[f][c] = new Muro(new Posicion(f, c));
                }
            }
        }

        int murosExtra = (filas + columnas) / 2;
        int colocados = 0;
        while (colocados < murosExtra) {
            int f = 1 + Rng.rng.nextInt(filas - 2);
            int c = 1 + Rng.rng.nextInt(columnas - 2);
            if (entidades[f][c] == null) {
                entidades[f][c] = new Muro(new Posicion(f, c));
                colocados++;
            }
        }
    }

    // ==================== Colocacion de entidades ====================

    public void colocarEntidades(GameConfig config) {
        colocarEntidadesTipo("enemigo", config.numEnemigo, config.vidaEnemigo,
                config.danioEnemigoMin, config.danioEnemigoMax, config.visionEnemigo);
        colocarEntidadesTipo("tanque", config.numEnemigoTanque, config.vidaTanque,
                config.danioTanqueMin, config.danioTanqueMax, config.visionTanque);
        colocarEntidadesTipo("rapido", config.numEnemigoRapido, config.vidaRapido,
                config.danioRapidoMin, config.danioRapidoMax, config.visionRapido);
        colocarEntidadesTipo("aliado", config.numAliado, config.vidaAliado,
                config.danioBaseAliadoMin, config.danioBaseAliadoMax, config.visionAliado);
    }

    private void colocarEntidadesTipo(String tipo, int num, int vida, int danioMin, int danioMax, int vision) {
        for (int i = 0; i < num; i++) {
            boolean colocado = false;
            while (!colocado) {
                int f = Rng.rng.nextInt(filas);
                int c = Rng.rng.nextInt(columnas);
                if (entidades[f][c] == null) {
                    Posicion pos = new Posicion(f, c);
                    switch (tipo) {
                        case "enemigo":
                            entidades[f][c] = new Enemigo(pos, vida, danioMin, danioMax, vision);
                            break;
                        case "tanque":
                            entidades[f][c] = new EnemigoTanque(pos, vida, danioMin, danioMax, vision);
                            break;
                        case "rapido":
                            entidades[f][c] = new EnemigoRapido(pos, vida, danioMin, danioMax, vision);
                            break;
                        case "aliado":
                            entidades[f][c] = new Aliado(pos, vida, danioMin, danioMax, vision);
                            break;
                    }
                    colocado = true;
                }
            }
        }
    }

    // ==================== Objetos ====================

    public void generarObjetos(GameConfig config) {
        colocarObjetosTipo(config.numEscudo, "escudo", config);
        colocarObjetosTipo(config.numArma, "arma", config);
        colocarObjetosTipo(config.numEstrella, "estrella", config);
        colocarObjetosTipo(config.numVelocidad, "velocidad", config);
        colocarObjetosTipo(config.numPocion, "pocion", config);
    }

    private void colocarObjetosTipo(int cantidad, String tipo, GameConfig config) {
        int colocados = 0;
        while (colocados < cantidad) {
            int f = Rng.rng.nextInt(filas);
            int c = Rng.rng.nextInt(columnas);
            if (entidades[f][c] == null && objetos[f][c] == null) {
                objetos[f][c] = crearObjeto(tipo, new Posicion(f, c), config);
                colocados++;
            }
        }
    }

    private Objeto crearObjeto(String tipo, Posicion pos, GameConfig config) {
        switch (tipo) {
            case "escudo": return new Escudo(pos, config.valorEscudo);
            case "arma": return new Arma(pos, config.valorArma);
            case "estrella": return new Estrella(pos, config.turnosEstrella);
            case "velocidad": return new Velocidad(pos, config.duracionVelocidad);
            case "pocion": return new Pocion(pos, config.curacionPocion);
            default: return new Escudo(pos, config.valorEscudo);
        }
    }

    public void spawnObjetoRandom(GameConfig config) {
        int intentos = 0;
        while (intentos < 100) {
            int f = Rng.rng.nextInt(filas);
            int c = Rng.rng.nextInt(columnas);
            if (entidades[f][c] == null && objetos[f][c] == null) {
                Posicion pos = new Posicion(f, c);
                double r = Rng.rng.nextDouble();
                if (r < 0.30) {
                    objetos[f][c] = new Escudo(pos, config.valorEscudo);
                } else if (r < 0.50) {
                    objetos[f][c] = new Arma(pos, config.valorArma);
                } else if (r < 0.70) {
                    objetos[f][c] = new Velocidad(pos, config.duracionVelocidad);
                } else if (r < 0.85) {
                    objetos[f][c] = new Pocion(pos, config.curacionPocion);
                } else {
                    objetos[f][c] = new Estrella(pos, config.turnosEstrella);
                }
                return;
            }
            intentos++;
        }
    }

    // ==================== Trampas ====================

    public void generarTrampas(int num, int danio) {
        int colocadas = 0;
        while (colocadas < num) {
            int f = 1 + Rng.rng.nextInt(filas - 2);
            int c = 1 + Rng.rng.nextInt(columnas - 2);
            if (entidades[f][c] == null && objetos[f][c] == null && trampas[f][c] == null) {
                trampas[f][c] = new Trampa(new Posicion(f, c), danio);
                colocadas++;
            }
        }
    }
}
