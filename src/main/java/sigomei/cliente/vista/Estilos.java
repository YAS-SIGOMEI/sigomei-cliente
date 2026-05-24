package sigomei.cliente.vista;

import javafx.scene.Scene;

public final class Estilos {

    private static final String HOJA = "/sigomei/cliente/vista/styles.css";

    private Estilos() {}

    public static void aplicar(Scene scene) {
        if (scene == null) return;
        String url = Estilos.class.getResource(HOJA).toExternalForm();
        if (!scene.getStylesheets().contains(url)) {
            scene.getStylesheets().add(url);
        }
    }
}
