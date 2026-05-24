package sigomei.excepciones;

public class EquipoConOrdenesException extends Exception {

    private static final long serialVersionUID = 1L;

    public EquipoConOrdenesException(String mensaje) {
        super(mensaje);
    }
}
