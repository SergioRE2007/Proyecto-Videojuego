public abstract class Entidad {
    protected Posicion posicion;
    protected char simbolo;

    // Constructor
public Entidad(Posicion pos, char simbolo) {
        this.posicion = pos;
        this.simbolo = simbolo;
    }

    // Getters
public Posicion getPosicion() {
        return posicion;
    }

public char getSimbolo() {
        return simbolo;
    }

    // DISTANCIA COMÚN
protected int distanciaManhattan(Posicion p1, Posicion p2) {
        return Math.abs(p1.getFila() - p2.getFila()) + Math.abs(p1.getColumna() - p2.getColumna());
    }

    // MOVER HACIA (para Enemigo)
protected void moverHacia(Posicion destino, Entidad[][] tablero) {
    int filaDestino = destino.getFila();
    int colDestino = destino.getColumna();
    boolean movido = false;

    // Prioridad: vertical primero
    if (filaDestino > posicion.getFila()) {
        movido = moverAbajo(tablero);
    } else if (filaDestino < posicion.getFila()) {
        movido = moverArriba(tablero);
    }

    // Si no se movió en vertical, prueba horizontal
    if (!movido) {
        if (colDestino > posicion.getColumna()) {
            moverDerecha(tablero);
        } else if (colDestino < posicion.getColumna()) {
            moverIzquierda(tablero);
        }
    }
}

    // MOVER LEJOS (para Aliado)
protected void moverLejos(Posicion enemigoPos, Entidad[][] tablero) {
    int filaEnemigo = enemigoPos.getFila();
    int colEnemigo = enemigoPos.getColumna();
    boolean movido = false;
    // Primero intentar alejarse verticalmente
    if (filaEnemigo > posicion.getFila()) {
        movido = moverArriba(tablero);    // enemigo abajo → yo arriba
    } else if (filaEnemigo < posicion.getFila()) {
        movido = moverAbajo(tablero);     // enemigo arriba → yo abajo
    }
    // Si no ha podido, probar horizontalmente
    if (!movido) {
        if (colEnemigo > posicion.getColumna()) {
            moverIzquierda(tablero);      // enemigo derecha → yo izquierda
        } else if (colEnemigo < posicion.getColumna()) {
            moverDerecha(tablero);        // enemigo izquierda → yo derecha
        }
    }
}
    // MOVIMIENTOS BÁSICOS (comunes)
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


    // Al final del código que te copié:
private boolean moverSiPosible(int nuevaFila, int nuevaCol, Entidad[][] tablero) {
    if (nuevaFila < 0 || nuevaFila >= tablero.length ||
        nuevaCol < 0 || nuevaCol >= tablero[0].length) {
        return false;
    }

    Entidad destino = tablero[nuevaFila][nuevaCol];

    // 1) Muro: nunca paso
    if (destino instanceof Muro) {
        return false;
    }

    // 2) Si YO soy Enemigo y destino es Aliado → lo mato (borro al aliado)
    if (this instanceof Enemigo && destino instanceof Aliado) {
        tablero[nuevaFila][nuevaCol] = null; // quito al aliado
        // sigo y me coloco yo en esa casilla
    }

    // 3) Si YO soy Aliado y destino es Enemigo → no me muevo
    if (this instanceof Aliado && destino instanceof Enemigo) {
        return false;
    }

    // 4) Si queda cualquier cosa (otro enemigo, otro aliado) → no paso
    if (tablero[nuevaFila][nuevaCol] != null) {
        return false;
    }

    // 5) Mover
    tablero[posicion.getFila()][posicion.getColumna()] = null;
    posicion.setFila(nuevaFila);
    posicion.setColumna(nuevaCol);
    tablero[nuevaFila][nuevaCol] = this;

    return true;
}



    protected void moverRandom(Entidad[][] tablero) {
        int dir = (int) (Math.random() * 4); // 0..3
        switch (dir) {
            case 0 -> moverArriba(tablero); // usa moverSiPosible(...)
            case 1 -> moverAbajo(tablero);
            case 2 -> moverIzquierda(tablero);
            case 3 -> moverDerecha(tablero);
        }
    }

}
