package sigomei.cliente;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sigomei.cliente.logging.LoggingConfigCliente;
import sigomei.cliente.vista.Estilos;
import sigomei.remoto.AutenticacionRemota;
import sigomei.remoto.EquipoRemoto;
import sigomei.remoto.OrdenRemota;
import sigomei.remoto.TecnicoRemoto;

public class ClienteMain extends Application {

    private static final Logger LOG = Logger.getLogger("sigomei.cliente.ClienteMain");

    public static void main(String[] args) {
        LoggingConfigCliente.inicializar();
        Application.launch(ClienteMain.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        ConfiguracionCliente config = ConfiguracionCliente.desdeEntorno();
        LOG.info("Conectando al servidor RMI " + config.descripcion());

        try {
            Registry registry = LocateRegistry.getRegistry(config.host(), config.port());
            AutenticacionRemota auth = (AutenticacionRemota) registry.lookup("AutenticacionService");
            EquipoRemoto equipos = (EquipoRemoto) registry.lookup("EquipoService");
            TecnicoRemoto tecnicos = (TecnicoRemoto) registry.lookup("TecnicoService");
            OrdenRemota ordenes = (OrdenRemota) registry.lookup("OrdenService");

            Sesion.inicializar(auth, equipos, tecnicos, ordenes);
            LOG.info("Servicios RMI listos");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "No se pudo conectar al servidor " + config.descripcion(), e);
            throw e;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sigomei/cliente/vista/Login.fxml"));
        Parent root = loader.load();
        stage.setTitle("SIGOMEI — Iniciar sesión");
        Scene scene = new Scene(root);
        Estilos.aplicar(scene);
        stage.setScene(scene);
        stage.show();
    }
}
