package entidades;

import utils.GameBoard;
import utils.Posicion;

public class Muro extends Entidad {

    public Muro(Posicion pos) {
        super(pos, 'M', 0);
    }

    @Override
    public void actuar(GameBoard board) {
        // Los muros no actuan
    }
}
