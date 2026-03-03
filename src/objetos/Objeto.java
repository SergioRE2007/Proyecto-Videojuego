package objetos;

import utils.Posicion;
import entidades.Aliado;

public abstract class Objeto {
    protected Posicion posicion;
    protected char simbolo;

    public Objeto(Posicion posicion, char simbolo) {
        this.posicion = posicion;
        this.simbolo = simbolo;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public char getSimbolo() {
        return simbolo;
    }

    public abstract void aplicar(Aliado aliado);
}
