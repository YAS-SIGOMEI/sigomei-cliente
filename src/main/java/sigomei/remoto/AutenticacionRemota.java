package sigomei.remoto;

import java.rmi.Remote;
import java.rmi.RemoteException;

import sigomei.excepciones.CredencialesInvalidasException;
import sigomei.modelo.Usuario;

public interface AutenticacionRemota extends Remote {

    Usuario login(String nombreUsuario, String contrasena)
            throws CredencialesInvalidasException, RemoteException;

    void logout(int idUsuario) throws RemoteException;
}
