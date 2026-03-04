package entidades;

import utils.GameBoard;
import utils.Posicion;
import utils.Rng;

public abstract class Entidad {
    private static int contadorId = 0;

    protected Posicion posicion;
    protected char simbolo;
    protected int vida;
    protected int vidaMax;
    protected int id;
    protected int danioInfligido;
    protected int danioRecibido;
    protected int kills;

    // Historial de posiciones recientes para evitar ciclos
    private static final int HISTORIAL_MAX = 5;
    private int[] historialFilas = new int[HISTORIAL_MAX];
    private int[] historialCols = new int[HISTORIAL_MAX];
    private int historialSize = 0;
    private int historialIdx = 0;

    // Las 8 direcciones: cardinales + diagonales
    private static final int[][] MOVIMIENTOS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1},
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    public Entidad(Posicion pos, char simbolo, int vida) {
        this.id = contadorId++;
        this.posicion = pos;
        this.simbolo = simbolo;
        this.vida = vida;
        this.vidaMax = vida;
    }

    public static void resetContadorId() {
        contadorId = 0;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public char getSimbolo() {
        return simbolo;
    }

    public int getVida() {
        return vida;
    }

    public void recibirDanio(int danio) {
        vida -= danio;
        if (vida < 0) vida = 0;
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public int getId() { return id; }
    public int getDanioInfligido() { return danioInfligido; }
    public int getDanioRecibido() { return danioRecibido; }
    public int getKills() { return kills; }
    public void addDanioRecibido(int cantidad) { danioRecibido += cantidad; }

    public abstract void actuar(GameBoard board);

    protected int distancia(Posicion p1, Posicion p2) {
        return Math.abs(p1.getFila() - p2.getFila()) + Math.abs(p1.getColumna() - p2.getColumna());
    }

    protected Entidad buscarCercano(Class<?> tipo, int vision, GameBoard board) {
        Entidad mejor = null;
        int distMin = Integer.MAX_VALUE;
        int miFila = posicion.getFila();
        int miCol = posicion.getColumna();
        for (int df = -vision; df <= vision; df++) {
            for (int dc = -vision; dc <= vision; dc++) {
                int fila = miFila + df;
                int col = miCol + dc;
                if (fila >= 0 && fila < board.getFilas() && col >= 0 && col < board.getColumnas()) {
                    Entidad e = board.getEntidad(fila, col);
                    if (e != null && tipo.isInstance(e)) {
                        int dist = distancia(posicion, e.getPosicion());
                        if (dist < distMin) {
                            distMin = dist;
                            mejor = e;
                        }
                    }
                }
            }
        }
        return mejor;
    }

    protected void moverHacia(Posicion destino, GameBoard board) {
        int[][] movs = copiarMovimientos();
        ordenarPorDistancia(movs, destino, true);
        intentarMovimientos(movs, board);
    }

    protected void moverLejos(Posicion enemigoPos, GameBoard board) {
        int[][] movs = copiarMovimientos();
        ordenarPorDistancia(movs, enemigoPos, false);
        intentarMovimientos(movs, board);
    }

    protected void moverRandom(GameBoard board) {
        int[][] movs = copiarMovimientos();
        // Fisher-Yates shuffle
        for (int i = movs.length - 1; i > 0; i--) {
            int j = Rng.rng.nextInt(i + 1);
            int[] tmp = movs[i];
            movs[i] = movs[j];
            movs[j] = tmp;
        }
        intentarMovimientos(movs, board);
    }

    private int[][] copiarMovimientos() {
        int[][] copia = new int[MOVIMIENTOS.length][2];
        for (int i = 0; i < MOVIMIENTOS.length; i++) {
            copia[i][0] = MOVIMIENTOS[i][0];
            copia[i][1] = MOVIMIENTOS[i][1];
        }
        return copia;
    }

    private void ordenarPorDistancia(int[][] movs, Posicion objetivo, boolean ascendente) {
        for (int i = 0; i < movs.length - 1; i++) {
            int mejorIdx = i;
            int mejorDist = distancia(
                new Posicion(posicion.getFila() + movs[i][0], posicion.getColumna() + movs[i][1]),
                objetivo);
            for (int j = i + 1; j < movs.length; j++) {
                int dist = distancia(
                    new Posicion(posicion.getFila() + movs[j][0], posicion.getColumna() + movs[j][1]),
                    objetivo);
                if (ascendente ? dist < mejorDist : dist > mejorDist) {
                    mejorDist = dist;
                    mejorIdx = j;
                }
            }
            int[] tmp = movs[i];
            movs[i] = movs[mejorIdx];
            movs[mejorIdx] = tmp;
        }
    }

    private boolean estaEnHistorial(int fila, int col) {
        for (int i = 0; i < historialSize; i++) {
            if (historialFilas[i] == fila && historialCols[i] == col) return true;
        }
        return false;
    }

    private boolean intentarMovimientos(int[][] movs, GameBoard board) {
        // Primer paso: intentar todo excepto posiciones del historial
        for (int[] mov : movs) {
            int nf = posicion.getFila() + mov[0];
            int nc = posicion.getColumna() + mov[1];
            if (estaEnHistorial(nf, nc)) continue;
            if (moverSiPosible(nf, nc, board)) {
                return true;
            }
        }
        // Si nada funciono, permitir volver a posiciones del historial
        for (int[] mov : movs) {
            int nf = posicion.getFila() + mov[0];
            int nc = posicion.getColumna() + mov[1];
            if (estaEnHistorial(nf, nc)) {
                if (moverSiPosible(nf, nc, board)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean moverSiPosible(int nuevaFila, int nuevaCol, GameBoard board) {
        if (nuevaFila < 0 || nuevaFila >= board.getFilas() || nuevaCol < 0 || nuevaCol >= board.getColumnas()) {
            return false;
        }

        // Aliados detectan trampas y las esquivan
        if (this instanceof Aliado && board.getTrampa(nuevaFila, nuevaCol) != null) {
            return false;
        }

        Entidad destino = board.getEntidad(nuevaFila, nuevaCol);

        // Enemigo ataca Aliado
        if (this instanceof Enemigo && destino instanceof Aliado) {
            Aliado aliado = (Aliado) destino;
            if (aliado.getTurnosInvencible() > 0) {
                // Aliado invencible: el enemigo muere
                aliado.danioInfligido += this.vida;
                this.danioRecibido += this.vida;
                aliado.kills++;
                this.recibirDanio(this.vida);
                board.setEntidad(posicion.getFila(), posicion.getColumna(), null);
                return false;
            }
            int danioEnemigo = ((Enemigo) this).getDanio();
            this.danioInfligido += danioEnemigo;
            aliado.danioRecibido += danioEnemigo;
            aliado.recibirDanio(danioEnemigo);
            // Contraataque: dano base configurable + dano extra por arma
            int contraataque = aliado.getDanioBaseMin() + Rng.rng.nextInt(aliado.getDanioBaseMax() - aliado.getDanioBaseMin() + 1) + aliado.getDanioExtra();
            aliado.danioInfligido += contraataque;
            this.danioRecibido += contraataque;
            this.recibirDanio(contraataque);
            if (!this.estaVivo()) {
                aliado.kills++;
                board.setEntidad(posicion.getFila(), posicion.getColumna(), null);
                return false;
            }
            if (aliado.estaVivo()) {
                return false;
            }
            this.kills++;
            board.setEntidad(nuevaFila, nuevaCol, null);
        }

        // Aliado con estrella ataca Enemigo
        if (this instanceof Aliado && destino instanceof Enemigo) {
            Aliado aliado = (Aliado) this;
            if (aliado.getTurnosInvencible() > 0) {
                aliado.danioInfligido += destino.vida;
                destino.danioRecibido += destino.vida;
                aliado.kills++;
                destino.recibirDanio(destino.vida);
                board.setEntidad(nuevaFila, nuevaCol, null);
            }
        }

        if (board.getEntidad(nuevaFila, nuevaCol) != null) {
            return false;
        }

        int filaVieja = posicion.getFila();
        int colVieja = posicion.getColumna();
        board.setEntidad(filaVieja, colVieja, null);
        posicion.setFila(nuevaFila);
        posicion.setColumna(nuevaCol);
        board.setEntidad(nuevaFila, nuevaCol, this);

        // Anadir posicion vieja al historial (circular)
        historialFilas[historialIdx] = filaVieja;
        historialCols[historialIdx] = colVieja;
        historialIdx = (historialIdx + 1) % HISTORIAL_MAX;
        if (historialSize < HISTORIAL_MAX) historialSize++;
        return true;
    }
}
