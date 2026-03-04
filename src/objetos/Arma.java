package objetos;

import entidades.Aliado;
import utils.Posicion;

public class Arma extends Objeto {

    private int cantidad;

    public Arma(Posicion posicion, int cantidad) {
        super(posicion, 'W');
        this.cantidad = cantidad;
    }

    @Override
    public void aplicar(Aliado aliado) {
        aliado.addDanioExtra(cantidad);
    }
}
