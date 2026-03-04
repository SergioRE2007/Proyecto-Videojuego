package utils;

import java.util.List;

import entidades.*;
import objetos.Objeto;

public class Renderer {
    public static final String RESET = "\u001B[0m";
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String AZUL = "\u001B[34m";

    private static final String[] COLORES_ESTRELLA = {
        "\u001B[1;31m",
        "\u001B[1;33m",
        "\u001B[1;32m",
        "\u001B[1;36m",
        "\u001B[1;35m",
        "\u001B[1;37m",
    };

    public void mostrarTablero(GameBoard board, int turno) {
        int filas = board.getFilas();
        int columnas = board.getColumnas();
        StringBuilder sb = new StringBuilder(filas * columnas * 15);
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                Entidad e = board.getEntidad(i, j);
                if (e != null) {
                    char c = e.getSimbolo();
                    if (c == 'T') {
                        sb.append("\u001B[1;31m").append(" T ").append(RESET);
                    } else if (c == 'R') {
                        sb.append(AMARILLO).append(" \u00A4 ").append(RESET);
                    } else if (c == 'X') {
                        sb.append(ROJO).append(" # ").append(RESET);
                    } else if (c == 'A') {
                        if (e instanceof Aliado && ((Aliado) e).getTurnosInvencible() > 0) {
                            String color = COLORES_ESTRELLA[turno % COLORES_ESTRELLA.length];
                            sb.append(color).append(" o ").append(RESET);
                        } else {
                            sb.append(VERDE).append(" o ").append(RESET);
                        }
                    } else if (c == 'M') {
                        sb.append(AMARILLO).append("[=]").append(RESET);
                    } else {
                        sb.append(" . ");
                    }
                } else if (board.getObjeto(i, j) != null) {
                    char s = board.getObjeto(i, j).getSimbolo();
                    if (s == 'S') {
                        sb.append(CYAN).append(" S ").append(RESET);
                    } else if (s == 'W') {
                        sb.append(MAGENTA).append(" W ").append(RESET);
                    } else if (s == 'V') {
                        sb.append(AZUL).append(" V ").append(RESET);
                    } else if (s == '*') {
                        sb.append("\u001B[1;33m").append(" * ").append(RESET);
                    } else if (s == '+') {
                        sb.append("\u001B[1;32m").append(" + ").append(RESET);
                    } else {
                        sb.append(" ").append(s).append(" ");
                    }
                } else if (board.getTrampa(i, j) != null) {
                    sb.append("\u001B[37m").append(" ^ ").append(RESET);
                } else {
                    sb.append(" . ");
                }
            }
            sb.append("\033[K\n");
        }
        System.out.print(sb);
        System.out.flush();
    }

    public void mostrarHUD(GameBoard board, GameConfig config, int turno, long tiempoInicio,
                           int enemigosEliminados, int objetosRecogidos, int numAliados, int numEnemigos) {
        long tiempoMs = System.currentTimeMillis() - tiempoInicio;
        int segundosTotales = (int) (tiempoMs / 1000);
        int minutos = segundosTotales / 60;
        int segundos = segundosTotales % 60;
        String tiempo = String.format("%02d:%02d", minutos, segundos);

        String colorAliados = numAliados <= 2 ? ROJO : VERDE;

        int anchoHud = board.getColumnas() * 3;
        String lineaH = "\u2550".repeat(anchoHud - 2);

        String linea1 = String.format("  Turno: %d  \u2502  Tiempo: %s  \u2502  Velocidad: %dms",
                turno, tiempo, config.velocidadMs);
        String linea2 = String.format("  Aliados: %s%d/%d%s  \u2502  Enemigos: %s%d%s  \u2502  Eliminados: %s%d%s  \u2502  Objetos: %s%d%s",
                colorAliados, numAliados, config.numAliado, RESET,
                ROJO, numEnemigos, RESET,
                VERDE, enemigosEliminados, RESET,
                CYAN, objetosRecogidos, RESET);

        String leyenda = "  " + VERDE + "o" + RESET + "=Aliado  "
                + ROJO + "#" + RESET + "=Enemigo  "
                + "\u001B[1;31m" + "T" + RESET + "=Tanque  "
                + AMARILLO + "\u00A4" + RESET + "=R\u00E1pido  "
                + AMARILLO + "[=]" + RESET + "=Muro  "
                + "\u001B[37m" + "^" + RESET + "=Trampa  "
                + CYAN + "S" + RESET + "=Escudo  "
                + MAGENTA + "W" + RESET + "=Arma  "
                + "\u001B[1;33m" + "*" + RESET + "=Estrella  "
                + AZUL + "V" + RESET + "=Vel  "
                + "\u001B[1;32m" + "+" + RESET + "=Poci\u00F3n";

        System.out.println("\u2554" + lineaH + "\u2557\033[K");
        System.out.println("\u2551" + linea1 + espacios(anchoHud - 2 - longitudVisible(linea1)) + "\u2551\033[K");
        System.out.println("\u2551" + linea2 + espacios(anchoHud - 2 - longitudVisible(linea2)) + "\u2551\033[K");
        System.out.println("\u2551" + leyenda + espacios(anchoHud - 2 - longitudVisible(leyenda)) + "\u2551\033[K");
        System.out.println("\u255A" + lineaH + "\u255D\033[K");
    }

    public void mostrarResultado(String resultado) {
        switch (resultado) {
            case "empate":
                System.out.println(AMARILLO + " Empate! Ambos bandos han caido al mismo tiempo." + RESET);
                break;
            case "enemigos":
                System.out.println(ROJO + " Los enemigos han ganado! Todos los aliados han caido." + RESET);
                break;
            case "aliados":
                System.out.println(VERDE + " Los aliados han sobrevivido! Todos los enemigos han sido eliminados." + RESET);
                break;
        }
    }

    public void limpiarPantalla() {
        System.out.print("\033[H");
    }

    public void ocultarCursor() {
        System.out.print("\033[?25l\033[H\033[2J");
        System.out.flush();
    }

    public void restaurarCursor() {
        System.out.print("\033[?25h");
        System.out.flush();
    }

    public void mostrarEstadisticas(List<Entidad> todasEntidades, String resultado,
                                     int turnos, long tiempoInicio,
                                     int numAliadosInicial, int numEnemigosInicial) {
        int ancho = 46;
        String lineaH = "\u2550".repeat(ancho);

        // Calcular stats por faccion
        int danioAliadosInf = 0, danioAliadosRec = 0, killsAliados = 0, objRecogidos = 0;
        int aliadosVivos = 0;
        int danioEnemigosInf = 0, danioEnemigosRec = 0, killsEnemigos = 0;
        int enemigosVivos = 0;

        Entidad mvpAliado = null, mvpEnemigo = null;

        for (Entidad e : todasEntidades) {
            if (e instanceof Aliado) {
                Aliado a = (Aliado) e;
                danioAliadosInf += a.getDanioInfligido();
                danioAliadosRec += a.getDanioRecibido();
                killsAliados += a.getKills();
                objRecogidos += a.getObjetosRecogidosPersonal();
                if (a.estaVivo()) aliadosVivos++;
                if (mvpAliado == null || esMejorMvp(a, mvpAliado)) mvpAliado = a;
            } else if (e instanceof Enemigo) {
                danioEnemigosInf += e.getDanioInfligido();
                danioEnemigosRec += e.getDanioRecibido();
                killsEnemigos += e.getKills();
                if (e.estaVivo()) enemigosVivos++;
                if (mvpEnemigo == null || esMejorMvp(e, mvpEnemigo)) mvpEnemigo = e;
            }
        }

        long tiempoMs = System.currentTimeMillis() - tiempoInicio;
        int seg = (int) (tiempoMs / 1000);
        String tiempo = String.format("%02d:%02d", seg / 60, seg % 60);

        String textoResultado;
        switch (resultado) {
            case "aliados": textoResultado = VERDE + "Los aliados han ganado" + RESET; break;
            case "enemigos": textoResultado = ROJO + "Los enemigos han ganado" + RESET; break;
            default: textoResultado = AMARILLO + "Empate" + RESET; break;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("\u2554").append(lineaH).append("\u2557\n");
        lineaCentrada(sb, ancho, "ESTADISTICAS DE PARTIDA");
        sb.append("\u2560").append(lineaH).append("\u2563\n");
        lineaIzq(sb, ancho, "  Resultado: " + textoResultado);
        lineaIzq(sb, ancho, "  Turnos: " + turnos + "  \u2502  Tiempo: " + tiempo);
        sb.append("\u2560").append(lineaH).append("\u2563\n");
        lineaCentrada(sb, ancho, VERDE + "ALIADOS" + RESET);
        lineaIzq(sb, ancho, "  Danio infligido total: " + danioAliadosInf);
        lineaIzq(sb, ancho, "  Danio recibido total: " + danioAliadosRec);
        lineaIzq(sb, ancho, "  Supervivientes: " + aliadosVivos + "/" + numAliadosInicial);
        lineaIzq(sb, ancho, "  Kills totales: " + killsAliados);
        lineaIzq(sb, ancho, "  Objetos recogidos: " + objRecogidos);
        sb.append("\u2560").append(lineaH).append("\u2563\n");
        lineaCentrada(sb, ancho, ROJO + "ENEMIGOS" + RESET);
        lineaIzq(sb, ancho, "  Danio infligido total: " + danioEnemigosInf);
        lineaIzq(sb, ancho, "  Danio recibido total: " + danioEnemigosRec);
        lineaIzq(sb, ancho, "  Supervivientes: " + enemigosVivos + "/" + numEnemigosInicial);
        lineaIzq(sb, ancho, "  Kills totales: " + killsEnemigos);
        sb.append("\u2560").append(lineaH).append("\u2563\n");

        if (mvpAliado != null) {
            lineaCentrada(sb, ancho, VERDE + "MVP ALIADO" + RESET);
            lineaIzq(sb, ancho, "  Aliado #" + mvpAliado.getId() + " \u2014 " + mvpAliado.getKills() + " kills, " + mvpAliado.getDanioInfligido() + " danio");
        }
        lineaIzq(sb, ancho, "");
        if (mvpEnemigo != null) {
            lineaCentrada(sb, ancho, ROJO + "MVP ENEMIGO" + RESET);
            lineaIzq(sb, ancho, "  Enemigo #" + mvpEnemigo.getId() + " \u2014 " + mvpEnemigo.getKills() + " kills, " + mvpEnemigo.getDanioInfligido() + " danio");
        }

        sb.append("\u255A").append(lineaH).append("\u255D\n");
        System.out.print(sb);
        System.out.flush();
    }

    private boolean esMejorMvp(Entidad candidato, Entidad actual) {
        if (candidato.getKills() != actual.getKills()) return candidato.getKills() > actual.getKills();
        return candidato.getDanioInfligido() > actual.getDanioInfligido();
    }

    private void lineaCentrada(StringBuilder sb, int ancho, String texto) {
        int visible = longitudVisible(texto);
        int pad = (ancho - visible) / 2;
        int padDer = ancho - visible - pad;
        sb.append("\u2551").append(espacios(pad)).append(texto).append(espacios(padDer)).append("\u2551\n");
    }

    private void lineaIzq(StringBuilder sb, int ancho, String texto) {
        int visible = longitudVisible(texto);
        sb.append("\u2551").append(texto).append(espacios(ancho - visible)).append("\u2551\n");
    }

    private int longitudVisible(String s) {
        return s.replaceAll("\u001B\\[[;\\d]*m", "").length();
    }

    private String espacios(int n) {
        return n > 0 ? " ".repeat(n) : "";
    }
}
