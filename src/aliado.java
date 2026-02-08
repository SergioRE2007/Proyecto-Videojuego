public class aliado {

    // 1. Atributos (datos)
    private Posicion posicion;
    private boolean vivo;
    private char simbolo;

    // 2. Constructor (cómo nace un aliado)
    public aliado(Posicion posicionInicial) {
        this.posicion = posicionInicial; // empieza en esa casilla
        this.vivo = true;                // nace vivo
        this.simbolo = 'Y';              // siempre se pinta como X
    }

    // 3. Métodos para leer datos (getters)
    public Posicion getPosicion() {
        return posicion;
    }

    public boolean estaVivo() {
        return vivo;
    }

    public char getSimbolo() {
        return simbolo;
    }

    // 4. Métodos para cambiar el estado (setters / acciones internas)
    public void morir() {
        this.vivo = false;
        this.simbolo = 'X'; // por ejemplo, se marca como muerto
    }

    public void moverArriba() {
        int filaActual = posicion.getFila();
        posicion.setFila(filaActual - 1);
    }

    public void moverAbajo() {
        int filaActual = posicion.getFila();
        posicion.setFila(filaActual + 1);
    }

    public void moverIzquierda() {
        int colActual = posicion.getColumna();
        posicion.setColumna(colActual - 1);
    }

    public void moverDerecha() {
        int colActual = posicion.getColumna();
        posicion.setColumna(colActual + 1);
    }
}
