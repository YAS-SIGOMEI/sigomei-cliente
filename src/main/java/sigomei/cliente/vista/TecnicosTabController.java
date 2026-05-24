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
import sigomei.modelo.Tecnico;

public class TecnicosTabController {

    private static final Logger LOG = Logger.getLogger("sigomei.cliente.TecnicosTabController");

    @FXML private TableView<Tecnico> tabla;
    @FXML private TableColumn<Tecnico, Number> colId;
    @FXML private TableColumn<Tecnico, String> colNombre;
    @FXML private TableColumn<Tecnico, String> colRfc;
    @FXML private TableColumn<Tecnico, String> colEspecialidad;
    @FXML private TableColumn<Tecnico, String> colNivel;
    @FXML private TableColumn<Tecnico, String> colEstatus;
    @FXML private TableColumn<Tecnico, String> colTelefono;
    @FXML private TableColumn<Tecnico, String> colCorreo;
    @FXML private Label lblMensaje;

    private final ObservableList<Tecnico> datos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId_tecnico()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre_completo()));
        colRfc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRfc()));
        colEspecialidad.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getEspecialidad())));
        colNivel.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getNivel_certificacion())));
        colEstatus.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getEstatus())));
        colTelefono.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelefono()));
        colCorreo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCorreo()));

        tabla.setItems(datos);
        refrescar();
    }

    @FXML
    private void onRefrescar(ActionEvent ev) { refrescar(); }

    private void refrescar() {
        lblMensaje.setText("Cargando técnicos...");
        try {
            List<Tecnico> lista = Sesion.get().tecnicos().consultarTecnicos(null);
            datos.setAll(lista);
            lblMensaje.setText("Técnicos: " + lista.size());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al consultar técnicos", e);
            Dialogos.error("Error", "No se pudieron consultar los técnicos: " + e.getMessage());
        }
    }

    @FXML
    private void onNuevo(ActionEvent ev) { abrirFormulario(null); }

    @FXML
    private void onEditar(ActionEvent ev) {
        Tecnico sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Dialogos.info("Editar", "Selecciona un técnico de la tabla para editar.");
            return;
        }
        abrirFormulario(sel);
    }

    @FXML
    private void onDesactivar(ActionEvent ev) {
        Tecnico sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Dialogos.info("Desactivar", "Selecciona un técnico de la tabla.");
            return;
        }
        boolean ok = Dialogos.confirmar("Desactivar técnico",
                "¿Confirmas desactivar al técnico \"" + sel.getNombre_completo() + "\"?");
        if (!ok) return;
        try {
            Sesion.get().tecnicos().desactivarTecnico(sel.getId_tecnico());
            Dialogos.info("OK", "Técnico desactivado.");
            refrescar();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Desactivar técnico rechazado", e);
            Dialogos.error("No se pudo desactivar", e.getMessage());
        }
    }

    private void abrirFormulario(Tecnico paraEditar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sigomei/cliente/vista/FormularioTecnico.fxml"));
            Parent root = loader.load();
            FormularioTecnicoController ctrl = loader.getController();
            ctrl.iniciar(paraEditar);

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle(paraEditar == null ? "Nuevo técnico" : "Editar técnico");
            Scene scene = new Scene(root);
            Estilos.aplicar(scene);
            modal.setScene(scene);
            modal.showAndWait();

            if (ctrl.guardado()) refrescar();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "No se pudo abrir el formulario de técnico", e);
            Dialogos.error("Error", "No se pudo abrir el formulario: " + e.getMessage());
        }
    }
}
