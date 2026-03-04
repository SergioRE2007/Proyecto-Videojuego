package entidades;

import utils.GameBoard;
import utils.Posicion;

public class EnemigoRapido extends Enemigo {

    public EnemigoRapido(Posicion pos, int vida, int danioMin, int danioMax, int vision) {
        super(pos, vida, danioMin, danioMax, vision);
        this.simbolo = 'R';
    }

    @Override
    public void actuar(GameBoard board) {
        super.actuar(board);
        // Segundo movimiento (doble velocidad)
        if (estaVivo()) {
            super.actuar(board);
        }
    }
}
