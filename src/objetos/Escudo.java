package objetos;

import entidades.Aliado;
import utils.Posicion;

public class Escudo extends Objeto {

    public Escudo(Posicion posicion) {
        super(posicion, 'S');
    }

    @Override
    public void aplicar(Aliado aliado) {
        aliado.addEscudo(50);
    }
}
