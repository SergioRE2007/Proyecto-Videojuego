public abstract class Entidad {
    protected Posicion posicion;
    protected char simbolo;

    // Las 8 direcciones: cardinales + diagonales
    private static final int[][] MOVIMIENTOS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1},
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    public Entidad(Posicion pos, char simbolo) {
        this.posicion = pos;
        this.simbolo = simbolo;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public char getSimbolo() {
        return simbolo;
    }

    protected int distancia(Posicion p1, Posicion p2) {
        return Math.abs(p1.getFila() - p2.getFila()) + Math.abs(p1.getColumna() - p2.getColumna());
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
        for (int[] mov : movs) {
            if (moverSiPosible(posicion.getFila() + mov[0], posicion.getColumna() + mov[1], tablero)) {
                return true;
            }
        }
        return false;
    }

    private boolean moverSiPosible(int nuevaFila, int nuevaCol, Entidad[][] tablero) {
        if (nuevaFila < 0 || nuevaFila >= tablero.length || nuevaCol < 0 || nuevaCol >= tablero[0].length) {
            return false;
        }

        Entidad destino = tablero[nuevaFila][nuevaCol];

        if (this instanceof Enemigo && destino instanceof Aliado) {
            tablero[nuevaFila][nuevaCol] = null;
        }

        if (tablero[nuevaFila][nuevaCol] != null) {
            return false;
        }

        tablero[posicion.getFila()][posicion.getColumna()] = null;
        posicion.setFila(nuevaFila);
        posicion.setColumna(nuevaCol);
        tablero[nuevaFila][nuevaCol] = this;
        return true;
    }
}
