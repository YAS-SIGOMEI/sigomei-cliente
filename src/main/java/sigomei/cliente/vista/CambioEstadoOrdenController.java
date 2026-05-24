package sigomei.cliente.vista;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sigomei.modelo.EstadoOrden;
import sigomei.modelo.OrdenMantenimiento;

/**
 * Controller del modal "Cambiar estado de orden".
 * Muestra las transiciones válidas/bloqueadas del estado actual y permite
 * al usuario seleccionar y confirmar una transición permitida.
 */
public class CambioEstadoOrdenController {

    private static final Logger LOG = Logger.getLogger("sigomei.cliente.CambioEstadoOrden");

    @FXML private Label lblTitulo;
    @FXML private Label lblInfo;
    @FXML private HBox estadoActualBox;
    @FXML private VBox transicionesBox;
    @FXML private Button btnConfirmar;

    private OrdenMantenimiento orden;
    private EstadoOrden estadoSeleccionado;
    private boolean confirmado = false;

    /**
     * Mapa de transiciones. Clave: estadoActual, Valor: mapa de estadoDestino → nota.
     * Las transiciones con nota "BLOQUEADO" se muestran deshabilitadas.
     */
    private static final Map<EstadoOrden, Map<EstadoOrden, TransicionInfo>> TRANSICIONES = new LinkedHashMap<>();

    static {
        // Desde PROGRAMADA
        Map<EstadoOrden, TransicionInfo> desdeProgramada = new LinkedHashMap<>();
        desdeProgramada.put(EstadoOrden.EN_EJECUCION, new TransicionInfo(true, ""));
        desdeProgramada.put(EstadoOrden.CANCELADA, new TransicionInfo(true, "acción separada · botón \"Cancelar\""));
        TRANSICIONES.put(EstadoOrden.PROGRAMADA, desdeProgramada);

        // Desde EN_EJECUCION
        Map<EstadoOrden, TransicionInfo> desdeEnEjecucion = new LinkedHashMap<>();
        desdeEnEjecucion.put(EstadoOrden.PROGRAMADA, new TransicionInfo(false, "no permitido — solo avance"));
        desdeEnEjecucion.put(EstadoOrden.FINALIZADA, new TransicionInfo(true, ""));
        desdeEnEjecucion.put(EstadoOrden.CANCELADA, new TransicionInfo(true, "acción separada · botón \"Cancelar\""));
        TRANSICIONES.put(EstadoOrden.EN_EJECUCION, desdeEnEjecucion);

        // Desde FINALIZADA — sin transiciones
        TRANSICIONES.put(EstadoOrden.FINALIZADA, new LinkedHashMap<>());

        // Desde CANCELADA — sin transiciones
        TRANSICIONES.put(EstadoOrden.CANCELADA, new LinkedHashMap<>());
    }

    public void iniciar(OrdenMantenimiento orden) {
        this.orden = orden;
        this.estadoSeleccionado = null;
        this.confirmado = false;
        btnConfirmar.setDisable(true);

        lblTitulo.setText("Cambiar estado de orden");
        lblInfo.setText("OT-" + orden.getId_orden()
                + " · EQUIPO #" + orden.getId_equipo()
                + " · TÉC. #" + orden.getId_tecnico());

        // Estado actual
        estadoActualBox.getChildren().clear();
        estadoActualBox.getChildren().addAll(
                crearDot(orden.getEstado_orden()),
                new Label(String.valueOf(orden.getEstado_orden()))
        );

        // Transiciones
        construirTransiciones();
    }

    public boolean confirmado() { return confirmado; }

    public EstadoOrden getEstadoSeleccionado() { return estadoSeleccionado; }

    private void construirTransiciones() {
        transicionesBox.getChildren().clear();
        EstadoOrden actual = orden.getEstado_orden();
        Map<EstadoOrden, TransicionInfo> trans = TRANSICIONES.getOrDefault(actual, new LinkedHashMap<>());

        if (trans.isEmpty()) {
            Label noTrans = new Label("No hay transiciones disponibles desde " + actual);
            noTrans.getStyleClass().add("label-muted");
            transicionesBox.getChildren().add(noTrans);
            return;
        }

        for (Map.Entry<EstadoOrden, TransicionInfo> entry : trans.entrySet()) {
            EstadoOrden destino = entry.getKey();
            TransicionInfo info = entry.getValue();
            HBox fila = crearFilaTransicion(actual, destino, info);
            transicionesBox.getChildren().add(fila);
        }
    }

    private HBox crearFilaTransicion(EstadoOrden origen, EstadoOrden destino, TransicionInfo info) {
        HBox fila = new HBox(8);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.getStyleClass().add("transition-row");
        fila.getStyleClass().add(info.permitida ? "transition-allowed" : "transition-blocked");

        // Origen
        HBox origenBox = new HBox(4, crearDot(origen), new Label(String.valueOf(origen)));
        origenBox.setAlignment(Pos.CENTER_LEFT);

        // Flecha
        Label flecha = new Label("→");
        flecha.getStyleClass().add("transition-arrow");

        // Destino
        HBox destinoBox = new HBox(4, crearDot(destino), new Label(String.valueOf(destino)));
        destinoBox.setAlignment(Pos.CENTER_LEFT);

        fila.getChildren().addAll(origenBox, flecha, destinoBox);

        if (info.permitida) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            Button btnSeleccionar = new Button("Seleccionar");
            btnSeleccionar.getStyleClass().addAll("button", "button-sm", "button-primary");
            btnSeleccionar.setOnAction(e -> {
                estadoSeleccionado = destino;
                btnConfirmar.setDisable(false);
                // Resaltar la fila seleccionada
                for (javafx.scene.Node node : transicionesBox.getChildren()) {
                    node.setStyle("");
                }
                fila.setStyle("-fx-border-color: #0f172a; -fx-border-width: 2;");
            });
            fila.getChildren().addAll(spacer, btnSeleccionar);
        } else {
            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            Label nota = new Label(info.nota);
            nota.getStyleClass().add("transition-note");
            fila.getChildren().addAll(spacer, nota);
        }

        return fila;
    }

    private Region crearDot(EstadoOrden estado) {
        Region dot = new Region();
        dot.getStyleClass().add("status-dot");
        switch (estado) {
            case PROGRAMADA: dot.getStyleClass().add("status-dot-yellow"); break;
            case EN_EJECUCION: dot.getStyleClass().add("status-dot-gray"); break;
            case FINALIZADA: dot.getStyleClass().add("status-dot-green"); break;
            case CANCELADA: dot.getStyleClass().add("status-dot-red"); break;
        }
        dot.setMinSize(8, 8);
        dot.setMaxSize(8, 8);
        return dot;
    }

    @FXML
    private void onConfirmar(ActionEvent ev) {
        if (estadoSeleccionado == null) return;
        confirmado = true;
        cerrar();
    }

    @FXML
    private void onCerrar(ActionEvent ev) {
        cerrar();
    }

    private void cerrar() {
        ((Stage) lblTitulo.getScene().getWindow()).close();
    }

    /** Info de una transición */
    private static class TransicionInfo {
        final boolean permitida;
        final String nota;

        TransicionInfo(boolean permitida, String nota) {
            this.permitida = permitida;
            this.nota = nota;
        }
    }
}
