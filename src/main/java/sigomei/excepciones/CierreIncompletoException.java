package sigomei.excepciones;

public class CierreIncompletoException extends Exception {

    private static final long serialVersionUID = 1L;

    public CierreIncompletoException(String mensaje) {
        super(mensaje);
    }
}
