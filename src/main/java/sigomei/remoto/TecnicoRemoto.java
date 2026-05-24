package sigomei.remoto;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import sigomei.excepciones.RfcDuplicadoException;
import sigomei.excepciones.TecnicoConOrdenesException;
import sigomei.excepciones.TecnicoNoEncontradoException;
import sigomei.modelo.FiltroTecnico;
import sigomei.modelo.Tecnico;

public interface TecnicoRemoto extends Remote {

    int registrarTecnico(Tecnico tecnico) throws RfcDuplicadoException, RemoteException;

    List<Tecnico> consultarTecnicos(FiltroTecnico filtro) throws RemoteException;

    Tecnico obtenerTecnicoPorId(int idTecnico) throws TecnicoNoEncontradoException, RemoteException;

    void modificarTecnico(Tecnico tecnico) throws RfcDuplicadoException, RemoteException;

    void desactivarTecnico(int idTecnico) throws TecnicoConOrdenesException, RemoteException;
}
