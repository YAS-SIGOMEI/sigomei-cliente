package sigomei.cliente.vista;

import java.time.LocalDate;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sigomei.cliente.Sesion;
import sigomei.modelo.EstadoOrden;
import sigomei.modelo.OrdenMantenimiento;

public class OrdenesTabController {

    private static final Logger LOG = Logger.getLogger("sigomei.cliente.OrdenesTabController");
    private static final int PAGE_SIZE = 6;

    @FXML private TableView<OrdenMantenimiento> tabla;
    @FXML private TableColumn<OrdenMantenimiento, Number> colId;
    @FXML private TableColumn<OrdenMantenimiento, String> colEquipo;
    @FXML private TableColumn<OrdenMantenimiento, String> colTecnico;
    @FXML private TableColumn<OrdenMantenimiento, String> colTipo;
    @FXML private TableColumn<OrdenMantenimiento, String> colFechaProg;
    @FXML private TableColumn<OrdenMantenimiento, String> colEstado;
    @FXML private TableColumn<OrdenMantenimiento, Void> colAcciones;
    @FXML private Label lblMensaje;

    // Filtros
    @FXML private ComboBox<String> filtroEstado;
    @FXML private ComboBox<String> filtroEquipo;
    @FXML private ComboBox<String> filtroTecnico;
    @FXML private DatePicker filtroFecha;

    // Paginación
    @FXML private HBox paginacionBox;

    private final ObservableList<OrdenMantenimiento> datosTodos = FXCollections.observableArrayList();
    private final ObservableList<OrdenMantenimiento> datosFiltrados = FXCollections.observableArrayList();
    private final ObservableList<OrdenMantenimiento> datosPagina = FXCollections.observableArrayList();
    private int paginaActual = 0;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId_orden()));
        colEquipo.setCellValueFactory(c -> new SimpleStringProperty(
                "Equipo #" + c.getValue().getId_equipo()));
        colTecnico.setCellValueFactory(c -> new SimpleStringProperty(
                "Téc. #" + c.getValue().getId_tecnico()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getTipo_mantenimiento())));
        colFechaProg.setCellValueFactory(c -> new SimpleStringProperty(
                toStr(c.getValue().getFecha_programada())));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getEstado_orden())));

        configurarColumnaAcciones();
        tabla.setItems(datosPagina);

        // Inicializar filtros
        filtroEstado.getItems().add("Todos");
        for (EstadoOrden e : EstadoOrden.values()) filtroEstado.getItems().add(e.name());
        filtroEstado.getSelectionModel().selectFirst();

        filtroEquipo.getItems().add("Todos");
        filtroEquipo.getSelectionModel().selectFirst();

        filtroTecnico.getItems().add("Cualquiera");
        filtroTecnico.getSelectionModel().selectFirst();

        refrescar();
    }

    private static String toStr(Object o) { return o == null ? "" : String.valueOf(o); }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnVer = new Button("Ver");
            private final Button btnCambiarEstado = new Button("Cambiar estado");
            private final Button btnCancelar = new Button("Cancelar");
            private final HBox box = new HBox(4, btnVer, btnCambiarEstado, btnCancelar);

            {
                btnVer.getStyleClass().addAll("button", "button-sm");
                btnCambiarEstado.getStyleClass().addAll("button", "button-sm");
                btnCancelar.getStyleClass().addAll("button", "button-sm", "button-danger");
                box.setAlignment(Pos.CENTER_LEFT);

                btnVer.setOnAction(e -> {
                    OrdenMantenimiento o = getTableView().getItems().get(getIndex());
                    verDetalle(o);
                });
                btnCambiarEstado.setOnAction(e -> {
                    OrdenMantenimiento o = getTableView().getItems().get(getIndex());
                    abrirCambioEstado(o);
                });
                btnCancelar.setOnAction(e -> {
                    OrdenMantenimiento o = getTableView().getItems().get(getIndex());
                    cancelarOrden(o);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    OrdenMantenimiento o = getTableView().getItems().get(getIndex());
                    // Ocultar "Cancelar" si ya está FINALIZADA o CANCELADA
                    boolean puedeCancelar = o.getEstado_orden() != EstadoOrden.FINALIZADA
                            && o.getEstado_orden() != EstadoOrden.CANCELADA;
                    btnCancelar.setVisible(puedeCancelar);
                    btnCancelar.setManaged(puedeCancelar);

                    boolean puedeCambiar = o.getEstado_orden() != EstadoOrden.FINALIZADA
                            && o.getEstado_orden() != EstadoOrden.CANCELADA;
                    btnCambiarEstado.setVisible(puedeCambiar);
                    btnCambiarEstado.setManaged(puedeCambiar);

                    setGraphic(box);
                }
            }
        });
    }

    // ---------- Filtros ----------

    @FXML
    private void onBuscar(ActionEvent ev) { aplicarFiltros(); }

    @FXML
    private void onLimpiar(ActionEvent ev) {
        filtroEstado.getSelectionModel().selectFirst();
        filtroEquipo.getSelectionModel().selectFirst();
        filtroTecnico.getSelectionModel().selectFirst();
        filtroFecha.setValue(null);
        aplicarFiltros();
    }

    @FXML
    private void onRefrescar(ActionEvent ev) { refrescar(); }

    private void aplicarFiltros() {
        String estado = filtroEstado.getValue();
        String equipo = filtroEquipo.getValue();
        String tecnico = filtroTecnico.getValue();
        LocalDate fecha = filtroFecha.getValue();

        List<OrdenMantenimiento> resultado = datosTodos.stream()
                .filter(o -> "Todos".equals(estado) || String.valueOf(o.getEstado_orden()).equals(estado))
                .filter(o -> "Todos".equals(equipo) || ("Equipo #" + o.getId_equipo()).equals(equipo))
                .filter(o -> "Cualquiera".equals(tecnico) || ("Téc. #" + o.getId_tecnico()).equals(tecnico))
                .filter(o -> {
                    if (fecha == null) return true;
                    return fecha.equals(o.getFecha_programada());
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
        lblMensaje.setText("Cargando órdenes...");
        try {
            List<OrdenMantenimiento> lista = Sesion.get().ordenes().consultarOrdenes(null);
            datosTodos.setAll(lista);

            // Poblar filtros de equipo y técnico con datos reales
            poblarFiltrosCatalogo(lista);

            aplicarFiltros();
            lblMensaje.setText("Órdenes: " + lista.size());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al consultar órdenes", e);
            Dialogos.error("Error", "No se pudieron consultar las órdenes: " + e.getMessage());
        }
    }

    private void poblarFiltrosCatalogo(List<OrdenMantenimiento> lista) {
        // Equipos únicos
        String equipoSel = filtroEquipo.getValue();
        filtroEquipo.getItems().clear();
        filtroEquipo.getItems().add("Todos");
        lista.stream()
                .map(o -> "Equipo #" + o.getId_equipo())
                .distinct()
                .sorted()
                .forEach(s -> filtroEquipo.getItems().add(s));
        if (filtroEquipo.getItems().contains(equipoSel)) {
            filtroEquipo.setValue(equipoSel);
        } else {
            filtroEquipo.getSelectionModel().selectFirst();
        }

        // Técnicos únicos
        String tecSel = filtroTecnico.getValue();
        filtroTecnico.getItems().clear();
        filtroTecnico.getItems().add("Cualquiera");
        lista.stream()
                .map(o -> "Téc. #" + o.getId_tecnico())
                .distinct()
                .sorted()
                .forEach(s -> filtroTecnico.getItems().add(s));
        if (filtroTecnico.getItems().contains(tecSel)) {
            filtroTecnico.setValue(tecSel);
        } else {
            filtroTecnico.getSelectionModel().selectFirst();
        }
    }

    // ---------- Acciones ----------

    @FXML
    private void onNueva(ActionEvent ev) { abrirFormulario(null); }

    private void verDetalle(OrdenMantenimiento orden) {
        abrirFormulario(orden);
    }

    private void abrirCambioEstado(OrdenMantenimiento orden) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/sigomei/cliente/vista/CambioEstadoOrden.fxml"));
            Parent root = loader.load();
            CambioEstadoOrdenController ctrl = loader.getController();
            ctrl.iniciar(orden);

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Cambiar estado — OT-" + orden.getId_orden());
            Scene scene = new Scene(root);
            Estilos.aplicar(scene);
            modal.setScene(scene);
            modal.showAndWait();

            if (ctrl.confirmado()) {
                EstadoOrden nuevoEstado = ctrl.getEstadoSeleccionado();
                ejecutarCambioEstado(orden, nuevoEstado);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "No se pudo abrir el modal de cambio de estado", e);
            Dialogos.error("Error", "No se pudo abrir el modal: " + e.getMessage());
        }
    }

    private void ejecutarCambioEstado(OrdenMantenimiento orden, EstadoOrden nuevoEstado) {
        try {
            if (nuevoEstado == EstadoOrden.FINALIZADA) {
                // Abrir formulario de cierre
                abrirFormularioCierre(orden);
            } else if (nuevoEstado == EstadoOrden.CANCELADA) {
                Sesion.get().ordenes().cancelarOrden(orden.getId_orden());
                Dialogos.info("OK", "Orden #" + orden.getId_orden() + " → " + nuevoEstado);
                refrescar();
            } else {
                Sesion.get().ordenes().avanzarEstadoOrden(orden.getId_orden(), nuevoEstado);
                Dialogos.info("OK", "Orden #" + orden.getId_orden() + " → " + nuevoEstado);
                refrescar();
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Cambio de estado rechazado", e);
            Dialogos.error("No se pudo cambiar el estado", e.getMessage());
        }
    }

    private void cancelarOrden(OrdenMantenimiento orden) {
        boolean ok = Dialogos.confirmar("Cancelar orden",
                "¿Confirmas cancelar la orden #" + orden.getId_orden() + "?");
        if (!ok) return;
        try {
            Sesion.get().ordenes().cancelarOrden(orden.getId_orden());
            Dialogos.info("OK", "Orden cancelada.");
            refrescar();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Cancelar orden rechazado", e);
            Dialogos.error("No se pudo cancelar", e.getMessage());
        }
    }

    private void abrirFormularioCierre(OrdenMantenimiento orden) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/sigomei/cliente/vista/FormularioCierreOrden.fxml"));
            Parent root = loader.load();
            FormularioCierreOrdenController ctrl = loader.getController();
            ctrl.iniciar(orden);

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Cerrar orden #" + orden.getId_orden());
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

    private void abrirFormulario(OrdenMantenimiento paraEditar) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/sigomei/cliente/vista/FormularioOrden.fxml"));
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
