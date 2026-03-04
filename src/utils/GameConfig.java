package utils;

public class GameConfig {
    // General
    public long semilla = -1;
    public int velocidadMs = 100;

    // Mapa
    public int filas = 20;
    public int columnas = 37;
    public String tipoMapa = "laberinto"; //  "abierto" "salas" "laberinto" "arena":

    public int numMuro = 60;
    public int probPegarMuro = 70;

    // Aliados
    public int numAliado = 10;
    public int vidaAliado = 100;
    public int danioBaseAliadoMin = 10;
    public int danioBaseAliadoMax = 30;
    public int visionAliado = 5;

    // Enemigo Normal
    public int numEnemigo = 10;
    public int vidaEnemigo = 100;
    public int danioEnemigoMin = 20;
    public int danioEnemigoMax = 50;
    public int visionEnemigo = 5;

    // Enemigo Tanque
    public int numEnemigoTanque = 1;
    public int vidaTanque = 3000;
    public int danioTanqueMin = 100;
    public int danioTanqueMax = 200;
    public int visionTanque = 10;

    // Enemigo Rapido
    public int numEnemigoRapido = 2;
    public int vidaRapido = 200;
    public int danioRapidoMin = 100;
    public int danioRapidoMax = 100;
    public int visionRapido = 10;

    // Trampas
    public int numTrampa = 15;
    public int danioTrampa = 80;

    // Objetos
    public int turnosSpawnObjeto = 1;
    public int numEscudo = 5;
    public int numArma = 3;
    public int numEstrella = 0;
    public int numVelocidad = 3;
    public int numPocion = 3;

    // Valores de objetos
    public int valorEscudo = 50;
    public int valorArma = 20;
    public int turnosEstrella = 30;
    public int duracionVelocidad = 30;
    public int curacionPocion = 50;
}
