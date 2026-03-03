package entidades;

import objetos.Objeto;
import utils.Posicion;

public class Enemigo extends Entidad {

    private int vision;
    private int danioMin;
    private int danioMax;

    public Enemigo(Posicion pos, int vida, int danioMin, int danioMax, int vision) {
        super(pos, 'X', vida);
        this.danioMin = danioMin;
        this.danioMax = danioMax;
        this.vision = vision;
    }

    public int getDanio() {
        return danioMin + (int) (Math.random() * (danioMax - danioMin + 1));
    }

    public int getDanioMin() {
        return danioMin;
    }

    public int getDanioMax() {
        return danioMax;
    }

    @Override
    public void actuar(Entidad[][] tablero, Objeto[][] objetos) {
        Entidad masCercano = buscarCercano(Aliado.class, vision, tablero);
        if (masCercano != null) {
            moverHacia(masCercano.getPosicion(), tablero);
        } else {
            moverRandom(tablero);
        }
    }
}
