package sigomei.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class FiltroOrden implements Serializable {

    private static final long serialVersionUID = 1L;

    private EstadoOrden estadoOrden;
    private Integer idEquipo;
    private Integer idTecnico;
    private LocalDate fechaProgramada;

    public FiltroOrden() {
    }

    public FiltroOrden(EstadoOrden estadoOrden, Integer idEquipo, Integer idTecnico,
                       LocalDate fechaProgramada) {
        this.estadoOrden = estadoOrden;
        this.idEquipo = idEquipo;
        this.idTecnico = idTecnico;
        this.fechaProgramada = fechaProgramada;
    }

    public EstadoOrden getEstadoOrden() {
        return estadoOrden;
    }

    public void setEstadoOrden(EstadoOrden estadoOrden) {
        this.estadoOrden = estadoOrden;
    }

    public Integer getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Integer idEquipo) {
        this.idEquipo = idEquipo;
    }

    public Integer getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(Integer idTecnico) {
        this.idTecnico = idTecnico;
    }

    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDate fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }
}
