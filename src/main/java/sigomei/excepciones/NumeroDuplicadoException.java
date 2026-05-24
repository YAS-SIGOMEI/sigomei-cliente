package sigomei.excepciones;

public class NumeroDuplicadoException extends Exception {

    private static final long serialVersionUID = 1L;

    public NumeroDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
