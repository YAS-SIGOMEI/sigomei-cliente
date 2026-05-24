package sigomei.cliente;

import sigomei.modelo.Usuario;
import sigomei.remoto.AutenticacionRemota;
import sigomei.remoto.EquipoRemoto;
import sigomei.remoto.OrdenRemota;
import sigomei.remoto.TecnicoRemoto;

/**
 * Sesion en memoria del cliente: referencias a los 4 servicios remotos y al
 * usuario autenticado. Singleton accesible desde los controllers.
 */
public final class Sesion {

    private static Sesion instancia;

    private final AutenticacionRemota autenticacion;
    private final EquipoRemoto equipos;
    private final TecnicoRemoto tecnicos;
    private final OrdenRemota ordenes;
    private Usuario usuarioActual;

    private Sesion(AutenticacionRemota a, EquipoRemoto e, TecnicoRemoto t, OrdenRemota o) {
        this.autenticacion = a;
        this.equipos = e;
        this.tecnicos = t;
        this.ordenes = o;
    }

    public static void inicializar(AutenticacionRemota a, EquipoRemoto e, TecnicoRemoto t, OrdenRemota o) {
        instancia = new Sesion(a, e, t, o);
    }

    public static Sesion get() {
        if (instancia == null) {
            throw new IllegalStateException("Sesion no inicializada. Llama a Sesion.inicializar primero.");
        }
        return instancia;
    }

    public AutenticacionRemota autenticacion() { return autenticacion; }
    public EquipoRemoto equipos() { return equipos; }
    public TecnicoRemoto tecnicos() { return tecnicos; }
    public OrdenRemota ordenes() { return ordenes; }

    public Usuario usuarioActual() { return usuarioActual; }
    public void setUsuarioActual(Usuario u) { this.usuarioActual = u; }
}
