import { Aliado, Enemigo, Muro } from './entidad.js';

const DIRS_WASD = {
    w: [-1, 0],
    s: [1, 0],
    a: [0, -1],
    d: [0, 1],
};

export class Jugador extends Aliado {
    constructor(fila, columna, vida, danioMin, danioMax, vision) {
        super(fila, columna, vida, danioMin, danioMax, vision);
        this.simbolo = 'J';
        this.dinero = 0;
        this.armaActual = 'espada'; // 'espada' | 'arco'
        this.cooldownAtaque = 0;
        this.direccion = [0, 1]; // ultima dir WASD (default: derecha)
        this.cooldownEspada = 3;
        this.cooldownArco = 5;
        this.rangoArco = 5;
    }

    // Override — el jugador no tiene IA, solo decrementa cooldowns
    actuar(board) {
        if (this.turnosInvencible > 0) this.turnosInvencible--;
        if (this.turnosVelocidad > 0) this.turnosVelocidad--;
        if (this.cooldownAtaque > 0) this.cooldownAtaque--;
    }

    moverWASD(tecla, board) {
        const dir = DIRS_WASD[tecla];
        if (!dir) return false;
        this.direccion = dir;
        const nf = this.fila + dir[0];
        const nc = this.columna + dir[1];
        return this._moverSiPosible(nf, nc, board);
    }

    atacarEspada(board) {
        if (this.cooldownAtaque > 0) return [];
        this.cooldownAtaque = this.cooldownEspada;

        const kills = [];
        // 8 celdas adyacentes
        for (let df = -1; df <= 1; df++) {
            for (let dc = -1; dc <= 1; dc++) {
                if (df === 0 && dc === 0) continue;
                const f = this.fila + df;
                const c = this.columna + dc;
                if (f < 0 || f >= board.filas || c < 0 || c >= board.columnas) continue;
                const e = board.getEntidad(f, c);
                if (e instanceof Enemigo) {
                    const danio = this.danioBaseMin + Math.floor(Math.random() * (this.danioBaseMax - this.danioBaseMin + 1)) + this.danioExtra;
                    this.danioInfligido += danio;
                    e.recibirDanio(danio);
                    if (!e.estaVivo()) {
                        this.kills++;
                        board.setEntidad(f, c, null);
                        kills.push(e);
                    }
                }
            }
        }
        return kills;
    }

    atacarArco(board) {
        if (this.cooldownAtaque > 0) return [];
        this.cooldownAtaque = this.cooldownArco;

        const kills = [];
        const [df, dc] = this.direccion;
        for (let i = 1; i <= this.rangoArco; i++) {
            const f = this.fila + df * i;
            const c = this.columna + dc * i;
            if (f < 0 || f >= board.filas || c < 0 || c >= board.columnas) break;
            if (board.esVacio && board.esVacio(f, c)) break;

            const e = board.getEntidad(f, c);
            if (e instanceof Muro) break; // muros bloquean flechas

            if (e instanceof Enemigo) {
                const danio = this.danioBaseMin + Math.floor(Math.random() * (this.danioBaseMax - this.danioBaseMin + 1)) + this.danioExtra;
                this.danioInfligido += danio;
                e.recibirDanio(danio);
                if (!e.estaVivo()) {
                    this.kills++;
                    board.setEntidad(f, c, null);
                    kills.push(e);
                }
                break; // solo primer enemigo
            }
        }
        return kills;
    }

    atacar(board) {
        if (this.armaActual === 'espada') {
            return this.atacarEspada(board);
        } else {
            return this.atacarArco(board);
        }
    }

    cambiarArma() {
        this.armaActual = this.armaActual === 'espada' ? 'arco' : 'espada';
    }
}
