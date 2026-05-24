package sigomei.excepciones;

public class OrdenActivaEnFechaException extends Exception {

    private static final long serialVersionUID = 1L;

    public OrdenActivaEnFechaException(String mensaje) {
        super(mensaje);
    }
}
