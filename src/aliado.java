public class Aliado extends Entidad {

    public Aliado(Posicion pos) {
        super(pos, 'A');  // ← HERENCIA: usa constructor del padre
    }

    public void huir() {
        System.out.println("¡Huyendo del enemigo!");
    }
}
