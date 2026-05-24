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
        if (paraEditar == null) {
            lblTitulo.setText("Nuevo equipo");
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

    @FXML
    private void onCancelar(ActionEvent ev) {
        cerrar();
    }

    @FXML
    private void onGuardar(ActionEvent ev) {
        // Validacion basica del lado cliente
        if (vacio(txtNombre.getText()) || cmbTipo.getValue() == null
                || vacio(txtMarca.getText()) || vacio(txtModelo.getText())
                || vacio(txtSerie.getText()) || vacio(txtUbicacion.getText())
                || dpInstalacion.getValue() == null
                || cmbCriticidad.getValue() == null || cmbEstado.getValue() == null) {
            Dialogos.error("Datos incompletos", "Todos los campos son obligatorios.");
            return;
        }

        Equipo e = (enEdicion != null) ? enEdicion : new Equipo();
        e.setNombre(txtNombre.getText().trim());
        e.setTipo(cmbTipo.getValue());
        e.setMarca(txtMarca.getText().trim());
        e.setModelo(txtModelo.getText().trim());
        e.setNumero_serie(txtSerie.getText().trim());
        e.setUbicacion_planta(txtUbicacion.getText().trim());
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
            cerrar();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Guardar equipo rechazado", ex);
            Dialogos.error("No se pudo guardar", ex.getMessage());
        }
    }

    private void cerrar() {
        ((Stage) lblTitulo.getScene().getWindow()).close();
    }

    private static boolean vacio(String s) {
        return s == null || s.isBlank();
    }
}
