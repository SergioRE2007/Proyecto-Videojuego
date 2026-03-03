package entidades;

import objetos.Objeto;
import utils.Posicion;

public abstract class Entidad {
    protected Posicion posicion;
    protected char simbolo;
    protected int vida;
    protected int vidaMax;
    private int filaAnterior = -1;
    private int colAnterior = -1;

    // Las 8 direcciones: cardinales + diagonales
    private static final int[][] MOVIMIENTOS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1},
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    public Entidad(Posicion pos, char simbolo, int vida) {
        this.posicion = pos;
        this.simbolo = simbolo;
        this.vida = vida;
        this.vidaMax = vida;
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

    public abstract void actuar(Entidad[][] tablero, Objeto[][] objetos);

    protected int distancia(Posicion p1, Posicion p2) {
        return Math.abs(p1.getFila() - p2.getFila()) + Math.abs(p1.getColumna() - p2.getColumna());
    }

    protected Entidad buscarCercano(Class<?> tipo, int vision, Entidad[][] tablero) {
        Entidad mejor = null;
        int distMin = Integer.MAX_VALUE;
        int miFila = posicion.getFila();
        int miCol = posicion.getColumna();
        for (int df = -vision; df <= vision; df++) {
            for (int dc = -vision; dc <= vision; dc++) {
                int fila = miFila + df;
                int col = miCol + dc;
                if (fila >= 0 && fila < tablero.length && col >= 0 && col < tablero[0].length) {
                    Entidad e = tablero[fila][col];
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

    protected void moverHacia(Posicion destino, Entidad[][] tablero) {
        int[][] movs = copiarMovimientos();
        ordenarPorDistancia(movs, destino, true);
        intentarMovimientos(movs, tablero);
    }

    protected void moverLejos(Posicion enemigoPos, Entidad[][] tablero) {
        int[][] movs = copiarMovimientos();
        ordenarPorDistancia(movs, enemigoPos, false);
        intentarMovimientos(movs, tablero);
    }

    protected void moverRandom(Entidad[][] tablero) {
        int[][] movs = copiarMovimientos();
        // Fisher-Yates shuffle
        for (int i = movs.length - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            int[] tmp = movs[i];
            movs[i] = movs[j];
            movs[j] = tmp;
        }
        intentarMovimientos(movs, tablero);
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

    private boolean intentarMovimientos(int[][] movs, Entidad[][] tablero) {
        // Primer paso: intentar todo excepto volver a la posición anterior
        for (int[] mov : movs) {
            int nf = posicion.getFila() + mov[0];
            int nc = posicion.getColumna() + mov[1];
            if (nf == filaAnterior && nc == colAnterior) continue;
            if (moverSiPosible(nf, nc, tablero)) {
                return true;
            }
        }
        // Si nada funcionó, permitir volver atrás como último recurso
        for (int[] mov : movs) {
            int nf = posicion.getFila() + mov[0];
            int nc = posicion.getColumna() + mov[1];
            if (nf == filaAnterior && nc == colAnterior) {
                if (moverSiPosible(nf, nc, tablero)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean moverSiPosible(int nuevaFila, int nuevaCol, Entidad[][] tablero) {
        if (nuevaFila < 0 || nuevaFila >= tablero.length || nuevaCol < 0 || nuevaCol >= tablero[0].length) {
            return false;
        }

        Entidad destino = tablero[nuevaFila][nuevaCol];

        // Enemigo ataca Aliado
        if (this instanceof Enemigo && destino instanceof Aliado) {
            Aliado aliado = (Aliado) destino;
            if (aliado.getTurnosInvencible() > 0) {
                // Aliado invencible: el enemigo muere
                this.recibirDanio(100);
                tablero[posicion.getFila()][posicion.getColumna()] = null;
                return false;
            }
            aliado.recibirDanio(((Enemigo) this).getDanio());
            // Contraataque: daño base configurable + daño extra por arma
            int contraataque = aliado.getDanioBaseMin() + (int) (Math.random() * (aliado.getDanioBaseMax() - aliado.getDanioBaseMin() + 1)) + aliado.getDanioExtra();
            this.recibirDanio(contraataque);
            if (!this.estaVivo()) {
                tablero[posicion.getFila()][posicion.getColumna()] = null;
                return false;
            }
            if (aliado.estaVivo()) {
                return false;
            }
            tablero[nuevaFila][nuevaCol] = null;
        }

        // Aliado con estrella ataca Enemigo
        if (this instanceof Aliado && destino instanceof Enemigo) {
            Aliado aliado = (Aliado) this;
            if (aliado.getTurnosInvencible() > 0) {
                destino.recibirDanio(100);
                tablero[nuevaFila][nuevaCol] = null;
            }
        }

        if (tablero[nuevaFila][nuevaCol] != null) {
            return false;
        }

        int filaVieja = posicion.getFila();
        int colVieja = posicion.getColumna();
        tablero[filaVieja][colVieja] = null;
        posicion.setFila(nuevaFila);
        posicion.setColumna(nuevaCol);
        tablero[nuevaFila][nuevaCol] = this;
        filaAnterior = filaVieja;
        colAnterior = colVieja;
        return true;
    }
}
