import { Aliado, Enemigo, EnemigoTanque, EnemigoRapido, Muro } from './entidad.js';
import { Escudo, Arma, Estrella, Velocidad, Pocion, Trampa } from './objetos.js';

const COLORES_ESTRELLA = ['#ef4444', '#eab308', '#22c55e', '#06b6d4', '#a855f7', '#f5f5f5'];

const SPRITES_PATH = '0x72_DungeonTilesetII_v1.7/frames/';

const SPRITE_MAP = {
    aliado:     'knight_m_idle_anim_f1.png',
    aliadoStar: 'angel_idle_anim_f0.png',
    enemigo:    'goblin_idle_anim_f0.png',
    tanque:     'ogre_idle_anim_f0.png',
    rapido:     'chort_idle_anim_f0.png',
    muro:       'wall_mid.png',
    trampa:     'floor_spikes_anim_f3.png',
    escudo:     'flask_blue.png',
    arma:       'weapon_red_gem_sword.png',
    estrella:   'coin_anim_f0.png',
    velocidad:  'flask_yellow.png',
    pocion:     'flask_red.png',
    suelo:      'floor_1.png',
};

function cargarSprites() {
    const sprites = {};
    const promesas = [];
    for (const [key, file] of Object.entries(SPRITE_MAP)) {
        const img = new Image();
        img.src = SPRITES_PATH + file;
        sprites[key] = img;
        promesas.push(new Promise((resolve) => {
            img.onload = resolve;
            img.onerror = resolve; // no bloquear si falta alguno
        }));
    }
    return { sprites, ready: Promise.all(promesas) };
}

const { sprites, ready: spritesReady } = cargarSprites();
let spritesLoaded = false;
export const spritesListos = spritesReady.then(() => { spritesLoaded = true; });

export class Renderer {
    constructor(canvas, hudDiv, statsDiv) {
        this.canvas = canvas;
        this.ctx = canvas.getContext('2d');
        this.hudDiv = hudDiv;
        this.statsDiv = statsDiv;
    }

    drawBoard(board, turno) {
        const filas = board.filas;
        const columnas = board.columnas;
        const cellW = this.canvas.width / columnas;
        const cellH = this.canvas.height / filas;
        const ctx = this.ctx;

        ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        ctx.imageSmoothingEnabled = false; // pixel art nítido

        for (let f = 0; f < filas; f++) {
            for (let c = 0; c < columnas; c++) {
                const x = c * cellW;
                const y = f * cellH;

                // Suelo siempre de fondo (estirar a celda completa)
                if (spritesLoaded && sprites.suelo.complete && sprites.suelo.naturalWidth) {
                    ctx.drawImage(sprites.suelo, x, y, cellW, cellH);
                } else {
                    ctx.fillStyle = '#1a1a2e';
                    ctx.fillRect(x, y, cellW, cellH);
                }

                const e = board.getEntidad(f, c);
                const obj = board.getObjeto(f, c);
                const trampa = board.getTrampa(f, c);

                // Trampa (debajo de entidades)
                if (trampa !== null) {
                    this._drawSprite(ctx, 'trampa', x, y, cellW, cellH);
                }

                // Objetos
                if (obj !== null && e === null) {
                    if (obj instanceof Escudo) {
                        this._drawSprite(ctx, 'escudo', x, y, cellW, cellH);
                    } else if (obj instanceof Arma) {
                        this._drawSprite(ctx, 'arma', x, y, cellW, cellH);
                    } else if (obj instanceof Estrella) {
                        this._drawSprite(ctx, 'estrella', x, y, cellW, cellH);
                    } else if (obj instanceof Velocidad) {
                        this._drawSprite(ctx, 'velocidad', x, y, cellW, cellH);
                    } else if (obj instanceof Pocion) {
                        this._drawSprite(ctx, 'pocion', x, y, cellW, cellH);
                    }
                }

                // Entidades
                if (e !== null) {
                    if (e instanceof Muro) {
                        this._drawSpriteFill(ctx, 'muro', x, y, cellW, cellH);
                    } else if (e instanceof EnemigoTanque) {
                        this._drawSprite(ctx, 'tanque', x, y, cellW, cellH);
                    } else if (e instanceof EnemigoRapido) {
                        this._drawSprite(ctx, 'rapido', x, y, cellW, cellH);
                    } else if (e instanceof Enemigo) {
                        this._drawSprite(ctx, 'enemigo', x, y, cellW, cellH);
                    } else if (e instanceof Aliado) {
                        if (e.turnosInvencible > 0) {
                            this._drawSprite(ctx, 'aliadoStar', x, y, cellW, cellH);
                        } else {
                            this._drawSprite(ctx, 'aliado', x, y, cellW, cellH);
                        }
                    }
                }
            }
        }

        // Grid lines
        ctx.strokeStyle = 'rgba(0,0,0,0.15)';
        ctx.lineWidth = 0.5;
        for (let f = 0; f <= filas; f++) {
            ctx.beginPath();
            ctx.moveTo(0, f * cellH);
            ctx.lineTo(this.canvas.width, f * cellH);
            ctx.stroke();
        }
        for (let c = 0; c <= columnas; c++) {
            ctx.beginPath();
            ctx.moveTo(c * cellW, 0);
            ctx.lineTo(c * cellW, this.canvas.height);
            ctx.stroke();
        }
    }

    _drawSpriteFill(ctx, key, x, y, w, h) {
        const img = sprites[key];
        if (spritesLoaded && img && img.complete && img.naturalWidth) {
            ctx.drawImage(img, x, y, w, h);
        }
    }

    _drawSprite(ctx, key, x, y, w, h) {
        const img = sprites[key];
        if (spritesLoaded && img && img.complete && img.naturalWidth) {
            // Mantener proporcion original, centrado en la celda
            const imgW = img.naturalWidth;
            const imgH = img.naturalHeight;
            const scale = Math.min(w / imgW, h / imgH);
            const dw = imgW * scale;
            const dh = imgH * scale;
            const dx = x + (w - dw) / 2;
            const dy = y + (h - dh) / 2 - h * 0.12;
            ctx.drawImage(img, dx, dy, dw, dh);
        } else {
            const fontSize = Math.floor(Math.min(w, h) * 0.65);
            ctx.font = `bold ${fontSize}px monospace`;
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillStyle = '#e0e0e0';
            ctx.fillText('?', x + w / 2, y + h / 2);
        }
    }

    updateHUD(engine) {
        const tiempoMs = Date.now() - engine.tiempoInicio;
        const seg = Math.floor(tiempoMs / 1000);
        const min = Math.floor(seg / 60);
        const s = seg % 60;
        const tiempo = `${String(min).padStart(2, '0')}:${String(s).padStart(2, '0')}`;

        const colorAliados = engine.numAliados <= 2 ? '#ef4444' : '#22c55e';

        this.hudDiv.innerHTML = `
            <div class="hud-row">
                <span>Turno: <strong>${engine.turno}</strong></span>
                <span>Tiempo: <strong>${tiempo}</strong></span>
                <span>Velocidad: <strong>${engine.config.velocidadMs}ms</strong></span>
            </div>
            <div class="hud-row">
                <span>Aliados: <strong style="color:${colorAliados}">${engine.numAliados}/${engine.config.numAliado}</strong></span>
                <span>Enemigos: <strong style="color:#ef4444">${engine.numEnemigos}</strong></span>
                <span>Eliminados: <strong style="color:#22c55e">${engine.enemigosEliminados}</strong></span>
                <span>Objetos: <strong style="color:#06b6d4">${engine.objetosRecogidos}</strong></span>
            </div>
            <div class="hud-leyenda">
                <span style="color:#22c55e">o</span>=Aliado
                <span style="color:#ef4444">#</span>=Enemigo
                <span style="color:#dc2626">T</span>=Tanque
                <span style="color:#eab308">\u00A4</span>=R\u00E1pido
                <span style="color:#eab308">[=]</span>=Muro
                <span style="color:#9ca3af">^</span>=Trampa
                <span style="color:#06b6d4">S</span>=Escudo
                <span style="color:#a855f7">W</span>=Arma
                <span style="color:#facc15">*</span>=Estrella
                <span style="color:#3b82f6">V</span>=Vel
                <span style="color:#4ade80">+</span>=Poci\u00F3n
            </div>
        `;
    }

    mostrarEstadisticas(engine) {
        let danioAliadosInf = 0, danioAliadosRec = 0, killsAliados = 0, objRecogidos = 0;
        let aliadosVivos = 0;
        let danioEnemigosInf = 0, danioEnemigosRec = 0, killsEnemigos = 0;
        let enemigosVivos = 0;
        let mvpAliado = null, mvpEnemigo = null;

        for (const e of engine.todasEntidades) {
            if (e instanceof Aliado) {
                danioAliadosInf += e.danioInfligido;
                danioAliadosRec += e.danioRecibido;
                killsAliados += e.kills;
                objRecogidos += e.objetosRecogidosPersonal;
                if (e.estaVivo()) aliadosVivos++;
                if (mvpAliado === null || this._esMejorMvp(e, mvpAliado)) mvpAliado = e;
            } else if (e instanceof Enemigo) {
                danioEnemigosInf += e.danioInfligido;
                danioEnemigosRec += e.danioRecibido;
                killsEnemigos += e.kills;
                if (e.estaVivo()) enemigosVivos++;
                if (mvpEnemigo === null || this._esMejorMvp(e, mvpEnemigo)) mvpEnemigo = e;
            }
        }

        const tiempoMs = Date.now() - engine.tiempoInicio;
        const seg = Math.floor(tiempoMs / 1000);
        const tiempo = `${String(Math.floor(seg / 60)).padStart(2, '0')}:${String(seg % 60).padStart(2, '0')}`;

        let textoResultado, colorResultado;
        switch (engine.resultado) {
            case "aliados":
                textoResultado = "Los aliados han ganado";
                colorResultado = "#22c55e";
                break;
            case "enemigos":
                textoResultado = "Los enemigos han ganado";
                colorResultado = "#ef4444";
                break;
            default:
                textoResultado = "Empate";
                colorResultado = "#eab308";
                break;
        }

        let mvpAliadoHTML = '';
        if (mvpAliado !== null) {
            mvpAliadoHTML = `
                <div class="stats-section-title" style="color:#22c55e">MVP ALIADO</div>
                <div class="stats-line">Aliado #${mvpAliado.id} \u2014 ${mvpAliado.kills} kills, ${mvpAliado.danioInfligido} da\u00F1o</div>
            `;
        }
        let mvpEnemigoHTML = '';
        if (mvpEnemigo !== null) {
            mvpEnemigoHTML = `
                <div class="stats-section-title" style="color:#ef4444">MVP ENEMIGO</div>
                <div class="stats-line">Enemigo #${mvpEnemigo.id} \u2014 ${mvpEnemigo.kills} kills, ${mvpEnemigo.danioInfligido} da\u00F1o</div>
            `;
        }

        this.statsDiv.innerHTML = `
            <div class="stats-header">ESTADISTICAS DE PARTIDA</div>
            <div class="stats-separator"></div>
            <div class="stats-line">Resultado: <strong style="color:${colorResultado}">${textoResultado}</strong></div>
            <div class="stats-line">Turnos: ${engine.turno} | Tiempo: ${tiempo}</div>
            <div class="stats-separator"></div>
            <div class="stats-section-title" style="color:#22c55e">ALIADOS</div>
            <div class="stats-line">Da\u00F1o infligido total: ${danioAliadosInf}</div>
            <div class="stats-line">Da\u00F1o recibido total: ${danioAliadosRec}</div>
            <div class="stats-line">Supervivientes: ${aliadosVivos}/${engine.numAliadosInicial}</div>
            <div class="stats-line">Kills totales: ${killsAliados}</div>
            <div class="stats-line">Objetos recogidos: ${objRecogidos}</div>
            <div class="stats-separator"></div>
            <div class="stats-section-title" style="color:#ef4444">ENEMIGOS</div>
            <div class="stats-line">Da\u00F1o infligido total: ${danioEnemigosInf}</div>
            <div class="stats-line">Da\u00F1o recibido total: ${danioEnemigosRec}</div>
            <div class="stats-line">Supervivientes: ${enemigosVivos}/${engine.numEnemigosInicial}</div>
            <div class="stats-line">Kills totales: ${killsEnemigos}</div>
            <div class="stats-separator"></div>
            ${mvpAliadoHTML}
            ${mvpEnemigoHTML}
        `;
        this.statsDiv.style.display = 'block';
    }

    _esMejorMvp(candidato, actual) {
        if (candidato.kills !== actual.kills) return candidato.kills > actual.kills;
        return candidato.danioInfligido > actual.danioInfligido;
    }
}
