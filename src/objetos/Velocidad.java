package objetos;

import entidades.Aliado;
import utils.Posicion;

public class Velocidad extends Objeto {

    private int duracion;

    public Velocidad(Posicion posicion, int duracion) {
        super(posicion, 'V');
        this.duracion = duracion;
    }

    @Override
    public void aplicar(Aliado aliado) {
        aliado.setTurnosVelocidad(duracion);
    }
}
