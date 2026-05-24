package sigomei.cliente.vista;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sigomei.cliente.Sesion;
import sigomei.modelo.Equipo;
import sigomei.modelo.EstadoOrden;
import sigomei.modelo.OrdenMantenimiento;
import sigomei.modelo.Tecnico;
import sigomei.modelo.TipoMantenimiento;
import sigomei.modelo.Usuario;

public class FormularioOrdenController {

    private static final Logger LOG = Logger.getLogger("sigomei.cliente.FormularioOrden");

    @FXML private Label lblTitulo;
    @FXML private ComboBox<Equipo> cmbEquipo;
    @FXML private ComboBox<Tecnico> cmbTecnico;
    @FXML private ComboBox<TipoMantenimiento> cmbTipo;
    @FXML private DatePicker dpProgramada;
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpCierre;
    @FXML private TextField txtCostoEst;
    @FXML private TextArea txtDescripcion;

    private OrdenMantenimiento enEdicion;
    private boolean guardado = false;

    @FXML
    public void initialize() {
        cmbTipo.getItems().setAll(TipoMantenimiento.values());

        cmbEquipo.setConverter(new StringConverter<>() {
            @Override public String toString(Equipo e) {
                return e == null ? "" : "#" + e.getId_equipo() + " " + e.getNombre() + " (" + e.getTipo() + ", " + e.getCriticidad() + ")";
            }
            @Override public Equipo fromString(String s) { return null; }
        });

        cmbTecnico.setConverter(new StringConverter<>() {
            @Override public String toString(Tecnico t) {
                return t == null ? "" : "#" + t.getId_tecnico() + " " + t.getNombre_completo()
                        + " (" + t.getEspecialidad() + " " + t.getNivel_certificacion() + " " + t.getEstatus() + ")";
            }
            @Override public Tecnico fromString(String s) { return null; }
        });

        try {
            List<Equipo> equipos = Sesion.get().equipos().consultarEquipos(null);
            cmbEquipo.getItems().setAll(equipos);
            List<Tecnico> tecnicos = Sesion.get().tecnicos().consultarTecnicos(null);
            cmbTecnico.getItems().setAll(tecnicos);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "No se pudieron cargar catalogos para la orden", e);
            Dialogos.error("Error", "No se pudieron cargar equipos o técnicos: " + e.getMessage());
        }

        dpProgramada.setValue(LocalDate.now().plusDays(1));
    }

    public void iniciar(OrdenMantenimiento paraEditar) {
        this.enEdicion = paraEditar;
        if (paraEditar == null) {
            lblTitulo.setText("Nueva orden");
            cmbTipo.getSelectionModel().select(TipoMantenimiento.PREVENTIVO);
        } else {
            lblTitulo.setText("Editar orden #" + paraEditar.getId_orden());
            seleccionarEquipoPorId(paraEditar.getId_equipo());
            seleccionarTecnicoPorId(paraEditar.getId_tecnico());
            cmbTipo.setValue(paraEditar.getTipo_mantenimiento());
            dpProgramada.setValue(paraEditar.getFecha_programada());
            dpInicio.setValue(paraEditar.getFecha_inicio());
            dpCierre.setValue(paraEditar.getFecha_cierre());
            txtCostoEst.setText(paraEditar.getCosto_estimado() == null ? "" : String.valueOf(paraEditar.getCosto_estimado()));
            txtDescripcion.setText(paraEditar.getDescripcion_trabajo());
        }
    }

    public boolean guardado() { return guardado; }

    @FXML
    private void onCancelar(ActionEvent ev) { cerrar(); }

    @FXML
    private void onGuardar(ActionEvent ev) {
        if (cmbEquipo.getValue() == null || cmbTecnico.getValue() == null
                || cmbTipo.getValue() == null || dpProgramada.getValue() == null
                || vacio(txtCostoEst.getText()) || vacio(txtDescripcion.getText())) {
            Dialogos.error("Datos incompletos", "Equipo, técnico, tipo, fecha programada, costo estimado y descripción son obligatorios.");
            return;
        }
        double costoEst;
        try {
            costoEst = Double.parseDouble(txtCostoEst.getText().trim());
        } catch (NumberFormatException e) {
            Dialogos.error("Costo invalido", "El costo estimado debe ser numérico.");
            return;
        }

        OrdenMantenimiento o = (enEdicion != null) ? enEdicion : new OrdenMantenimiento();
        o.setId_equipo(cmbEquipo.getValue().getId_equipo());
        o.setId_tecnico(cmbTecnico.getValue().getId_tecnico());
        if (enEdicion == null) {
            Usuario u = Sesion.get().usuarioActual();
            o.setId_usuario(u != null ? u.getId_usuario() : 0);
        }
        o.setTipo_mantenimiento(cmbTipo.getValue());
        o.setFecha_programada(dpProgramada.getValue());
        o.setFecha_inicio(dpInicio.getValue());
        o.setFecha_cierre(dpCierre.getValue());
        o.setCosto_estimado(costoEst);
        o.setDescripcion_trabajo(txtDescripcion.getText().trim());

        try {
            if (enEdicion == null) {
                o.setEstado_orden(EstadoOrden.PROGRAMADA);
                int id = Sesion.get().ordenes().registrarOrden(o);
                LOG.info("registrarOrden OK id=" + id);
                Dialogos.info("OK", "Orden registrada con id " + id);
            } else {
                Sesion.get().ordenes().modificarOrden(o);
                LOG.info("modificarOrden OK id=" + o.getId_orden());
                Dialogos.info("OK", "Orden actualizada");
            }
            guardado = true;
            cerrar();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Guardar orden rechazada", ex);
            Dialogos.error("No se pudo guardar", ex.getMessage());
        }
    }

    private void seleccionarEquipoPorId(int id) {
        for (Equipo e : cmbEquipo.getItems()) {
            if (e.getId_equipo() == id) {
                cmbEquipo.setValue(e);
                return;
            }
        }
    }

    private void seleccionarTecnicoPorId(int id) {
        for (Tecnico t : cmbTecnico.getItems()) {
            if (t.getId_tecnico() == id) {
                cmbTecnico.setValue(t);
                return;
            }
        }
    }

    private void cerrar() { ((Stage) lblTitulo.getScene().getWindow()).close(); }

    private static boolean vacio(String s) { return s == null || s.isBlank(); }
}
