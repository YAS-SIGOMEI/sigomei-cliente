package sigomei.excepciones;

public class EspecialidadIncompatibleException extends Exception {

    private static final long serialVersionUID = 1L;

    public EspecialidadIncompatibleException(String mensaje) {
        super(mensaje);
    }
}
