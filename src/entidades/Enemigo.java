package entidades;

import utils.GameBoard;
import utils.Posicion;
import utils.Rng;

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
        return danioMin + Rng.rng.nextInt(danioMax - danioMin + 1);
    }

    public int getDanioMin() {
        return danioMin;
    }

    public int getDanioMax() {
        return danioMax;
    }

    @Override
    public void actuar(GameBoard board) {
        Entidad masCercano = buscarCercano(Aliado.class, vision, board);
        if (masCercano != null) {
            moverHacia(masCercano.getPosicion(), board);
        } else {
            moverRandom(board);
        }
    }
}
