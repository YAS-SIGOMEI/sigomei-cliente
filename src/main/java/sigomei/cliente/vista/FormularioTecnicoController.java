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
import sigomei.modelo.EstatusTecnico;
import sigomei.modelo.NivelCertificacion;
import sigomei.modelo.Tecnico;
import sigomei.modelo.TipoEquipo;

public class FormularioTecnicoController {

    private static final Logger LOG = Logger.getLogger("sigomei.cliente.FormularioTecnico");

    @FXML private Label lblTitulo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtRfc;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private ComboBox<TipoEquipo> cmbEspecialidad;
    @FXML private ComboBox<NivelCertificacion> cmbNivel;
    @FXML private DatePicker dpIngreso;
    @FXML private ComboBox<EstatusTecnico> cmbEstatus;

    private Tecnico enEdicion;
    private boolean guardado = false;

    @FXML
    public void initialize() {
        cmbEspecialidad.getItems().setAll(TipoEquipo.values());
        cmbNivel.getItems().setAll(NivelCertificacion.values());
        cmbEstatus.getItems().setAll(EstatusTecnico.values());
        cmbEstatus.getSelectionModel().select(EstatusTecnico.ACTIVO);
        dpIngreso.setValue(LocalDate.now());
    }

    public void iniciar(Tecnico paraEditar) {
        this.enEdicion = paraEditar;
        if (paraEditar == null) {
            lblTitulo.setText("Nuevo técnico");
        } else {
            lblTitulo.setText("Editar técnico #" + paraEditar.getId_tecnico());
            txtNombre.setText(paraEditar.getNombre_completo());
            txtRfc.setText(paraEditar.getRfc());
            txtTelefono.setText(paraEditar.getTelefono());
            txtCorreo.setText(paraEditar.getCorreo());
            cmbEspecialidad.setValue(paraEditar.getEspecialidad());
            cmbNivel.setValue(paraEditar.getNivel_certificacion());
            dpIngreso.setValue(paraEditar.getFecha_ingreso());
            cmbEstatus.setValue(paraEditar.getEstatus());
        }
    }

    public boolean guardado() { return guardado; }

    @FXML
    private void onCancelar(ActionEvent ev) { cerrar(); }

    @FXML
    private void onGuardar(ActionEvent ev) {
        if (vacio(txtNombre.getText()) || vacio(txtRfc.getText())
                || vacio(txtTelefono.getText()) || vacio(txtCorreo.getText())
                || cmbEspecialidad.getValue() == null || cmbNivel.getValue() == null
                || dpIngreso.getValue() == null || cmbEstatus.getValue() == null) {
            Dialogos.error("Datos incompletos", "Todos los campos son obligatorios.");
            return;
        }

        Tecnico t = (enEdicion != null) ? enEdicion : new Tecnico();
        t.setNombre_completo(txtNombre.getText().trim());
        t.setRfc(txtRfc.getText().trim().toUpperCase());
        t.setTelefono(txtTelefono.getText().trim());
        t.setCorreo(txtCorreo.getText().trim());
        t.setEspecialidad(cmbEspecialidad.getValue());
        t.setNivel_certificacion(cmbNivel.getValue());
        t.setFecha_ingreso(dpIngreso.getValue());
        t.setEstatus(cmbEstatus.getValue());

        try {
            if (enEdicion == null) {
                int id = Sesion.get().tecnicos().registrarTecnico(t);
                LOG.info("registrarTecnico OK id=" + id);
                Dialogos.info("OK", "Técnico registrado con id " + id);
            } else {
                Sesion.get().tecnicos().modificarTecnico(t);
                LOG.info("modificarTecnico OK id=" + t.getId_tecnico());
                Dialogos.info("OK", "Técnico actualizado");
            }
            guardado = true;
            cerrar();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Guardar técnico rechazado", ex);
            Dialogos.error("No se pudo guardar", ex.getMessage());
        }
    }

    private void cerrar() { ((Stage) lblTitulo.getScene().getWindow()).close(); }

    private static boolean vacio(String s) { return s == null || s.isBlank(); }
}
