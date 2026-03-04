package entidades;

import objetos.Objeto;
import utils.Posicion;

public class EnemigoTanque extends Enemigo {

    private int turnoInterno = 0;

    public EnemigoTanque(Posicion pos, int vida, int danioMin, int danioMax, int vision) {
        super(pos, vida, danioMin, danioMax, vision);
        this.simbolo = 'T';
    }

    @Override
    public void actuar(Entidad[][] tablero, Objeto[][] objetos) {
        turnoInterno++;
        if (turnoInterno % 2 != 0) return; // solo actúa en turnos pares
        super.actuar(tablero, objetos);
    }
}
