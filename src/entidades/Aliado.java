package entidades;

import utils.GameBoard;
import utils.Posicion;

public class Aliado extends Entidad {

    private int vision;
    private int danioBaseMin;
    private int danioBaseMax;

    private int escudo;
    private int danioExtra;
    private int turnosInvencible;
    private int turnosVelocidad;
    private int objetosRecogidosPersonal;

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

    public int getObjetosRecogidosPersonal() {
        return objetosRecogidosPersonal;
    }

    public void incrementarObjetosRecogidos() {
        objetosRecogidosPersonal++;
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
    public void actuar(GameBoard board) {
        if (turnosVelocidad > 0) turnosVelocidad--;

        int movimientos = turnosVelocidad > 0 ? 2 : 1;
        for (int m = 0; m < movimientos; m++) {
            if (!estaVivo()) break;
            realizarMovimiento(board);
        }
    }

    private void realizarMovimiento(GameBoard board) {
        if (turnosInvencible > 0) {
            turnosInvencible--;
            // Con estrella, perseguir enemigos
            Entidad masCercano = buscarCercano(Enemigo.class, vision, board);
            if (masCercano != null) {
                moverHacia(masCercano.getPosicion(), board);
            } else {
                moverRandom(board);
            }
            return;
        }

        Entidad enemigoCerca = buscarCercano(Enemigo.class, vision, board);
        Posicion objetoCerca = buscarObjetoCercano(board, vision);

        if (enemigoCerca == null) {
            if (objetoCerca != null) {
                moverHacia(objetoCerca, board);
            } else {
                moverRandom(board);
            }
        } else {
            // Hay enemigo cerca
            if (objetoCerca != null && distancia(posicion, objetoCerca) <= 2) {
                // Coger objeto solo si no nos acerca al enemigo
                int distObjetoEnemigo = distancia(objetoCerca, enemigoCerca.getPosicion());
                int distYoEnemigo = distancia(posicion, enemigoCerca.getPosicion());
                if (distObjetoEnemigo >= distYoEnemigo) {
                    moverHacia(objetoCerca, board);
                } else {
                    moverLejos(enemigoCerca.getPosicion(), board);
                }
            } else {
                moverLejos(enemigoCerca.getPosicion(), board);
            }
        }
    }

    private Posicion buscarObjetoCercano(GameBoard board, int vision) {
        Posicion mejor = null;
        int distMin = Integer.MAX_VALUE;
        int miFila = posicion.getFila();
        int miCol = posicion.getColumna();
        for (int df = -vision; df <= vision; df++) {
            for (int dc = -vision; dc <= vision; dc++) {
                int fila = miFila + df;
                int col = miCol + dc;
                if (fila >= 0 && fila < board.getFilas() && col >= 0 && col < board.getColumnas()) {
                    if (board.getObjeto(fila, col) != null) {
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
