package sigomei.excepciones;

public class CredencialesInvalidasException extends Exception {

    private static final long serialVersionUID = 1L;

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
