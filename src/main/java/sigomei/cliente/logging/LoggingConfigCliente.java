package sigomei.cliente.logging;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Configura java.util.logging para el cliente SIGOMEI:
 *   - Archivo rotado sigomei-cliente.%g.log (3 archivos, 2 MB c/u).
 *   - Consola.
 */
public final class LoggingConfigCliente {

    private LoggingConfigCliente() {}

    public static void inicializar() {
        Logger raiz = Logger.getLogger("sigomei");
        raiz.setUseParentHandlers(false);
        for (Handler h : raiz.getHandlers()) {
            raiz.removeHandler(h);
        }

        Formatter formato = new FormatoSigomei();

        ConsoleHandler consola = new ConsoleHandler();
        consola.setLevel(Level.INFO);
        consola.setFormatter(formato);
        raiz.addHandler(consola);

        try {
            FileHandler archivo = new FileHandler("sigomei-cliente.%g.log", 2 * 1024 * 1024, 3, true);
            archivo.setLevel(Level.INFO);
            archivo.setFormatter(formato);
            raiz.addHandler(archivo);
        } catch (IOException e) {
            raiz.log(Level.SEVERE, "No se pudo abrir el archivo de log del cliente", e);
        }

        raiz.setLevel(Level.INFO);
    }

    private static final class FormatoSigomei extends Formatter {
        private final SimpleDateFormat fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder(160);
            sb.append(fechaHora.format(new Date(record.getMillis())))
              .append(" | ").append(String.format("%-7s", record.getLevel().getName()))
              .append(" | ").append(record.getSourceClassName())
              .append('.').append(record.getSourceMethodName())
              .append(" | ").append(formatMessage(record))
              .append(System.lineSeparator());
            if (record.getThrown() != null) {
                Throwable t = record.getThrown();
                sb.append("    -> ").append(t.getClass().getName())
                  .append(": ").append(t.getMessage()).append(System.lineSeparator());
            }
            return sb.toString();
        }
    }
}
