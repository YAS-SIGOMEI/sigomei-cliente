package sigomei.modelo;

import java.io.Serializable;

public class FiltroEquipo implements Serializable {

    private static final long serialVersionUID = 1L;

    private TipoEquipo tipo;
    private Criticidad criticidad;
    private EstadoOperativo estadoOperativo;
    private String textoBusqueda;

    public FiltroEquipo() {
    }

    public FiltroEquipo(TipoEquipo tipo, Criticidad criticidad,
        EstadoOperativo estadoOperativo, String textoBusqueda) {
        this.tipo = tipo;
        this.criticidad = criticidad;
        this.estadoOperativo = estadoOperativo;
        this.textoBusqueda = textoBusqueda;
    }

    public TipoEquipo getTipo() {
        return tipo;
    }

    public void setTipo(TipoEquipo tipo) {
        this.tipo = tipo;
    }

    public Criticidad getCriticidad() {
        return criticidad;
    }

    public void setCriticidad(Criticidad criticidad) {
        this.criticidad = criticidad;
    }

    public EstadoOperativo getEstadoOperativo() {
        return estadoOperativo;
    }

    public void setEstadoOperativo(EstadoOperativo estadoOperativo) {
        this.estadoOperativo = estadoOperativo;
    }

    public String getTextoBusqueda() {
        return textoBusqueda;
    }

    public void setTextoBusqueda(String textoBusqueda) {
        this.textoBusqueda = textoBusqueda;
    }
}
