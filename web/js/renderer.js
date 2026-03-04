import { Aliado, Enemigo, EnemigoTanque, EnemigoRapido, Muro } from './entidad.js';
import { Escudo, Arma, Estrella, Velocidad, Pocion, Trampa } from './objetos.js';

const COLORES_ESTRELLA = ['#ef4444', '#eab308', '#22c55e', '#06b6d4', '#a855f7', '#f5f5f5'];

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

        const fontSize = Math.floor(Math.min(cellW, cellH) * 0.65);
        ctx.font = `bold ${fontSize}px monospace`;
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';

        for (let f = 0; f < filas; f++) {
            for (let c = 0; c < columnas; c++) {
                const x = c * cellW;
                const y = f * cellH;
                const cx = x + cellW / 2;
                const cy = y + cellH / 2;

                const e = board.getEntidad(f, c);

                if (e !== null) {
                    if (e instanceof Muro) {
                        ctx.fillStyle = '#78716c';
                        ctx.fillRect(x, y, cellW, cellH);
                        ctx.fillStyle = '#eab308';
                        ctx.fillText('[=]', cx, cy);
                    } else if (e instanceof EnemigoTanque) {
                        ctx.fillStyle = '#1a1a2e';
                        ctx.fillRect(x, y, cellW, cellH);
                        ctx.fillStyle = '#dc2626';
                        ctx.fillText('T', cx, cy);
                    } else if (e instanceof EnemigoRapido) {
                        ctx.fillStyle = '#1a1a2e';
                        ctx.fillRect(x, y, cellW, cellH);
                        ctx.fillStyle = '#eab308';
                        ctx.fillText('\u00A4', cx, cy);
                    } else if (e instanceof Enemigo) {
                        ctx.fillStyle = '#1a1a2e';
                        ctx.fillRect(x, y, cellW, cellH);
                        ctx.fillStyle = '#ef4444';
                        ctx.fillText('#', cx, cy);
                    } else if (e instanceof Aliado) {
                        ctx.fillStyle = '#1a1a2e';
                        ctx.fillRect(x, y, cellW, cellH);
                        if (e.turnosInvencible > 0) {
                            ctx.fillStyle = COLORES_ESTRELLA[turno % COLORES_ESTRELLA.length];
                        } else {
                            ctx.fillStyle = '#22c55e';
                        }
                        ctx.fillText('o', cx, cy);
                    } else {
                        ctx.fillStyle = '#1a1a2e';
                        ctx.fillRect(x, y, cellW, cellH);
                    }
                } else if (board.getObjeto(f, c) !== null) {
                    ctx.fillStyle = '#1a1a2e';
                    ctx.fillRect(x, y, cellW, cellH);
                    const obj = board.getObjeto(f, c);
                    if (obj instanceof Escudo) {
                        ctx.fillStyle = '#06b6d4';
                        ctx.fillText('S', cx, cy);
                    } else if (obj instanceof Arma) {
                        ctx.fillStyle = '#a855f7';
                        ctx.fillText('W', cx, cy);
                    } else if (obj instanceof Velocidad) {
                        ctx.fillStyle = '#3b82f6';
                        ctx.fillText('V', cx, cy);
                    } else if (obj instanceof Estrella) {
                        ctx.fillStyle = '#facc15';
                        ctx.fillText('*', cx, cy);
                    } else if (obj instanceof Pocion) {
                        ctx.fillStyle = '#4ade80';
                        ctx.fillText('+', cx, cy);
                    } else {
                        ctx.fillStyle = '#ffffff';
                        ctx.fillText(obj.simbolo, cx, cy);
                    }
                } else if (board.getTrampa(f, c) !== null) {
                    ctx.fillStyle = '#1a1a2e';
                    ctx.fillRect(x, y, cellW, cellH);
                    ctx.fillStyle = '#9ca3af';
                    ctx.fillText('^', cx, cy);
                } else {
                    ctx.fillStyle = '#1a1a2e';
                    ctx.fillRect(x, y, cellW, cellH);
                    ctx.fillStyle = '#333347';
                    ctx.fillText('.', cx, cy);
                }
            }
        }

        // Grid lines
        ctx.strokeStyle = '#2a2a3e';
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
