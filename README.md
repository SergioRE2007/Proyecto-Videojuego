# Juego de Tablero en Java (DAM)

Proyecto de consola en Java donde **enemigos (X)** persiguen a **aliados (A)** en un tablero con **muros (M)** como obstáculos.  
Está pensado como ejercicio de POO para el ciclo DAM: herencia, arrays bidimensionales e IA sencilla con campo de visión.

## 🎮 Características

- Tablero de tamaño configurable (ejemplo: 20 x 60).
- Enemigos `X` que:
  - Tienen un campo de visión limitado (n casillas alrededor).
  - Buscan el aliado más cercano dentro de su visión.
  - Se mueven una casilla por turno hacia el objetivo.
  - Si no ven aliados, se mueven en una dirección aleatoria.
  - Cuando alcanzan un aliado, lo eliminan y ocupan su casilla.
- Aliados `A` que:
  - Detectan el enemigo más cercano dentro de su visión.
  - Se mueven una casilla por turno alejándose de él.
  - Si no ven enemigos, también pueden moverse aleatoriamente.
- Muros `M` que:
  - Ocupan casillas fijas del tablero.
  - No se pueden atravesar.

## 🧱 Estructura del proyecto

Clases principales:

- **App**  
  Contiene el `main`, crea el tablero, coloca enemigos, aliados y muros en posiciones aleatorias y ejecuta el bucle de turnos.

- **Posicion**  
  Representa una coordenada del tablero (`fila`, `columna`).

- **Entidad** (abstracta)  
  Clase base de todo lo que ocupa una casilla.  
  Gestiona:
  - Posición y símbolo.
  - Cálculo de distancia Manhattan.
  - Movimiento (hacia, lejos, arriba/abajo/izquierda/derecha, aleatorio).
  - Comprobación de colisiones y límites del tablero.

- **Enemigo**  
  Hereda de `Entidad`.  
  Usa el símbolo `X` y define:
  - Campo de visión.
  - Búsqueda del aliado más cercano dentro de su visión.
  - Comportamiento de persecución.

- **Aliado**  
  Hereda de `Entidad`.  
  Usa el símbolo `A` y define:
  - Campo de visión.
  - Búsqueda del enemigo más cercano dentro de su visión.
  - Comportamiento de huida.

- **Muro**  
  Hereda de `Entidad`.  
  Usa el símbolo `M` y actúa como obstáculo fijo.

## ⚙️ Parámetros configurables

En el código se pueden ajustar fácilmente:

- Número de filas y columnas del tablero.
- Número de enemigos, aliados y muros iniciales.
- Radio de visión de enemigos y aliados.
- Velocidad de la simulación (tiempo de espera entre turnos).

## ▶️ Cómo ejecutar

1. Clonar el repositorio:

```bash
git clone https://github.com/tu-usuario/tu-repo.git
cd tu-repo
