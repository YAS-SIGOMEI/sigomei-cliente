package sigomei.excepciones;

public class TecnicoNoEncontradoException extends Exception {

    private static final long serialVersionUID = 1L;

    public TecnicoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
