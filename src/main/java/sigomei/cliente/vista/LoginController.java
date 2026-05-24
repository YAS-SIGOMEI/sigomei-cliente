package sigomei.cliente.vista;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sigomei.cliente.Sesion;
import sigomei.excepciones.CredencialesInvalidasException;
import sigomei.modelo.Usuario;

public class LoginController {

    private static final Logger LOG = Logger.getLogger("sigomei.cliente.LoginController");

    @FXML private TextField campoUsuario;
    @FXML private PasswordField campoContrasena;
    @FXML private Label mensajeError;
    @FXML private VBox cajaError;

    private void mostrarError(String texto) {
        mensajeError.setText(texto);
        boolean visible = texto != null && !texto.isBlank();
        if (cajaError != null) {
            cajaError.setVisible(visible);
            cajaError.setManaged(visible);
        }
    }

    @FXML
    private void onLogin(ActionEvent ev) {
        mostrarError("");
        String usuario = campoUsuario.getText() == null ? "" : campoUsuario.getText().trim();
        String pass = campoContrasena.getText() == null ? "" : campoContrasena.getText();
        if (usuario.isBlank() || pass.isEmpty()) {
            mostrarError("Usuario y contraseña son obligatorios.");
            return;
        }

        try {
            // Si el servidor almacena las contraseñas hasheadas, el hash se calcula del lado servidor
            // a partir del valor en claro que enviamos. Aqui solo enviamos el plano.
            Usuario u = Sesion.get().autenticacion().login(usuario, pass);
            Sesion.get().setUsuarioActual(u);
            LOG.info("Login OK usuario=" + u.getNombre_usuario() + " rol=" + u.getRol());
            abrirDashboard(ev);
        } catch (CredencialesInvalidasException e) {
            LOG.warning("Login fallido: " + e.getMessage());
            mostrarError("Usuario o contraseña inválidos.");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error inesperado al iniciar sesion", e);
            mostrarError("No se pudo contactar al servidor. " + e.getMessage());
        }
    }

    private void abrirDashboard(ActionEvent ev) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sigomei/cliente/vista/Dashboard.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) campoUsuario.getScene().getWindow();
        stage.setTitle("SIGOMEI — Dashboard");
        Scene scene = new Scene(root);
        Estilos.aplicar(scene);
        stage.setScene(scene);
        stage.setMaximized(true);
    }

    // Helper por si en algun momento queremos hashear del lado cliente (no usado ahora).
    @SuppressWarnings("unused")
    private static String sha256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
