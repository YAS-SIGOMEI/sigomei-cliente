package sigomei.excepciones;

public class TransicionInvalidaException extends Exception {

    private static final long serialVersionUID = 1L;

    public TransicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
