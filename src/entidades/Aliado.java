package entidades;

import objetos.Objeto;
import utils.Posicion;

public class Aliado extends Entidad {

    private int vision;
    private int danioBaseMin;
    private int danioBaseMax;

    private int escudo;
    private int danioExtra;
    private int turnosInvencible;
    private int turnosVelocidad;

    public Aliado(Posicion pos, int vida, int danioBaseMin, int danioBaseMax, int vision) {
        super(pos, 'A', vida);
        this.danioBaseMin = danioBaseMin;
        this.danioBaseMax = danioBaseMax;
        this.vision = vision;
        this.escudo = 0;
        this.danioExtra = 0;
        this.turnosInvencible = 0;
        this.turnosVelocidad = 0;
    }

    public void addEscudo(int cantidad) {
        this.escudo += cantidad;
    }

    public void addDanioExtra(int cantidad) {
        this.danioExtra += cantidad;
    }

    public void setTurnosInvencible(int turnos) {
        this.turnosInvencible = turnos;
    }

    public int getTurnosInvencible() {
        return turnosInvencible;
    }

    public void setTurnosVelocidad(int turnos) {
        this.turnosVelocidad = turnos;
    }

    public int getTurnosVelocidad() {
        return turnosVelocidad;
    }

    public int getDanioExtra() {
        return danioExtra;
    }

    public int getDanioBaseMin() {
        return danioBaseMin;
    }

    public int getDanioBaseMax() {
        return danioBaseMax;
    }

    public int getVision() {
        return vision;
    }

    public void curar(int cantidad) {
        vida = Math.min(vida + cantidad, vidaMax);
    }

    @Override
    public void recibirDanio(int danio) {
        if (turnosInvencible > 0) {
            return;
        }
        if (escudo > 0) {
            if (danio <= escudo) {
                escudo -= danio;
                return;
            } else {
                danio -= escudo;
                escudo = 0;
            }
        }
        vida -= danio;
        if (vida < 0) vida = 0;
    }

    @Override
    public void actuar(Entidad[][] tablero, Objeto[][] objetos) {
        if (turnosVelocidad > 0) turnosVelocidad--;

        int movimientos = turnosVelocidad > 0 ? 2 : 1;
        for (int m = 0; m < movimientos; m++) {
            if (!estaVivo()) break;
            realizarMovimiento(tablero, objetos);
        }
    }

    private void realizarMovimiento(Entidad[][] tablero, Objeto[][] objetos) {
        if (turnosInvencible > 0) {
            turnosInvencible--;
            // Con estrella, perseguir enemigos
            Entidad masCercano = buscarCercano(Enemigo.class, vision, tablero);
            if (masCercano != null) {
                moverHacia(masCercano.getPosicion(), tablero);
            } else {
                moverRandom(tablero);
            }
            return;
        }

        Entidad enemigoCerca = buscarCercano(Enemigo.class, vision, tablero);
        Posicion objetoCerca = buscarObjetoCercano(objetos, vision);

        if (enemigoCerca == null) {
            if (objetoCerca != null) {
                moverHacia(objetoCerca, tablero);
            } else {
                moverRandom(tablero);
            }
        } else {
            // Hay enemigo cerca
            if (objetoCerca != null && distancia(posicion, objetoCerca) <= 2) {
                // Coger objeto solo si no nos acerca al enemigo
                int distObjetoEnemigo = distancia(objetoCerca, enemigoCerca.getPosicion());
                int distYoEnemigo = distancia(posicion, enemigoCerca.getPosicion());
                if (distObjetoEnemigo >= distYoEnemigo) {
                    moverHacia(objetoCerca, tablero);
                } else {
                    moverLejos(enemigoCerca.getPosicion(), tablero);
                }
            } else {
                moverLejos(enemigoCerca.getPosicion(), tablero);
            }
        }
    }

    private Posicion buscarObjetoCercano(Objeto[][] objetos, int vision) {
        Posicion mejor = null;
        int distMin = Integer.MAX_VALUE;
        int miFila = posicion.getFila();
        int miCol = posicion.getColumna();
        for (int df = -vision; df <= vision; df++) {
            for (int dc = -vision; dc <= vision; dc++) {
                int fila = miFila + df;
                int col = miCol + dc;
                if (fila >= 0 && fila < objetos.length && col >= 0 && col < objetos[0].length) {
                    if (objetos[fila][col] != null) {
                        Posicion p = new Posicion(fila, col);
                        int dist = distancia(posicion, p);
                        if (dist < distMin) {
                            distMin = dist;
                            mejor = p;
                        }
                    }
                }
            }
        }
        return mejor;
    }
}
