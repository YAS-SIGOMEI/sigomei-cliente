package sigomei.cliente.vista;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sigomei.cliente.Sesion;
import sigomei.modelo.Criticidad;
import sigomei.modelo.Equipo;
import sigomei.modelo.EstadoOperativo;
import sigomei.modelo.TipoEquipo;

public class FormularioEquipoController {

    private static final Logger LOG = Logger.getLogger("sigomei.cliente.FormularioEquipo");

    @FXML private Label lblTitulo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<TipoEquipo> cmbTipo;
    @FXML private TextField txtMarca;
    @FXML private TextField txtModelo;
    @FXML private TextField txtSerie;
    @FXML private TextField txtUbicacion;
    @FXML private DatePicker dpInstalacion;
    @FXML private ComboBox<Criticidad> cmbCriticidad;
    @FXML private ComboBox<EstadoOperativo> cmbEstado;

    private Equipo enEdicion;  // null si es alta
    private boolean guardado = false;

    // Callbacks para modo embebido (panel lateral)
    private Runnable onGuardado;
    private Runnable onCancelado;

    @FXML
    public void initialize() {
        cmbTipo.getItems().setAll(TipoEquipo.values());
        cmbCriticidad.getItems().setAll(Criticidad.values());
        cmbEstado.getItems().setAll(EstadoOperativo.values());
        cmbEstado.getSelectionModel().select(EstadoOperativo.OPERATIVO);
        dpInstalacion.setValue(LocalDate.now());
    }

    /** Llamar tras cargar el FXML. Si paraEditar es null, es alta; si no, edicion. */
    public void iniciar(Equipo paraEditar) {
        this.enEdicion = paraEditar;
        this.guardado = false;
        limpiarCampos();
        if (paraEditar == null) {
            lblTitulo.setText("Nuevo Equipo");
        } else {
            lblTitulo.setText("Editar equipo #" + paraEditar.getId_equipo());
            txtNombre.setText(paraEditar.getNombre());
            cmbTipo.setValue(paraEditar.getTipo());
            txtMarca.setText(paraEditar.getMarca());
            txtModelo.setText(paraEditar.getModelo());
            txtSerie.setText(paraEditar.getNumero_serie());
            txtUbicacion.setText(paraEditar.getUbicacion_planta());
            dpInstalacion.setValue(paraEditar.getFecha_instalacion());
            cmbCriticidad.setValue(paraEditar.getCriticidad());
            cmbEstado.setValue(paraEditar.getEstado_operativo());
        }
    }

    public boolean guardado() { return guardado; }

    /** Callback cuando se guarda exitosamente (modo panel lateral). */
    public void setOnGuardado(Runnable onGuardado) {
        this.onGuardado = onGuardado;
    }

    /** Callback cuando se cancela (modo panel lateral). */
    public void setOnCancelado(Runnable onCancelado) {
        this.onCancelado = onCancelado;
    }

    @FXML
    private void onCancelar(ActionEvent ev) {
        if (onCancelado != null) {
            onCancelado.run();
        } else {
            cerrar();
        }
    }

    @FXML
    private void onGuardar(ActionEvent ev) {
        // Validacion basica del lado cliente
        if (vacio(txtNombre.getText()) || cmbTipo.getValue() == null
                || vacio(txtSerie.getText())
                || cmbCriticidad.getValue() == null || cmbEstado.getValue() == null) {
            Dialogos.error("Datos incompletos", "Nombre, tipo, N° de serie, criticidad y estado son obligatorios.");
            return;
        }

        Equipo e = (enEdicion != null) ? enEdicion : new Equipo();
        e.setNombre(txtNombre.getText().trim());
        e.setTipo(cmbTipo.getValue());
        e.setMarca(txtMarca.getText() == null ? "" : txtMarca.getText().trim());
        e.setModelo(txtModelo.getText() == null ? "" : txtModelo.getText().trim());
        e.setNumero_serie(txtSerie.getText().trim());
        e.setUbicacion_planta(txtUbicacion.getText() == null ? "" : txtUbicacion.getText().trim());
        e.setFecha_instalacion(dpInstalacion.getValue());
        e.setCriticidad(cmbCriticidad.getValue());
        e.setEstado_operativo(cmbEstado.getValue());

        try {
            if (enEdicion == null) {
                int id = Sesion.get().equipos().registrarEquipo(e);
                LOG.info("registrarEquipo OK id=" + id);
                Dialogos.info("OK", "Equipo registrado con id " + id);
            } else {
                Sesion.get().equipos().modificarEquipo(e);
                LOG.info("modificarEquipo OK id=" + e.getId_equipo());
                Dialogos.info("OK", "Equipo actualizado");
            }
            guardado = true;
            if (onGuardado != null) {
                onGuardado.run();
            } else {
                cerrar();
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Guardar equipo rechazado", ex);
            Dialogos.error("No se pudo guardar", ex.getMessage());
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        cmbTipo.getSelectionModel().clearSelection();
        txtMarca.clear();
        txtModelo.clear();
        txtSerie.clear();
        txtUbicacion.clear();
        dpInstalacion.setValue(LocalDate.now());
        cmbCriticidad.getSelectionModel().clearSelection();
        cmbEstado.getSelectionModel().select(EstadoOperativo.OPERATIVO);
    }

    private void cerrar() {
        Stage stage = (Stage) lblTitulo.getScene().getWindow();
        // Solo cerrar si es un Stage separado (modal), no si es embebido
        if (stage != null && stage.getOwner() != null) {
            stage.close();
        } else if (stage != null) {
            stage.close();
        }
    }

    private static boolean vacio(String s) {
        return s == null || s.isBlank();
    }
}
