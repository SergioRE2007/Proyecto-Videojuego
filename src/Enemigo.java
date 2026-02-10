public class Enemigo extends Entidad { // ← HERENCIA AQUÍ

    private static final int VISION = 10;

    public Enemigo(Posicion pos) {
        super(pos, 'X'); 
    }

    public void actuar(Entidad[][] tablero) {
        Aliado masCercanoEnVision = buscarAliadoEnVision(tablero);
        if (masCercanoEnVision != null) {
            moverHacia(masCercanoEnVision.getPosicion(), tablero);
        } else {
            moverRandom(tablero);
        }
    }
    private Aliado buscarAliadoEnVision(Entidad[][] tablero) {
    Aliado mejor = null;
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
                if (e instanceof Aliado) {
                    Aliado aliado = (Aliado) e;
                    int dist = distanciaManhattan(getPosicion(), aliado.getPosicion());
                    
                    if (dist < distMin) {
                        distMin = dist;
                        mejor = aliado;
                    }
                }
            }
        }
    }
    return mejor;
}

}
