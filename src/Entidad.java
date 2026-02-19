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
        int[][] deltas = {
                { -1, 0 }, { +1, 0 }, { 0, -1 }, { 0, +1 },
                { -1, -1 }, { -1, +1 }, { +1, -1 }, { +1, +1 }
        };

        int distActual = distancia(posicion, destino);
        boolean movido = false;

        for (int[] delta : deltas) {
            int nuevaFila = posicion.getFila() + delta[0];
            int nuevaCol = posicion.getColumna() + delta[1];

            if (nuevaFila >= 0 && nuevaFila < tablero.length &&
                    nuevaCol >= 0 && nuevaCol < tablero[0].length) {

                // 1. PRIMERO: comprobar si moverSiPosible LO PERMITIRÍA
                Entidad destinoObj = tablero[nuevaFila][nuevaCol];
                boolean movimientoValido = true;

                if (destinoObj instanceof Muro) {
                    movimientoValido = false;
                } else if (this instanceof Enemigo && destinoObj instanceof Aliado) {
                    // puede matar
                } else if (destinoObj != null) {
                    movimientoValido = false; // cualquier otra cosa bloquea
                }

                // 2. SI es válido, calcular distancia
                if (movimientoValido) {
                    Posicion nuevaPos = new Posicion(nuevaFila, nuevaCol);
                    int nuevaDist = distancia(nuevaPos, destino);

                    if (nuevaDist < distActual) {
                        // 3. AHORA sí mover
                        moverSiPosible(nuevaFila, nuevaCol, tablero);
                        movido = true;
                        break;
                    }
                }
            }
        }

        if (!movido) {
            moverRandom(tablero);
        }
    }

    protected void moverLejos(Posicion enemigoPos, Entidad[][] tablero) {
        int[][] deltas = {
                { 0, -1 }, { 0, +1 }, { -1, 0 }, { +1, 0 },
                { -1, -1 }, { -1, +1 }, { +1, -1 }, { +1, +1 }
        };

        int distActual = distancia(posicion, enemigoPos);
        boolean movido = false;

        for (int[] delta : deltas) {
            int nuevaFila = posicion.getFila() + delta[0];
            int nuevaCol = posicion.getColumna() + delta[1];

            if (nuevaFila >= 0 && nuevaFila < tablero.length &&
                    nuevaCol >= 0 && nuevaCol < tablero[0].length) {

                Entidad destinoObj = tablero[nuevaFila][nuevaCol];
                boolean movimientoValido = true;

                if (destinoObj != null) {
                    movimientoValido = false;
                }

                if (movimientoValido) {
                    Posicion nuevaPos = new Posicion(nuevaFila, nuevaCol);
                    int nuevaDist = distancia(nuevaPos, enemigoPos);

                    if (nuevaDist > distActual) { // SE ALEJA
                        moverSiPosible(nuevaFila, nuevaCol, tablero);
                        movido = true;
                        break;
                    }
                }
            }
        }

        if (!movido) {
            moverRandom(tablero);
        }
    }

    private boolean moverSiPosible(int nuevaFila, int nuevaCol, Entidad[][] tablero) {
        if (nuevaFila < 0 || nuevaFila >= tablero.length || nuevaCol < 0 || nuevaCol >= tablero[0].length) {
            return false;
        }

        Entidad destino = tablero[nuevaFila][nuevaCol];

        // NO pasar muros
        if (destino instanceof Muro) {
            return false;
        }

        // Enemigo mata aliado
        if (this instanceof Enemigo && destino instanceof Aliado) {
            tablero[nuevaFila][nuevaCol] = null;
        }

        // Aliado NO entra en enemigo
        if (this instanceof Aliado && destino instanceof Enemigo) {
            return false;
        }

        // NINGUNA entidad entra en otra del MISMO tipo
        if (destino != null &&
                (this instanceof Enemigo && destino instanceof Enemigo ||
                        this instanceof Aliado && destino instanceof Aliado)) {
            return false;
        }

        // Mover
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

}
