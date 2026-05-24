package sigomei.excepciones;

public class EquipoNoEncontradoException extends Exception {

    private static final long serialVersionUID = 1L;

    public EquipoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
