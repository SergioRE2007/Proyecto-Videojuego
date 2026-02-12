public class Aliado extends Entidad {

    public Aliado(Posicion pos) {
        super(pos, 'A');
    }

private static final int VISION = 5;

    public void actuar(Entidad[][] tablero) {
        Enemigo masCercanoEnVision = buscarEnemigoEnVision(tablero);
        if (masCercanoEnVision != null) {
            moverLejos(masCercanoEnVision.getPosicion(), tablero);
        } else{
            moverRandom(tablero);
        }
    }

    private Enemigo buscarEnemigoEnVision(Entidad[][] tablero) {
        Enemigo mejor = null;
        int distMin = Integer.MAX_VALUE;
        int miFila = getPosicion().getFila();
        int miCol = getPosicion().getColumna();
        for (int df = -VISION; df <= VISION; df++) {
            for (int dc = -VISION; dc <= VISION; dc++) {
                int fila = miFila + df;
                int col = miCol + dc;
                if (fila >= 0 && fila < tablero.length &&
                        col >= 0 && col < tablero[0].length) {
                    Entidad e = tablero[fila][col];
                    if (e instanceof Enemigo) {
                        Enemigo enemigo = (Enemigo) e;
                        int dist = distancia(getPosicion(), enemigo.getPosicion());
                        if (dist < distMin) {
                            distMin = dist;
                            mejor = enemigo;
                        }
                    }
                }
            }
        }
        return mejor;
    }
}
