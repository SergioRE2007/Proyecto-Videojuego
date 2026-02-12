public abstract class Entidad {
    protected Posicion posicion;
    protected char simbolo;

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
        int filaDestino = destino.getFila();
        int colDestino = destino.getColumna();

        if (filaDestino > posicion.getFila()) {
            moverAbajo(tablero);
        } else if (filaDestino < posicion.getFila()) {
            moverArriba(tablero);
        } else if (colDestino > posicion.getColumna()) {
            moverDerecha(tablero);
        } else if (colDestino < posicion.getColumna()) {
            moverIzquierda(tablero);
        }
    }

    protected void moverLejos(Posicion enemigoPos, Entidad[][] tablero) {
        int filaEnemigo = enemigoPos.getFila();
        int colEnemigo = enemigoPos.getColumna();

        if (filaEnemigo > posicion.getFila()) {
            moverArriba(tablero);
        } else if (filaEnemigo < posicion.getFila()) {
            moverAbajo(tablero);
        } else if (colEnemigo > posicion.getColumna()) {
            moverIzquierda(tablero);
        } else if (colEnemigo < posicion.getColumna()) {
            moverDerecha(tablero);
        }
    }

    protected boolean moverArriba(Entidad[][] tablero) {
        int nuevaFila = posicion.getFila() - 1;
        return moverSiPosible(nuevaFila, posicion.getColumna(), tablero);
    }

    protected boolean moverAbajo(Entidad[][] tablero) {
        int nuevaFila = posicion.getFila() + 1;
        return moverSiPosible(nuevaFila, posicion.getColumna(), tablero);
    }

    protected boolean moverIzquierda(Entidad[][] tablero) {
        int nuevaCol = posicion.getColumna() - 1;
        return moverSiPosible(posicion.getFila(), nuevaCol, tablero);
    }

    protected boolean moverDerecha(Entidad[][] tablero) {
        int nuevaCol = posicion.getColumna() + 1;
        return moverSiPosible(posicion.getFila(), nuevaCol, tablero);
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

    protected void moverRandom(Entidad[][] tablero) {
        int dir = (int) (Math.random() * 4);
        switch (dir) {
            case 0 -> moverArriba(tablero);
            case 1 -> moverAbajo(tablero);
            case 2 -> moverIzquierda(tablero);
            case 3 -> moverDerecha(tablero);
        }
    }
}
