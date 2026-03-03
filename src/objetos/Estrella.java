package objetos;

import entidades.Aliado;
import utils.Posicion;

public class Estrella extends Objeto {

    public Estrella(Posicion posicion) {
        super(posicion, '*');
    }

    @Override
    public void aplicar(Aliado aliado) {
        aliado.setTurnosInvencible(30);
    }
}
