package objetos;

import utils.Posicion;
import entidades.Aliado;

public class Pocion extends Objeto {
    private int curacion;

    public Pocion(Posicion pos, int curacion) {
        super(pos, '+');
        this.curacion = curacion;
    }

    @Override
    public void aplicar(Aliado aliado) {
        aliado.curar(curacion);
    }
}
