package entidades;

import objetos.Objeto;
import utils.Posicion;

public class EnemigoRapido extends Enemigo {

    public EnemigoRapido(Posicion pos, int vida, int danioMin, int danioMax, int vision) {
        super(pos, vida, danioMin, danioMax, vision);
        this.simbolo = 'R';
    }

    @Override
    public void actuar(Entidad[][] tablero, Objeto[][] objetos) {
        super.actuar(tablero, objetos);
        // Segundo movimiento (doble velocidad)
        if (estaVivo()) {
            super.actuar(tablero, objetos);
        }
    }
}
