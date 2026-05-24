package sigomei.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Tecnico implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id_tecnico;
    private String nombre_completo;
    private String rfc;
    private String telefono;
    private String correo;
    private TipoEquipo especialidad;
    private NivelCertificacion nivel_certificacion;
    private LocalDate fecha_ingreso;
    private EstatusTecnico estatus;

    public Tecnico() {
    }

    public Tecnico(int id_tecnico, String nombre_completo, String rfc, String telefono,
        String correo, TipoEquipo especialidad, NivelCertificacion nivel_certificacion,
        LocalDate fecha_ingreso, EstatusTecnico estatus) {
        this.id_tecnico = id_tecnico;
        this.nombre_completo = nombre_completo;
        this.rfc = rfc;
        this.telefono = telefono;
        this.correo = correo;
        this.especialidad = especialidad;
        this.nivel_certificacion = nivel_certificacion;
        this.fecha_ingreso = fecha_ingreso;
        this.estatus = estatus;
    }

    public int getId_tecnico() {
        return id_tecnico;
    }

    public void setId_tecnico(int id_tecnico) {
        this.id_tecnico = id_tecnico;
    }

    public String getNombre_completo() {
        return nombre_completo;
    }

    public void setNombre_completo(String nombre_completo) {
        this.nombre_completo = nombre_completo;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public TipoEquipo getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(TipoEquipo especialidad) {
        this.especialidad = especialidad;
    }

    public NivelCertificacion getNivel_certificacion() {
        return nivel_certificacion;
    }

    public void setNivel_certificacion(NivelCertificacion nivel_certificacion) {
        this.nivel_certificacion = nivel_certificacion;
    }

    public LocalDate getFecha_ingreso() {
        return fecha_ingreso;
    }

    public void setFecha_ingreso(LocalDate fecha_ingreso) {
        this.fecha_ingreso = fecha_ingreso;
    }

    public EstatusTecnico getEstatus() {
        return estatus;
    }

    public void setEstatus(EstatusTecnico estatus) {
        this.estatus = estatus;
    }
}
