# Juego de Tablero en Java

Simulador de consola en Java donde **enemigos** persiguen a **aliados** en un tablero con **muros** como obstáculos.
Los aliados pueden recoger **objetos** (escudos, armas, estrellas) para sobrevivir.
Pensado como ejercicio de POO para el ciclo DAM: herencia, paquetes, arrays bidimensionales e IA con campo de visión.

## Aspecto en consola

```
[=][=][=][=][=][=][=][=][=][=][=][=][=][=][=]...
[=] .  .  .  S  .  o  .  .  .  .  .  .  . [=]
[=] .  #  .  .  o  .  .  W  .  .  .  .  . [=]
[=] .  #  . [=][=][=] .  o  .  .  o  .  . [=]
[=] .  .  .  .  .  *  .  .  .  .  .  .  . [=]
```

| Simbolo | Entidad/Objeto | Comportamiento |
|---------|----------------|----------------|
| ` # `   | Enemigo        | Persigue al aliado mas cercano dentro de su vision |
| ` o `   | Aliado         | Huye de enemigos, busca objetos activamente |
| `[=]`   | Muro           | Obstaculo fijo, no se puede atravesar |
| ` S `   | Escudo         | Absorbe daño al aliado que lo recoge |
| ` W `   | Arma           | Aumenta el contraataque del aliado |
| ` * `   | Estrella       | Invencibilidad temporal (efecto rainbow al moverse) |

## Caracteristicas

- Tablero configurable con **borde solido** alrededor.
- **Movimiento en 8 direcciones** (cardinales y diagonales).
- **Sistema de combate**: los enemigos atacan aliados al contacto, los aliados contraatacan automaticamente.
- **Objetos recogibles**: escudos, armas y estrellas que aparecen en el mapa y se regeneran cada N turnos.
- **IA inteligente de aliados**: buscan objetos activamente, pero priorizan huir si hay un enemigo cerca (salvo que el objeto les pille de camino).
- **Estrella de invencibilidad**: el aliado con estrella persigue enemigos y los mata al contacto, con efecto de colores ciclicos tipo Mario.
- **Anti-oscilacion**: las entidades evitan volver a la casilla anterior, eliminando el bug de quedarse yendo de un lado a otro.
- Enemigos y aliados **rodean obstaculos automaticamente**.
- Cada entidad actua **exactamente una vez por turno**.
- **Renderizado sin parpadeo**: el frame se construye en memoria y se vuelca de golpe.
- Generacion de muros **lineales con giros**: cada pared mantiene una direccion y tiene un 20% de probabilidad de girar.

## Parametros configurables (en App.java)

```java
// Tablero
final int FILAS = 20;
final int COLUMNAS = 20;
final int NUM_ENEMIGO = 30;
final int NUM_ALIADO = 20;
final int NUM_MURO = 60;
final int PROB_PEGAR_MURO = 70;

// Objetos
final int NUM_ESCUDO = 5;
final int NUM_ARMA = 3;
final int NUM_ESTRELLA = 2;
final int TURNOS_SPAWN_OBJETO = 150;

// Stats de aliados
final int VIDA_ALIADO = 100;
final int DANIO_BASE_ALIADO_MIN = 3;
final int DANIO_BASE_ALIADO_MAX = 5;
final int VISION_ALIADO = 5;

// Stats de enemigos
final int VIDA_ENEMIGO = 100;
final int DANIO_ENEMIGO_MIN = 20;
final int DANIO_ENEMIGO_MAX = 30;
final int VISION_ENEMIGO = 5;
```

## Estructura del proyecto

```
src/
├── App.java              main: tablero, game loop, renderizado
├── entidades/
│   ├── Entidad.java      clase abstracta: movimiento, combate, anti-oscilacion
│   ├── Enemigo.java      persecucion con vision y daño configurable
│   ├── Aliado.java       huida, busqueda de objetos, invencibilidad
│   └── Muro.java         obstaculo fijo
├── objetos/
│   ├── Objeto.java       clase abstracta de objetos recogibles
│   ├── Escudo.java       absorbe daño
│   ├── Arma.java         aumenta contraataque
│   └── Estrella.java     invencibilidad temporal
└── utils/
    └── Posicion.java     coordenada (fila, columna)
bin/                      bytecode compilado
```

## Compilar y ejecutar

```bash
javac -d bin src/utils/*.java src/objetos/*.java src/entidades/*.java src/App.java
java -cp bin App
```
