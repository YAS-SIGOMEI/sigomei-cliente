package sigomei.remoto;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import sigomei.excepciones.EquipoConOrdenesException;
import sigomei.excepciones.EquipoNoEncontradoException;
import sigomei.excepciones.NumeroDuplicadoException;
import sigomei.modelo.Equipo;
import sigomei.modelo.FiltroEquipo;

public interface EquipoRemoto extends Remote {

    int registrarEquipo(Equipo equipo) throws NumeroDuplicadoException, RemoteException;

    List<Equipo> consultarEquipos(FiltroEquipo filtro) throws RemoteException;

    Equipo obtenerEquipoPorId(int idEquipo) throws EquipoNoEncontradoException, RemoteException;

    void modificarEquipo(Equipo equipo) throws NumeroDuplicadoException, RemoteException;

    void desactivarEquipo(int idEquipo) throws EquipoConOrdenesException, RemoteException;
}
