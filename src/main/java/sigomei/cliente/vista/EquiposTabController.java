package sigomei.cliente.vista;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import sigomei.cliente.Sesion;
import sigomei.modelo.Criticidad;
import sigomei.modelo.Equipo;
import sigomei.modelo.EstadoOperativo;
import sigomei.modelo.TipoEquipo;

public class EquiposTabController {

    private static final Logger LOG = Logger.getLogger("sigomei.cliente.EquiposTabController");
    private static final int PAGE_SIZE = 7;

    @FXML private TableView<Equipo> tabla;
    @FXML private TableColumn<Equipo, Number> colId;
    @FXML private TableColumn<Equipo, String> colNombre;
    @FXML private TableColumn<Equipo, String> colTipo;
    @FXML private TableColumn<Equipo, String> colSerie;
    @FXML private TableColumn<Equipo, String> colCriticidad;
    @FXML private TableColumn<Equipo, String> colEstado;
    @FXML private TableColumn<Equipo, Void> colAcciones;
    @FXML private Label lblMensaje;

    // Filtros
    @FXML private ComboBox<String> filtroTipo;
    @FXML private ComboBox<String> filtroCriticidad;
    @FXML private ComboBox<String> filtroEstado;
    @FXML private TextField filtroBusqueda;

    // Panel lateral
    @FXML private SplitPane splitPane;
    @FXML private VBox panelLateral;
    @FXML private VBox formularioEquipo;

    // Paginación
    @FXML private HBox paginacionBox;

    @FXML private FormularioEquipoController formularioEquipoController;
    private final ObservableList<Equipo> datosTodos = FXCollections.observableArrayList();
    private final ObservableList<Equipo> datosFiltrados = FXCollections.observableArrayList();
    private final ObservableList<Equipo> datosPagina = FXCollections.observableArrayList();
    private int paginaActual = 0;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId_equipo()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getTipo())));
        colSerie.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumero_serie()));
        colCriticidad.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCriticidad())));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getEstado_operativo())));

        configurarColumnaAcciones();
        tabla.setItems(datosPagina);

        // Inicializar filtros
        filtroTipo.getItems().add("Todos");
        for (TipoEquipo t : TipoEquipo.values()) filtroTipo.getItems().add(t.name());
        filtroTipo.getSelectionModel().selectFirst();

        filtroCriticidad.getItems().add("Todas");
        for (Criticidad c : Criticidad.values()) filtroCriticidad.getItems().add(c.name());
        filtroCriticidad.getSelectionModel().selectFirst();

        filtroEstado.getItems().add("Todos");
        for (EstadoOperativo e : EstadoOperativo.values()) filtroEstado.getItems().add(e.name());
        filtroEstado.getSelectionModel().selectFirst();

        // Ocultar panel lateral al inicio
        ocultarPanelLateral();

        refrescar();
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox box = new HBox(6, btnEditar, btnEliminar);

            {
                btnEditar.getStyleClass().addAll("button", "button-sm");
                btnEliminar.getStyleClass().addAll("button", "button-sm", "button-danger");
                box.setAlignment(Pos.CENTER_LEFT);

                btnEditar.setOnAction(e -> {
                    Equipo eq = getTableView().getItems().get(getIndex());
                    abrirFormulario(eq);
                });
                btnEliminar.setOnAction(e -> {
                    Equipo eq = getTableView().getItems().get(getIndex());
                    eliminar(eq);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    @FXML
    private void onBuscar(ActionEvent ev) { aplicarFiltros(); }

    @FXML
    private void onLimpiar(ActionEvent ev) {
        filtroTipo.getSelectionModel().selectFirst();
        filtroCriticidad.getSelectionModel().selectFirst();
        filtroEstado.getSelectionModel().selectFirst();
        filtroBusqueda.clear();
        aplicarFiltros();
    }

    @FXML
    private void onRefrescar(ActionEvent ev) { refrescar(); }

    private void aplicarFiltros() {
        String tipo = filtroTipo.getValue();
        String criticidad = filtroCriticidad.getValue();
        String estado = filtroEstado.getValue();
        String busqueda = filtroBusqueda.getText() == null ? "" : filtroBusqueda.getText().trim().toLowerCase();

        List<Equipo> resultado = datosTodos.stream()
                .filter(e -> "Todos".equals(tipo) || String.valueOf(e.getTipo()).equals(tipo))
                .filter(e -> "Todas".equals(criticidad) || String.valueOf(e.getCriticidad()).equals(criticidad))
                .filter(e -> "Todos".equals(estado) || String.valueOf(e.getEstado_operativo()).equals(estado))
                .filter(e -> {
                    if (busqueda.isEmpty()) return true;
                    String id = String.valueOf(e.getId_equipo()).toLowerCase();
                    String nombre = e.getNombre() == null ? "" : e.getNombre().toLowerCase();
                    String serie = e.getNumero_serie() == null ? "" : e.getNumero_serie().toLowerCase();
                    return id.contains(busqueda) || nombre.contains(busqueda) || serie.contains(busqueda);
                })
                .collect(Collectors.toList());

        datosFiltrados.setAll(resultado);
        paginaActual = 0;
        actualizarPagina();
    }

    private void refrescar() {
        lblMensaje.setText("Cargando equipos...");
        try {
            List<Equipo> lista = Sesion.get().equipos().consultarEquipos(null);
            datosTodos.setAll(lista);
            aplicarFiltros();
            lblMensaje.setText("Equipos: " + lista.size());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al consultar equipos", e);
            Dialogos.error("Error", "No se pudieron consultar los equipos: " + e.getMessage());
        }
    }

    // ---------- Paginación ----------

    private void actualizarPagina() {
        int total = datosFiltrados.size();
        int totalPaginas = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        if (paginaActual >= totalPaginas) paginaActual = totalPaginas - 1;
        if (paginaActual < 0) paginaActual = 0;

        int desde = paginaActual * PAGE_SIZE;
        int hasta = Math.min(desde + PAGE_SIZE, total);

        datosPagina.setAll(datosFiltrados.subList(desde, hasta));
        construirPaginacion(total, totalPaginas, desde + 1, hasta);
    }

    private void construirPaginacion(int total, int totalPaginas, int desde, int hasta) {
        paginacionBox.getChildren().clear();
        paginacionBox.setSpacing(6);
        paginacionBox.setAlignment(Pos.CENTER);

        Label info = new Label("Mostrando " + desde + "–" + hasta + " de " + total);
        info.getStyleClass().add("label-muted");
        paginacionBox.getChildren().add(info);

        Region spacer = new Region();
        spacer.setMinWidth(12);
        paginacionBox.getChildren().add(spacer);

        Button btnPrev = new Button("‹");
        btnPrev.getStyleClass().add("page-btn");
        btnPrev.setDisable(paginaActual == 0);
        btnPrev.setOnAction(e -> { paginaActual--; actualizarPagina(); });
        paginacionBox.getChildren().add(btnPrev);

        for (int i = 0; i < totalPaginas; i++) {
            Button btn = new Button(String.valueOf(i + 1));
            btn.getStyleClass().add(i == paginaActual ? "page-btn-active" : "page-btn");
            final int pagina = i;
            btn.setOnAction(e -> { paginaActual = pagina; actualizarPagina(); });
            paginacionBox.getChildren().add(btn);
        }

        Button btnNext = new Button("›");
        btnNext.getStyleClass().add("page-btn");
        btnNext.setDisable(paginaActual >= totalPaginas - 1);
        btnNext.setOnAction(e -> { paginaActual++; actualizarPagina(); });
        paginacionBox.getChildren().add(btnNext);
    }

    // ---------- Acciones ----------

    @FXML
    private void onNuevo(ActionEvent ev) {
        abrirFormulario(null);
    }

    private void abrirFormulario(Equipo paraEditar) {
        if (formularioEquipoController == null) {
            abrirFormularioModal(paraEditar);
            return;
        }
        formularioEquipoController.iniciar(paraEditar);
        formularioEquipoController.setOnGuardado(() -> {
            refrescar();
            ocultarPanelLateral();
        });
        formularioEquipoController.setOnCancelado(this::ocultarPanelLateral);
        mostrarPanelLateral();
    }

    private void abrirFormularioModal(Equipo paraEditar) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/sigomei/cliente/vista/FormularioEquipo.fxml"));
            javafx.scene.Parent root = loader.load();
            FormularioEquipoController ctrl = loader.getController();
            ctrl.iniciar(paraEditar);

            javafx.stage.Stage modal = new javafx.stage.Stage();
            modal.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            modal.setTitle(paraEditar == null ? "Nuevo equipo" : "Editar equipo");
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
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

    private void eliminar(Equipo equipo) {
        boolean ok = Dialogos.confirmar("Eliminar equipo",
                "¿Confirmas eliminar el equipo \"" + equipo.getNombre() + "\"?");
        if (!ok) return;
        try {
            Sesion.get().equipos().desactivarEquipo(equipo.getId_equipo());
            Dialogos.info("OK", "Equipo eliminado.");
            refrescar();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Eliminar equipo rechazado", e);
            Dialogos.error("No se pudo eliminar", e.getMessage());
        }
    }

    private void mostrarPanelLateral() {
        panelLateral.setVisible(true);
        panelLateral.setManaged(true);
        splitPane.setDividerPositions(0.6);
    }

    private void ocultarPanelLateral() {
        panelLateral.setVisible(false);
        panelLateral.setManaged(false);
        splitPane.setDividerPositions(1.0);
    }
}
