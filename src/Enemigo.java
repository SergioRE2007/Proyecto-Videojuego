public class Enemigo extends Entidad {  // ← HERENCIA AQUÍ

    // Constructor: usa el de la madre + lo suyo
    public Enemigo(Posicion pos) {
        super(pos, 'X');  // ← LLAMA AL CONSTRUCTOR DE Entidad
    }

    // Comportamiento ESPECIAL del enemigo
    public void perseguir() {
        System.out.println("¡Persiguiendo aliado!");
    }
}
