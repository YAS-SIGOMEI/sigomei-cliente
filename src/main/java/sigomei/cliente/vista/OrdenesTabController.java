package sigomei.cliente.vista;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sigomei.cliente.Sesion;
import sigomei.modelo.EstadoOrden;
import sigomei.modelo.OrdenMantenimiento;

public class OrdenesTabController {

    private static final Logger LOG = Logger.getLogger("sigomei.cliente.OrdenesTabController");

    @FXML private TableView<OrdenMantenimiento> tabla;
    @FXML private TableColumn<OrdenMantenimiento, Number> colId;
    @FXML private TableColumn<OrdenMantenimiento, Number> colEquipo;
    @FXML private TableColumn<OrdenMantenimiento, Number> colTecnico;
    @FXML private TableColumn<OrdenMantenimiento, String> colTipo;
    @FXML private TableColumn<OrdenMantenimiento, String> colFechaProg;
    @FXML private TableColumn<OrdenMantenimiento, String> colFechaIni;
    @FXML private TableColumn<OrdenMantenimiento, String> colFechaCierre;
    @FXML private TableColumn<OrdenMantenimiento, String> colEstado;
    @FXML private TableColumn<OrdenMantenimiento, String> colCostoEst;
    @FXML private TableColumn<OrdenMantenimiento, String> colCostoReal;
    @FXML private TableColumn<OrdenMantenimiento, String> colDescripcion;
    @FXML private Label lblMensaje;

    private final ObservableList<OrdenMantenimiento> datos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId_orden()));
        colEquipo.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId_equipo()));
        colTecnico.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId_tecnico()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getTipo_mantenimiento())));
        colFechaProg.setCellValueFactory(c -> new SimpleStringProperty(toStr(c.getValue().getFecha_programada())));
        colFechaIni.setCellValueFactory(c -> new SimpleStringProperty(toStr(c.getValue().getFecha_inicio())));
        colFechaCierre.setCellValueFactory(c -> new SimpleStringProperty(toStr(c.getValue().getFecha_cierre())));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getEstado_orden())));
        colCostoEst.setCellValueFactory(c -> new SimpleStringProperty(toStr(c.getValue().getCosto_estimado())));
        colCostoReal.setCellValueFactory(c -> new SimpleStringProperty(toStr(c.getValue().getCosto_real())));
        colDescripcion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion_trabajo()));

        tabla.setItems(datos);
        refrescar();
    }

    private static String toStr(Object o) { return o == null ? "" : String.valueOf(o); }

    @FXML
    private void onRefrescar(ActionEvent ev) { refrescar(); }

    private void refrescar() {
        lblMensaje.setText("Cargando órdenes...");
        try {
            List<OrdenMantenimiento> lista = Sesion.get().ordenes().consultarOrdenes(null);
            datos.setAll(lista);
            lblMensaje.setText("Órdenes: " + lista.size());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al consultar órdenes", e);
            Dialogos.error("Error", "No se pudieron consultar las órdenes: " + e.getMessage());
        }
    }

    private OrdenMantenimiento seleccionada() {
        return tabla.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void onNueva(ActionEvent ev) { abrirFormulario(null); }

    @FXML
    private void onEditar(ActionEvent ev) {
        OrdenMantenimiento sel = seleccionada();
        if (sel == null) {
            Dialogos.info("Editar", "Selecciona una orden de la tabla.");
            return;
        }
        abrirFormulario(sel);
    }

    @FXML
    private void onIniciar(ActionEvent ev) {
        OrdenMantenimiento sel = seleccionada();
        if (sel == null) {
            Dialogos.info("Iniciar", "Selecciona una orden PROGRAMADA.");
            return;
        }
        boolean ok = Dialogos.confirmar("Iniciar orden",
                "¿Avanzar la orden #" + sel.getId_orden() + " a EN_EJECUCION?");
        if (!ok) return;
        try {
            Sesion.get().ordenes().avanzarEstadoOrden(sel.getId_orden(), EstadoOrden.EN_EJECUCION);
            Dialogos.info("OK", "Orden #" + sel.getId_orden() + " EN_EJECUCION.");
            refrescar();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Avanzar orden rechazado", e);
            Dialogos.error("No se pudo iniciar", e.getMessage());
        }
    }

    @FXML
    private void onCerrar(ActionEvent ev) {
        OrdenMantenimiento sel = seleccionada();
        if (sel == null) {
            Dialogos.info("Cerrar orden", "Selecciona una orden EN_EJECUCION.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sigomei/cliente/vista/FormularioCierreOrden.fxml"));
            Parent root = loader.load();
            FormularioCierreOrdenController ctrl = loader.getController();
            ctrl.iniciar(sel);

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Cerrar orden #" + sel.getId_orden());
            Scene scene = new Scene(root);
            Estilos.aplicar(scene);
            modal.setScene(scene);
            modal.showAndWait();

            if (ctrl.guardado()) refrescar();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "No se pudo abrir el formulario de cierre", e);
            Dialogos.error("Error", "No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelar(ActionEvent ev) {
        OrdenMantenimiento sel = seleccionada();
        if (sel == null) {
            Dialogos.info("Cancelar orden", "Selecciona una orden.");
            return;
        }
        boolean ok = Dialogos.confirmar("Cancelar orden",
                "¿Confirmas cancelar la orden #" + sel.getId_orden() + "?");
        if (!ok) return;
        try {
            Sesion.get().ordenes().cancelarOrden(sel.getId_orden());
            Dialogos.info("OK", "Orden cancelada.");
            refrescar();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Cancelar orden rechazado", e);
            Dialogos.error("No se pudo cancelar", e.getMessage());
        }
    }

    private void abrirFormulario(OrdenMantenimiento paraEditar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sigomei/cliente/vista/FormularioOrden.fxml"));
            Parent root = loader.load();
            FormularioOrdenController ctrl = loader.getController();
            ctrl.iniciar(paraEditar);

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle(paraEditar == null ? "Nueva orden" : "Editar orden #" + paraEditar.getId_orden());
            Scene scene = new Scene(root);
            Estilos.aplicar(scene);
            modal.setScene(scene);
            modal.showAndWait();

            if (ctrl.guardado()) refrescar();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "No se pudo abrir el formulario de orden", e);
            Dialogos.error("Error", "No se pudo abrir el formulario: " + e.getMessage());
        }
    }
}
