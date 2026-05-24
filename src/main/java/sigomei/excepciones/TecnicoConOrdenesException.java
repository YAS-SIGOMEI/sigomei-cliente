package sigomei.excepciones;

public class TecnicoConOrdenesException extends Exception {

    private static final long serialVersionUID = 1L;

    public TecnicoConOrdenesException(String mensaje) {
        super(mensaje);
    }
}
