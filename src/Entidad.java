public abstract class Entidad {
    // ✅ COMÚN a TODAS las entidades
    protected Posicion posicion;
    protected char simbolo;

    // Constructor (se llama automáticamente en las hijas)
    public Entidad(Posicion pos, char simbolo) {
        this.posicion = pos;
        this.simbolo = simbolo;
    }

    // Métodos que TODAS saben hacer
    public Posicion getPosicion() {
        return posicion;
    }

    public char getSimbolo() {
        return simbolo;
    }
}
