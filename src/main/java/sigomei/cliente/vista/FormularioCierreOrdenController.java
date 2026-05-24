package sigomei.cliente.vista;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sigomei.cliente.Sesion;
import sigomei.modelo.OrdenMantenimiento;

public class FormularioCierreOrdenController {

    private static final Logger LOG = Logger.getLogger("sigomei.cliente.FormularioCierreOrden");

    @FXML private Label lblTitulo;
    @FXML private Label lblInfo;
    @FXML private TextField txtCostoReal;
    @FXML private DatePicker dpFechaCierre;

    private OrdenMantenimiento orden;
    private boolean guardado = false;

    @FXML
    public void initialize() {
        dpFechaCierre.setValue(LocalDate.now());
    }

    public void iniciar(OrdenMantenimiento o) {
        this.orden = o;
        lblTitulo.setText("Cerrar orden #" + o.getId_orden());
        lblInfo.setText("Equipo #" + o.getId_equipo() + "  •  Estado actual: " + o.getEstado_orden()
                + "  •  Costo estimado: " + o.getCosto_estimado());
        if (o.getCosto_real() != null) {
            txtCostoReal.setText(String.valueOf(o.getCosto_real()));
        }
        if (o.getFecha_cierre() != null) {
            dpFechaCierre.setValue(o.getFecha_cierre());
        }
    }

    public boolean guardado() { return guardado; }

    @FXML
    private void onCancelar(ActionEvent ev) { cerrar(); }

    @FXML
    private void onGuardar(ActionEvent ev) {
        if (vacio(txtCostoReal.getText()) || dpFechaCierre.getValue() == null) {
            Dialogos.error("Datos incompletos", "Costo real y fecha de cierre son obligatorios (RN-06).");
            return;
        }
        double costoReal;
        try {
            costoReal = Double.parseDouble(txtCostoReal.getText().trim());
        } catch (NumberFormatException e) {
            Dialogos.error("Costo invalido", "El costo real debe ser numérico.");
            return;
        }

        try {
            Sesion.get().ordenes().cerrarOrden(orden.getId_orden(), costoReal, dpFechaCierre.getValue());
            LOG.info("cerrarOrden OK id=" + orden.getId_orden());
            Dialogos.info("OK", "Orden #" + orden.getId_orden() + " finalizada.");
            guardado = true;
            cerrar();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Cerrar orden rechazada", ex);
            Dialogos.error("No se pudo cerrar", ex.getMessage());
        }
    }

    private void cerrar() { ((Stage) lblTitulo.getScene().getWindow()).close(); }

    private static boolean vacio(String s) { return s == null || s.isBlank(); }
}
