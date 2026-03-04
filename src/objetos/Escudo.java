package objetos;

import entidades.Aliado;
import utils.Posicion;

public class Escudo extends Objeto {

    private int cantidad;

    public Escudo(Posicion posicion, int cantidad) {
        super(posicion, 'S');
        this.cantidad = cantidad;
    }

    @Override
    public void aplicar(Aliado aliado) {
        aliado.addEscudo(cantidad);
    }
}
