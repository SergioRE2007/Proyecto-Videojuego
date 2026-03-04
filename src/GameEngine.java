import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entidades.*;
import objetos.*;
import utils.*;

public class GameEngine {
    private GameBoard board;
    private GameConfig config;
    private int turno;
    private int enemigosEliminados;
    private int objetosRecogidos;
    private int numAliados;
    private int numEnemigos;
    private int numAliadosInicial;
    private int numEnemigosInicial;
    private long tiempoInicio;
    private String resultado;
    private List<Entidad> todasEntidades;

    public GameEngine(GameConfig config) {
        this.config = config;
    }

    public void inicializar() {
        Rng.rng = (config.semilla == -1) ? new Random() : new Random(config.semilla);

        board = new GameBoard(config.filas, config.columnas);
        board.colocarBordes();
        board.generarMapa(config);
        board.colocarEntidades(config);
        board.generarObjetos(config);
        board.generarTrampas(config.numTrampa, config.danioTrampa);

        turno = 0;
        enemigosEliminados = 0;
        objetosRecogidos = 0;
        tiempoInicio = System.currentTimeMillis();

        // Guardar referencia a todas las entidades para stats post-partida
        todasEntidades = new ArrayList<>();
        numAliadosInicial = 0;
        numEnemigosInicial = 0;
        for (int f = 0; f < board.getFilas(); f++) {
            for (int c = 0; c < board.getColumnas(); c++) {
                Entidad e = board.getEntidad(f, c);
                if (e instanceof Aliado) { todasEntidades.add(e); numAliadosInicial++; }
                else if (e instanceof Enemigo) { todasEntidades.add(e); numEnemigosInicial++; }
            }
        }
    }

    public void tick() {
        turno++;

        // Recoger todas las entidades ANTES de moverlas
        List<Entidad> entidades = new ArrayList<>();
        for (int f = 0; f < board.getFilas(); f++) {
            for (int c = 0; c < board.getColumnas(); c++) {
                Entidad e = board.getEntidad(f, c);
                if (e instanceof Enemigo || e instanceof Aliado) {
                    entidades.add(e);
                }
            }
        }

        for (Entidad e : entidades) {
            Posicion p = e.getPosicion();
            if (board.getEntidad(p.getFila(), p.getColumna()) != e) continue;
            e.actuar(board);
        }

        // Dano por trampas
        for (int f = 0; f < board.getFilas(); f++) {
            for (int c = 0; c < board.getColumnas(); c++) {
                Entidad eTrampa = board.getEntidad(f, c);
                if (eTrampa != null && board.getTrampa(f, c) != null
                        && !(eTrampa instanceof Muro)) {
                    int danioTrampa = board.getTrampa(f, c).getDanio();
                    eTrampa.addDanioRecibido(danioTrampa);
                    eTrampa.recibirDanio(danioTrampa);
                }
            }
        }

        // Recogida de objetos por aliados
        for (int f = 0; f < board.getFilas(); f++) {
            for (int c = 0; c < board.getColumnas(); c++) {
                if (board.getEntidad(f, c) instanceof Aliado && board.getObjeto(f, c) != null) {
                    Aliado aliadoObj = (Aliado) board.getEntidad(f, c);
                    board.getObjeto(f, c).aplicar(aliadoObj);
                    board.setObjeto(f, c, null);
                    aliadoObj.incrementarObjetosRecogidos();
                    objetosRecogidos++;
                }
            }
        }

        // Eliminar entidades muertas del tablero
        for (int f = 0; f < board.getFilas(); f++) {
            for (int c = 0; c < board.getColumnas(); c++) {
                Entidad e = board.getEntidad(f, c);
                if (e != null && !e.estaVivo() && !(e instanceof Muro)) {
                    if (e instanceof Enemigo) enemigosEliminados++;
                    board.setEntidad(f, c, null);
                }
            }
        }

        // Spawn de objeto aleatorio cada N turnos
        if (turno % config.turnosSpawnObjeto == 0) {
            board.spawnObjetoRandom(config);
        }

        // Contar entidades
        numAliados = 0;
        numEnemigos = 0;
        for (int f = 0; f < board.getFilas(); f++) {
            for (int c = 0; c < board.getColumnas(); c++) {
                Entidad e = board.getEntidad(f, c);
                if (e instanceof Aliado) numAliados++;
                else if (e instanceof Enemigo) numEnemigos++;
            }
        }

        // Comprobar fin de partida
        if (numAliados == 0 && numEnemigos == 0) {
            resultado = "empate";
        } else if (numAliados == 0) {
            resultado = "enemigos";
        } else if (numEnemigos == 0) {
            resultado = "aliados";
        }
    }

    public boolean haTerminado() { return resultado != null; }
    public GameBoard getBoard() { return board; }
    public GameConfig getConfig() { return config; }
    public int getTurno() { return turno; }
    public int getEnemigosEliminados() { return enemigosEliminados; }
    public int getObjetosRecogidos() { return objetosRecogidos; }
    public int getNumAliados() { return numAliados; }
    public int getNumEnemigos() { return numEnemigos; }
    public long getTiempoInicio() { return tiempoInicio; }
    public String getResultado() { return resultado; }
    public List<Entidad> getTodasEntidades() { return todasEntidades; }
    public int getNumAliadosInicial() { return numAliadosInicial; }
    public int getNumEnemigosInicial() { return numEnemigosInicial; }
}
