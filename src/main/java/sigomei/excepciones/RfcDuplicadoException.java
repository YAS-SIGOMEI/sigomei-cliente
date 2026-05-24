package sigomei.excepciones;

public class RfcDuplicadoException extends Exception {

    private static final long serialVersionUID = 1L;

    public RfcDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
