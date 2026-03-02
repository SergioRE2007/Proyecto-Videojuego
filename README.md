# Juego de Tablero en Java

Simulador de consola en Java donde **enemigos** persiguen a **aliados** en un tablero con **muros** como obstáculos.
Pensado como ejercicio de POO para el ciclo DAM: herencia, arrays bidimensionales e IA sencilla con campo de visión.

## Aspecto en consola

```
[=][=][=][=][=][=][=][=][=][=][=][=][=][=][=]...
[=] .  .  .  .  .  o  .  .  .  .  .  .  . [=]
[=] .  #  .  .  o  .  .  .  .  .  .  .  . [=]
[=] .  #  . [=][=][=] .  o  .  .  o  .  . [=]
[=] .  .  .  .  .  .  .  .  .  .  .  .  . [=]
```

| Símbolo | Entidad  | Comportamiento |
|---------|----------|----------------|
| ` # `   | Enemigo  | Persigue al aliado más cercano dentro de su visión |
| ` o `   | Aliado   | Huye del enemigo más cercano dentro de su visión   |
| `[=]`   | Muro     | Obstáculo fijo, no se puede atravesar              |

## Características

- Tablero configurable con **borde sólido** alrededor.
- **Movimiento en 8 direcciones** (cardinales y diagonales).
- Enemigos y aliados **rodean obstáculos automáticamente** — si el camino principal está bloqueado, intentan las direcciones alternativas.
- Cada entidad actúa **exactamente una vez por turno**, sin teletransportaciones ni entidades fantasma.
- **Renderizado sin parpadeo**: el frame se construye en memoria y se vuelca de golpe.
- Generación de muros **lineales con giros**: cada pared mantiene una dirección y tiene un 20% de probabilidad de girar, creando formas naturales.

## Parámetros configurables (en App.java)

```java
final int FILAS = 20;
final int COLUMNAS = 40;
final int NUM_ENEMIGO = 20;
final int NUM_ALIADO = 100;
final int NUM_MURO = 60;
final int PROB_PEGAR_MURO = 70; // % de que un muro nuevo se pegue a uno existente
```

- `PROB_PEGAR_MURO = 0` → muros completamente dispersos
- `PROB_PEGAR_MURO = 70` → paredes largas con ramas
- `PROB_PEGAR_MURO = 100` → una sola pared continua

## Estructura del proyecto

```
src/
├── App.java        main: inicializa el tablero y ejecuta el bucle de turnos
├── Entidad.java    clase abstracta base: movimiento en 8 dirs, colisiones, distancia
├── Enemigo.java    hereda de Entidad: campo de visión, persecución
├── Aliado.java     hereda de Entidad: campo de visión, huida
├── Muro.java       hereda de Entidad: obstáculo fijo
└── Posicion.java   coordenada (fila, columna)
bin/               bytecode compilado
```

## Compilar y ejecutar

```bash
javac -d bin src/*.java
java -cp bin App
```
