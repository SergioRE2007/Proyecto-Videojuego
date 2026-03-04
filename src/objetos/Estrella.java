package objetos;

import entidades.Aliado;
import utils.Posicion;

public class Estrella extends Objeto {

    private int turnos;

    public Estrella(Posicion posicion, int turnos) {
        super(posicion, '*');
        this.turnos = turnos;
    }

    @Override
    public void aplicar(Aliado aliado) {
        aliado.setTurnosInvencible(turnos);
    }
}
