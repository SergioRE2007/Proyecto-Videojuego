package entidades;

import utils.Posicion;

public class Trampa {
    private Posicion posicion;
    private int danio;
    private char simbolo = '^';

    public Trampa(Posicion posicion, int danio) {
        this.posicion = posicion;
        this.danio = danio;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public int getDanio() {
        return danio;
    }

    public char getSimbolo() {
        return simbolo;
    }
}
