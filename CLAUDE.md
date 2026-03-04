# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Console-based board game in Java where enemies pursue allies on a grid. Allies collect power-ups (shields, weapons, stars) to survive. Educational OOP project for the DAM cycle. All code is in Spanish.

## Build & Run

```bash
# Compile (order matters: utils → objetos → entidades → App)
javac -d bin src/utils/*.java src/objetos/*.java src/entidades/*.java src/App.java

# Run
java -cp bin App
```

No build tool (Maven/Gradle) — just javac. Output goes to `bin/` (gitignored). No test framework.

## Architecture

Two parallel inheritance hierarchies:

- **Entidades** (`src/entidades/`): `Entidad` (abstract) → `Enemigo`, `Aliado`, `Muro`
- **Objetos** (`src/objetos/`): `Objeto` (abstract) → `Escudo`, `Arma`, `Estrella`

**Board state** lives in `App.java` as two 2D arrays: `Entidad[][] tablero` (entities) and `Objeto[][] objetos` (collectables). The board has a solid wall border.

**Game loop** (`App.java`): snapshot entities → move sequentially → resolve collisions → spawn objects periodically → render frame → check win/loss/draw → sleep.

**Key mechanics in `Entidad.java`:**
- 8-directional movement with Manhattan distance sorting
- Anti-oscillation system (tracks previous position, avoids returning unless trapped)
- Combat: enemy attacks ally → damage + counterattack; invincible ally → instant kill

**AI behaviors:**
- `Enemigo`: pursues nearest ally within vision radius 5, otherwise random movement
- `Aliado`: flees enemies, collects nearby objects opportunistically; with star active, pursues and kills enemies

**Rendering:** ANSI escape codes for colors, StringBuilder for flicker-free single-flush output.

## Configuration

All game parameters are constants at the top of `App.java` (board size, entity counts, stats, spawn intervals). Modify these to tune gameplay.
