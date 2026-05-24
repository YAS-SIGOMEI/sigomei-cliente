package sigomei.cliente.vista;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Helpers de UI para mostrar dialogos de error, informacion y confirmacion.
 */
public final class Dialogos {

    private Dialogos() {}

    public static void error(String titulo, String mensaje) {
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    public static void info(String titulo, String mensaje) {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    public static boolean confirmar(String titulo, String mensaje) {
        Alert a = new Alert(AlertType.CONFIRMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        Optional<ButtonType> r = a.showAndWait();
        return r.isPresent() && r.get() == ButtonType.OK;
    }
}
