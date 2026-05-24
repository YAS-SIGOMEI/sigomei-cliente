package sigomei.excepciones;

public class CertificacionInsuficienteException extends Exception {

    private static final long serialVersionUID = 1L;

    public CertificacionInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
