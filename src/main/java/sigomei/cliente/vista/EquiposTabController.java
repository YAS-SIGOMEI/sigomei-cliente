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
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sigomei.cliente.Sesion;
import sigomei.modelo.Equipo;

public class EquiposTabController {

    private static final Logger LOG = Logger.getLogger("sigomei.cliente.EquiposTabController");

    @FXML private TableView<Equipo> tabla;
    @FXML private TableColumn<Equipo, Number> colId;
    @FXML private TableColumn<Equipo, String> colNombre;
    @FXML private TableColumn<Equipo, String> colTipo;
    @FXML private TableColumn<Equipo, String> colCriticidad;
    @FXML private TableColumn<Equipo, String> colEstado;
    @FXML private TableColumn<Equipo, String> colSerie;
    @FXML private TableColumn<Equipo, String> colMarca;
    @FXML private TableColumn<Equipo, String> colModelo;
    @FXML private TableColumn<Equipo, String> colUbicacion;
    @FXML private Label lblMensaje;

    private final ObservableList<Equipo> datos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId_equipo()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getTipo())));
        colCriticidad.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCriticidad())));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getEstado_operativo())));
        colSerie.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumero_serie()));
        colMarca.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMarca()));
        colModelo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getModelo()));
        colUbicacion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUbicacion_planta()));

        tabla.setItems(datos);
        refrescar();
    }

    @FXML
    private void onRefrescar(ActionEvent ev) { refrescar(); }

    private void refrescar() {
        lblMensaje.setText("Cargando equipos...");
        try {
            List<Equipo> lista = Sesion.get().equipos().consultarEquipos(null);
            datos.setAll(lista);
            lblMensaje.setText("Equipos: " + lista.size());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al consultar equipos", e);
            Dialogos.error("Error", "No se pudieron consultar los equipos: " + e.getMessage());
        }
    }

    @FXML
    private void onNuevo(ActionEvent ev) {
        abrirFormulario(null);
    }

    @FXML
    private void onEditar(ActionEvent ev) {
        Equipo seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Dialogos.info("Editar", "Selecciona un equipo de la tabla para editar.");
            return;
        }
        abrirFormulario(seleccionado);
    }

    @FXML
    private void onDesactivar(ActionEvent ev) {
        Equipo seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Dialogos.info("Desactivar", "Selecciona un equipo de la tabla.");
            return;
        }
        boolean ok = Dialogos.confirmar("Desactivar equipo",
                "¿Confirmas desactivar el equipo \"" + seleccionado.getNombre() + "\"?");
        if (!ok) return;
        try {
            Sesion.get().equipos().desactivarEquipo(seleccionado.getId_equipo());
            Dialogos.info("OK", "Equipo desactivado.");
            refrescar();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Desactivar equipo rechazado", e);
            Dialogos.error("No se pudo desactivar", e.getMessage());
        }
    }

    private void abrirFormulario(Equipo paraEditar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sigomei/cliente/vista/FormularioEquipo.fxml"));
            javafx.scene.Parent root = loader.load();
            FormularioEquipoController ctrl = loader.getController();
            ctrl.iniciar(paraEditar);

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle(paraEditar == null ? "Nuevo equipo" : "Editar equipo");
            Scene scene = new Scene(root);
            Estilos.aplicar(scene);
            modal.setScene(scene);
            modal.showAndWait();

            if (ctrl.guardado()) {
                refrescar();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "No se pudo abrir el formulario de equipo", e);
            Dialogos.error("Error", "No se pudo abrir el formulario: " + e.getMessage());
        }
    }
}
