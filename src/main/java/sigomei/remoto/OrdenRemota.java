package sigomei.remoto;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

import sigomei.excepciones.CertificacionInsuficienteException;
import sigomei.excepciones.CierreIncompletoException;
import sigomei.excepciones.EspecialidadIncompatibleException;
import sigomei.excepciones.FechaInvalidaException;
import sigomei.excepciones.OrdenActivaEnFechaException;
import sigomei.excepciones.OrdenNoEncontradaException;
import sigomei.excepciones.TecnicoInactivoException;
import sigomei.excepciones.TransicionInvalidaException;
import sigomei.modelo.EstadoOrden;
import sigomei.modelo.FiltroOrden;
import sigomei.modelo.OrdenMantenimiento;

public interface OrdenRemota extends Remote {

    int registrarOrden(OrdenMantenimiento orden)
            throws EspecialidadIncompatibleException,
                   OrdenActivaEnFechaException,
                   TecnicoInactivoException,
                   CertificacionInsuficienteException,
                   RemoteException;

    List<OrdenMantenimiento> consultarOrdenes(FiltroOrden filtro) throws RemoteException;

    OrdenMantenimiento obtenerOrdenPorId(int idOrden)
            throws OrdenNoEncontradaException, RemoteException;

    void modificarOrden(OrdenMantenimiento orden)
            throws FechaInvalidaException, EspecialidadIncompatibleException, RemoteException;

    void cancelarOrden(int idOrden) throws TransicionInvalidaException, RemoteException;

    void avanzarEstadoOrden(int idOrden, EstadoOrden nuevoEstado)
            throws TransicionInvalidaException, RemoteException;

    void cerrarOrden(int idOrden, Double costoReal, LocalDate fechaCierre)
            throws CierreIncompletoException, TransicionInvalidaException, RemoteException;
}
