import config from './config.js';
import { GameEngine } from './engine.js';
import { Renderer } from './renderer.js';
import { Aliado, Enemigo, EnemigoTanque, EnemigoRapido, Muro } from './entidad.js';
import { Escudo, Arma, Estrella, Velocidad, Pocion, Trampa } from './objetos.js';

const canvas = document.getElementById('gameCanvas');
const hudDiv = document.getElementById('hud');
const statsDiv = document.getElementById('stats');
const btnIniciar = document.getElementById('btnIniciar');
const btnPausa = document.getElementById('btnPausa');
const btnFinalizar = document.getElementById('btnFinalizar');

const engine = new GameEngine(config);
let renderer = null;
let intervalId = null;
let pausado = false;
let enSetup = true; // true = partida generada pero no iniciada

// Claves que al cambiar regeneran el tablero en modo setup
const CLAVES_GENERACION = new Set([
    'semilla', 'filas', 'columnas', 'tipoMapa', 'numMuro', 'probPegarMuro',
    'numAliado', 'vidaAliado', 'danioBaseAliadoMin', 'danioBaseAliadoMax', 'visionAliado',
    'numEnemigo', 'vidaEnemigo', 'danioEnemigoMin', 'danioEnemigoMax', 'visionEnemigo',
    'numEnemigoTanque', 'vidaTanque', 'danioTanqueMin', 'danioTanqueMax', 'visionTanque',
    'numEnemigoRapido', 'vidaRapido', 'danioRapidoMin', 'danioRapidoMax', 'visionRapido',
    'numTrampa', 'danioTrampa',
    'numEscudo', 'numArma', 'numEstrella', 'numVelocidad', 'numPocion',
    'valorEscudo', 'valorArma', 'turnosEstrella', 'duracionVelocidad', 'curacionPocion',
]);

// ==================== Panel de ajustes → config ====================

function syncPanelToConfig() {
    document.querySelectorAll('#panel [data-key]').forEach(el => {
        if (el.type === 'checkbox') {
            el.checked = config[el.dataset.key];
        } else {
            el.value = config[el.dataset.key];
        }
    });
}

function onPanelChange(e) {
    const el = e.target;
    const key = el.dataset.key;
    if (!key) return;
    if (el.type === 'checkbox') {
        config[key] = el.checked;
    } else {
        config[key] = el.tagName === 'SELECT' ? el.value : Number(el.value);
    }

    // Cambiar velocidad en tiempo real
    if (key === 'velocidadMs' && intervalId !== null) {
        clearInterval(intervalId);
        intervalId = setInterval(tickLoop, config.velocidadMs);
    }

    // Mapa vacio: poner todo a 0 y activar modo libre
    if (key === 'tipoMapa' && config.tipoMapa === 'vacio') {
        const clavesACero = [
            'numAliado', 'numEnemigo', 'numEnemigoTanque', 'numEnemigoRapido',
            'numMuro', 'numTrampa',
            'numEscudo', 'numArma', 'numEstrella', 'numVelocidad', 'numPocion',
            'turnosSpawnObjeto',
        ];
        for (const k of clavesACero) {
            config[k] = 0;
        }
        config.modoLibre = true;
        syncPanelToConfig();
    }

    if (enSetup && CLAVES_GENERACION.has(key)) {
        generarPartida();
    }
}

document.getElementById('panel').addEventListener('input', onPanelChange);
document.getElementById('panel').addEventListener('change', onPanelChange);

syncPanelToConfig();

// ==================== Canvas ====================

function resizeCanvas() {
    canvas.width = config.columnas * 24;
    canvas.height = config.filas * 24;
}

// ==================== Generar partida (sin iniciar) ====================

function generarPartida() {
    if (intervalId !== null) {
        clearInterval(intervalId);
        intervalId = null;
    }

    resizeCanvas();
    engine.config = config;
    engine.resultado = null;
    engine.inicializar();

    if (!renderer) {
        renderer = new Renderer(canvas, hudDiv, statsDiv);
    }
    renderer.drawBoard(engine.board, engine.turno);
    renderer.updateHUD(engine);

    statsDiv.style.display = 'none';
    btnIniciar.textContent = 'INICIAR PARTIDA';
    btnIniciar.style.display = '';
    btnPausa.style.display = 'none';
    btnFinalizar.style.display = 'none';
    pausado = false;
    enSetup = true;
}

// Generar al cargar la pagina
generarPartida();

// ==================== Toolbox (colocar) ====================

let toolSeleccionada = null;

document.querySelectorAll('.tool-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        const tool = btn.dataset.tool;
        if (toolSeleccionada === tool) {
            toolSeleccionada = null;
            btn.classList.remove('active');
        } else {
            document.querySelectorAll('.tool-btn').forEach(b => b.classList.remove('active'));
            toolSeleccionada = tool;
            btn.classList.add('active');
        }
        canvas.style.cursor = toolSeleccionada ? 'crosshair' : '';
    });
});

function getCelda(e) {
    const rect = canvas.getBoundingClientRect();
    const c = Math.floor((e.clientX - rect.left) / (canvas.width / config.columnas));
    const f = Math.floor((e.clientY - rect.top) / (canvas.height / config.filas));
    if (f < 0 || f >= config.filas || c < 0 || c >= config.columnas) return null;
    return { f, c };
}

function colocarEnCelda(f, c) {
    const board = engine.board;

    if (toolSeleccionada === 'borrar') {
        board.setEntidad(f, c, null);
        board.setObjeto(f, c, null);
        board.setTrampa(f, c, null);
    } else if (toolSeleccionada === 'trampa') {
        board.setTrampa(f, c, new Trampa(f, c, config.danioTrampa));
    } else if (toolSeleccionada === 'escudo') {
        board.setEntidad(f, c, null);
        board.setObjeto(f, c, new Escudo(f, c, config.valorEscudo));
    } else if (toolSeleccionada === 'arma') {
        board.setEntidad(f, c, null);
        board.setObjeto(f, c, new Arma(f, c, config.valorArma));
    } else if (toolSeleccionada === 'estrella') {
        board.setEntidad(f, c, null);
        board.setObjeto(f, c, new Estrella(f, c, config.turnosEstrella));
    } else if (toolSeleccionada === 'velocidad') {
        board.setEntidad(f, c, null);
        board.setObjeto(f, c, new Velocidad(f, c, config.duracionVelocidad));
    } else if (toolSeleccionada === 'pocion') {
        board.setEntidad(f, c, null);
        board.setObjeto(f, c, new Pocion(f, c, config.curacionPocion));
    } else {
        board.setObjeto(f, c, null);
        board.setEntidad(f, c, null);

        let entidad;
        switch (toolSeleccionada) {
            case 'aliado':
                entidad = new Aliado(f, c, config.vidaAliado, config.danioBaseAliadoMin, config.danioBaseAliadoMax, config.visionAliado);
                break;
            case 'enemigo':
                entidad = new Enemigo(f, c, config.vidaEnemigo, config.danioEnemigoMin, config.danioEnemigoMax, config.visionEnemigo);
                break;
            case 'tanque':
                entidad = new EnemigoTanque(f, c, config.vidaTanque, config.danioTanqueMin, config.danioTanqueMax, config.visionTanque);
                break;
            case 'rapido':
                entidad = new EnemigoRapido(f, c, config.vidaRapido, config.danioRapidoMin, config.danioRapidoMax, config.visionRapido);
                break;
            case 'muro':
                entidad = new Muro(f, c);
                break;
        }
        if (entidad) {
            board.setEntidad(f, c, entidad);
            if (entidad instanceof Aliado || entidad instanceof Enemigo) {
                engine.todasEntidades.push(entidad);
            }
        }
    }

    renderer.drawBoard(board, engine.turno);
}

// Drag para colocar/borrar manteniendo el raton pulsado
let pintando = false;
let ultimaCelda = null;

canvas.addEventListener('mousedown', (e) => {
    if (!toolSeleccionada || !engine.board) return;
    pintando = true;
    const celda = getCelda(e);
    if (celda) {
        ultimaCelda = `${celda.f},${celda.c}`;
        colocarEnCelda(celda.f, celda.c);
    }
});

canvas.addEventListener('mousemove', (e) => {
    if (!pintando || !toolSeleccionada || !engine.board) return;
    const celda = getCelda(e);
    if (!celda) return;
    const clave = `${celda.f},${celda.c}`;
    if (clave === ultimaCelda) return; // no repetir misma celda
    ultimaCelda = clave;
    colocarEnCelda(celda.f, celda.c);
});

window.addEventListener('mouseup', () => {
    pintando = false;
    ultimaCelda = null;
});

// ==================== Iniciar partida ====================

function iniciarSimulacion() {
    enSetup = false;
    btnIniciar.style.display = 'none';
    statsDiv.style.display = 'none';
    btnPausa.style.display = '';
    btnFinalizar.style.display = '';
    btnPausa.textContent = 'PAUSAR';
    pausado = false;

    // Recalcular contadores por si el usuario coloco/borro cosas en setup
    engine.numAliadosInicial = 0;
    engine.numEnemigosInicial = 0;
    engine.todasEntidades = [];
    for (let f = 0; f < engine.board.filas; f++) {
        for (let c = 0; c < engine.board.columnas; c++) {
            const e = engine.board.getEntidad(f, c);
            if (e instanceof Aliado) {
                engine.todasEntidades.push(e);
                engine.numAliadosInicial++;
            } else if (e instanceof Enemigo) {
                engine.todasEntidades.push(e);
                engine.numEnemigosInicial++;
            }
        }
    }
    engine.tiempoInicio = Date.now();

    intervalId = setInterval(tickLoop, config.velocidadMs);
}

function tickLoop() {
    if (pausado) return;
    engine.tick();
    renderer.drawBoard(engine.board, engine.turno);
    renderer.updateHUD(engine);
    if (engine.haTerminado()) {
        finalizarPartida();
    }
}

btnIniciar.addEventListener('click', () => {
    if (enSetup) {
        iniciarSimulacion();
    } else {
        // Post-partida: regenerar nueva partida en setup
        generarPartida();
    }
});

// ==================== Finalizar ====================

function finalizarPartida() {
    if (intervalId !== null) {
        clearInterval(intervalId);
        intervalId = null;
    }

    // En modo libre, calcular resultado por kills
    if (config.modoLibre && !engine.resultado) {
        let killsAliados = 0;
        let killsEnemigos = 0;
        for (const e of engine.todasEntidades) {
            if (e instanceof Aliado) killsAliados += e.kills;
            else if (e instanceof Enemigo) killsEnemigos += e.kills;
        }
        if (killsAliados > killsEnemigos) {
            engine.resultado = "aliados";
        } else if (killsEnemigos > killsAliados) {
            engine.resultado = "enemigos";
        } else {
            engine.resultado = "empate";
        }
    }

    btnPausa.style.display = 'none';
    btnFinalizar.style.display = 'none';
    renderer.mostrarEstadisticas(engine);
    btnIniciar.textContent = 'NUEVA PARTIDA';
    btnIniciar.style.display = '';
}

btnFinalizar.addEventListener('click', () => {
    finalizarPartida();
});

// ==================== Pausar / Reanudar ====================

btnPausa.addEventListener('click', () => {
    pausado = !pausado;
    btnPausa.textContent = pausado ? 'REANUDAR' : 'PAUSAR';
});
