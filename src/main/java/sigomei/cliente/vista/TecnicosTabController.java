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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sigomei.cliente.Sesion;
import sigomei.modelo.Tecnico;

public class TecnicosTabController {

    private static final Logger LOG = Logger.getLogger("sigomei.cliente.TecnicosTabController");
    private static final int PAGE_SIZE = 7;

    @FXML private TableView<Tecnico> tabla;
    @FXML private TableColumn<Tecnico, Number> colId;
    @FXML private TableColumn<Tecnico, String> colNombre;
    @FXML private TableColumn<Tecnico, String> colRfc;
    @FXML private TableColumn<Tecnico, String> colEspecialidad;
    @FXML private TableColumn<Tecnico, String> colNivel;
    @FXML private TableColumn<Tecnico, String> colEstatus;
    @FXML private TableColumn<Tecnico, String> colTelefono;
    @FXML private TableColumn<Tecnico, String> colCorreo;
    @FXML private TableColumn<Tecnico, Void> colAcciones;
    @FXML private Label lblMensaje;

    // Filtros
    @FXML private ComboBox<String> filtroEspecialidad;
    @FXML private ComboBox<String> filtroNivel;
    @FXML private ComboBox<String> filtroEstatus;
    @FXML private TextField filtroBusqueda;

    // Paginación
    @FXML private HBox paginacionBox;

    private final ObservableList<Tecnico> datosTodos = FXCollections.observableArrayList();
    private final ObservableList<Tecnico> datosFiltrados = FXCollections.observableArrayList();
    private final ObservableList<Tecnico> datosPagina = FXCollections.observableArrayList();
    private int paginaActual = 0;

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

        configurarColumnaAcciones();
        tabla.setItems(datosPagina);

        // Inicializar filtros con opción "Todas/Todos" + se llenan al cargar datos
        filtroEspecialidad.getItems().add("Todas");
        filtroEspecialidad.getSelectionModel().selectFirst();

        filtroNivel.getItems().add("Todos");
        filtroNivel.getSelectionModel().selectFirst();

        filtroEstatus.getItems().add("Todos");
        filtroEstatus.getSelectionModel().selectFirst();

        refrescar();
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnDesactivar = new Button("Eliminar");
            private final HBox box = new HBox(6, btnEditar, btnDesactivar);

            {
                btnEditar.getStyleClass().addAll("button", "button-sm");
                btnDesactivar.getStyleClass().addAll("button", "button-sm", "button-danger");
                box.setAlignment(Pos.CENTER_LEFT);

                btnEditar.setOnAction(e -> {
                    Tecnico tec = getTableView().getItems().get(getIndex());
                    abrirFormulario(tec);
                });
                btnDesactivar.setOnAction(e -> {
                    Tecnico tec = getTableView().getItems().get(getIndex());
                    desactivar(tec);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    // ---------- Filtros ----------

    @FXML
    private void onBuscar(ActionEvent ev) { aplicarFiltros(); }

    @FXML
    private void onLimpiar(ActionEvent ev) {
        filtroEspecialidad.getSelectionModel().selectFirst();
        filtroNivel.getSelectionModel().selectFirst();
        filtroEstatus.getSelectionModel().selectFirst();
        filtroBusqueda.clear();
        aplicarFiltros();
    }

    @FXML
    private void onRefrescar(ActionEvent ev) { refrescar(); }

    private void aplicarFiltros() {
        String especialidad = filtroEspecialidad.getValue();
        String nivel = filtroNivel.getValue();
        String estatus = filtroEstatus.getValue();
        String busqueda = filtroBusqueda.getText() == null ? "" : filtroBusqueda.getText().trim().toLowerCase();

        List<Tecnico> resultado = datosTodos.stream()
                .filter(t -> "Todas".equals(especialidad) || String.valueOf(t.getEspecialidad()).equals(especialidad))
                .filter(t -> "Todos".equals(nivel) || String.valueOf(t.getNivel_certificacion()).equals(nivel))
                .filter(t -> "Todos".equals(estatus) || String.valueOf(t.getEstatus()).equals(estatus))
                .filter(t -> {
                    if (busqueda.isEmpty()) return true;
                    String nombre = t.getNombre_completo() == null ? "" : t.getNombre_completo().toLowerCase();
                    String rfc = t.getRfc() == null ? "" : t.getRfc().toLowerCase();
                    String correo = t.getCorreo() == null ? "" : t.getCorreo().toLowerCase();
                    return nombre.contains(busqueda) || rfc.contains(busqueda) || correo.contains(busqueda);
                })
                .collect(Collectors.toList());

        datosFiltrados.setAll(resultado);
        paginaActual = 0;
        actualizarPagina();
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

    // ---------- Data ----------

    private void refrescar() {
        lblMensaje.setText("Cargando técnicos...");
        try {
            List<Tecnico> lista = Sesion.get().tecnicos().consultarTecnicos(null);
            datosTodos.setAll(lista);

            // Poblar filtros con valores únicos
            poblarFiltros(lista);

            aplicarFiltros();
            lblMensaje.setText("Técnicos: " + lista.size());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al consultar técnicos", e);
            Dialogos.error("Error", "No se pudieron consultar los técnicos: " + e.getMessage());
        }
    }

    private void poblarFiltros(List<Tecnico> lista) {
        String espSel = filtroEspecialidad.getValue();
        filtroEspecialidad.getItems().clear();
        filtroEspecialidad.getItems().add("Todas");
        lista.stream()
                .map(t -> String.valueOf(t.getEspecialidad()))
                .distinct().sorted()
                .forEach(s -> filtroEspecialidad.getItems().add(s));
        if (filtroEspecialidad.getItems().contains(espSel)) {
            filtroEspecialidad.setValue(espSel);
        } else {
            filtroEspecialidad.getSelectionModel().selectFirst();
        }

        String nivSel = filtroNivel.getValue();
        filtroNivel.getItems().clear();
        filtroNivel.getItems().add("Todos");
        lista.stream()
                .map(t -> String.valueOf(t.getNivel_certificacion()))
                .distinct().sorted()
                .forEach(s -> filtroNivel.getItems().add(s));
        if (filtroNivel.getItems().contains(nivSel)) {
            filtroNivel.setValue(nivSel);
        } else {
            filtroNivel.getSelectionModel().selectFirst();
        }

        String estSel = filtroEstatus.getValue();
        filtroEstatus.getItems().clear();
        filtroEstatus.getItems().add("Todos");
        lista.stream()
                .map(t -> String.valueOf(t.getEstatus()))
                .distinct().sorted()
                .forEach(s -> filtroEstatus.getItems().add(s));
        if (filtroEstatus.getItems().contains(estSel)) {
            filtroEstatus.setValue(estSel);
        } else {
            filtroEstatus.getSelectionModel().selectFirst();
        }
    }

    // ---------- Acciones ----------

    @FXML
    private void onNuevo(ActionEvent ev) { abrirFormulario(null); }

    private void desactivar(Tecnico tecnico) {
        boolean ok = Dialogos.confirmar("Desactivar técnico",
                "¿Confirmas desactivar al técnico \"" + tecnico.getNombre_completo() + "\"?");
        if (!ok) return;
        try {
            Sesion.get().tecnicos().desactivarTecnico(tecnico.getId_tecnico());
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
