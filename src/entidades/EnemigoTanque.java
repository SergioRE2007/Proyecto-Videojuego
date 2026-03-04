package entidades;

import utils.GameBoard;
import utils.Posicion;

public class EnemigoTanque extends Enemigo {

    private int turnoInterno = 0;

    public EnemigoTanque(Posicion pos, int vida, int danioMin, int danioMax, int vision) {
        super(pos, vida, danioMin, danioMax, vision);
        this.simbolo = 'T';
    }

    @Override
    public void actuar(GameBoard board) {
        turnoInterno++;
        if (turnoInterno % 2 != 0) return; // solo actua en turnos pares
        super.actuar(board);
    }
}
