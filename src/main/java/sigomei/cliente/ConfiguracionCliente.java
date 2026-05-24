package sigomei.cliente;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Resuelve a donde conectar el cliente RMI:
 *   1. Variables de entorno SIGOMEI_RMI_HOST / SIGOMEI_RMI_PORT
 *   2. Archivo cliente.properties (ruta indicada en SIGOMEI_RMI_CONFIG, o raiz)
 *   3. Defaults: localhost:1099
 */
public final class ConfiguracionCliente {

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "1099";

    private final String host;
    private final int port;

    private ConfiguracionCliente(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static ConfiguracionCliente desdeEntorno() {
        Properties archivo = cargarArchivo();
        String host = resolver("SIGOMEI_RMI_HOST", archivo, "rmi.host", DEFAULT_HOST);
        String puerto = resolver("SIGOMEI_RMI_PORT", archivo, "rmi.port", DEFAULT_PORT);
        return new ConfiguracionCliente(host, Integer.parseInt(puerto));
    }

    public String host() { return host; }
    public int port() { return port; }
    public String descripcion() { return "rmi://" + host + ":" + port; }

    private static String resolver(String env, Properties props, String clave, String def) {
        String v = System.getenv(env);
        if (v != null && !v.isBlank()) return v;
        v = props.getProperty(clave);
        if (v != null && !v.isBlank()) return v;
        return def;
    }

    private static Properties cargarArchivo() {
        Properties props = new Properties();
        String ruta = System.getenv("SIGOMEI_RMI_CONFIG");
        Path p = (ruta != null && !ruta.isBlank())
                ? Paths.get(ruta)
                : Paths.get("cliente.properties");
        if (!Files.exists(p)) return props;
        try (InputStream in = Files.newInputStream(p)) {
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + p.toAbsolutePath(), e);
        }
        return props;
    }
}
