package objetos;

import entidades.Aliado;
import utils.Posicion;

public class Arma extends Objeto {

    public Arma(Posicion posicion) {
        super(posicion, 'W');
    }

    @Override
    public void aplicar(Aliado aliado) {
        aliado.addDanioExtra(20);
    }
}
