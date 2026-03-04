import utils.*;

public class App {
    public static void main(String[] args) throws Exception {
        GameConfig config = new GameConfig();
        GameEngine engine = new GameEngine(config);
        Renderer renderer = new Renderer();

        engine.inicializar();
        renderer.ocultarCursor();

        while (!engine.haTerminado()) {
            engine.tick();
            renderer.limpiarPantalla();
            renderer.mostrarTablero(engine.getBoard(), engine.getTurno());
            renderer.mostrarHUD(engine.getBoard(), config, engine.getTurno(), engine.getTiempoInicio(),
                    engine.getEnemigosEliminados(), engine.getObjetosRecogidos(),
                    engine.getNumAliados(), engine.getNumEnemigos());
            Thread.sleep(config.velocidadMs);
        }

        renderer.mostrarResultado(engine.getResultado());
        renderer.mostrarEstadisticas(engine.getTodasEntidades(), engine.getResultado(),
                engine.getTurno(), engine.getTiempoInicio(),
                engine.getNumAliadosInicial(), engine.getNumEnemigosInicial());
        renderer.restaurarCursor();
    }
}
