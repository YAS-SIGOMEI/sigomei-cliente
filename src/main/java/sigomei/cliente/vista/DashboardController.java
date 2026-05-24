package sigomei.cliente.vista;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import sigomei.cliente.Sesion;
import sigomei.modelo.Usuario;

public class DashboardController {

    private static final Logger LOG = Logger.getLogger("sigomei.cliente.DashboardController");

    @FXML private Label lblBienvenida;
    @FXML private Label lblIniciales;

    @FXML
    public void initialize() {
        Usuario u = Sesion.get().usuarioActual();
        if (u != null) {
            String nombre = u.getNombre_usuario();
            lblBienvenida.setText(nombre + " · " + u.getRol());
            if (lblIniciales != null) {
                lblIniciales.setText(iniciales(nombre));
            }
        }
    }

    private static String iniciales(String texto) {
        if (texto == null || texto.isBlank()) return "·";
        String[] partes = texto.trim().split("\\s+|\\.|_|-");
        StringBuilder sb = new StringBuilder();
        for (String p : partes) {
            if (p.isEmpty()) continue;
            sb.append(Character.toUpperCase(p.charAt(0)));
            if (sb.length() >= 2) break;
        }
        return sb.length() == 0 ? "·" : sb.toString();
    }

    @FXML
    private void onLogout(ActionEvent ev) {
        Usuario u = Sesion.get().usuarioActual();
        if (u != null) {
            try {
                Sesion.get().autenticacion().logout(u.getId_usuario());
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Error en logout (continuamos)", e);
            }
            Sesion.get().setUsuarioActual(null);
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sigomei/cliente/vista/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setTitle("SIGOMEI — Iniciar sesión");
            Scene scene = new Scene(root);
            Estilos.aplicar(scene);
            stage.setScene(scene);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al volver al login", e);
        }
    }
}
