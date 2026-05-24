package sigomei.excepciones;

public class TecnicoInactivoException extends Exception {

    private static final long serialVersionUID = 1L;

    public TecnicoInactivoException(String mensaje) {
        super(mensaje);
    }
}
