package sigomei.modelo;

import java.io.Serializable;

public class FiltroTecnico implements Serializable {

    private static final long serialVersionUID = 1L;

    private TipoEquipo especialidad;
    private NivelCertificacion nivelCertificacion;
    private EstatusTecnico estatus;
    private String textoBusqueda;

    public FiltroTecnico() {
    }

    public FiltroTecnico(TipoEquipo especialidad, NivelCertificacion nivelCertificacion,
                         EstatusTecnico estatus, String textoBusqueda) {
        this.especialidad = especialidad;
        this.nivelCertificacion = nivelCertificacion;
        this.estatus = estatus;
        this.textoBusqueda = textoBusqueda;
    }

    public TipoEquipo getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(TipoEquipo especialidad) {
        this.especialidad = especialidad;
    }

    public NivelCertificacion getNivelCertificacion() {
        return nivelCertificacion;
    }

    public void setNivelCertificacion(NivelCertificacion nivelCertificacion) {
        this.nivelCertificacion = nivelCertificacion;
    }

    public EstatusTecnico getEstatus() {
        return estatus;
    }

    public void setEstatus(EstatusTecnico estatus) {
        this.estatus = estatus;
    }

    public String getTextoBusqueda() {
        return textoBusqueda;
    }

    public void setTextoBusqueda(String textoBusqueda) {
        this.textoBusqueda = textoBusqueda;
    }
}
