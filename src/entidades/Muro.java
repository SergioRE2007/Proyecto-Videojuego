package entidades;

import objetos.Objeto;
import utils.Posicion;

public class Muro extends Entidad {

    public Muro(Posicion pos) {
        super(pos, 'M', 0);
    }

    @Override
    public void actuar(Entidad[][] tablero, Objeto[][] objetos) {
        // Los muros no actuan
    }
}
