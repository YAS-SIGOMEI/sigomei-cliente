package sigomei.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class OrdenMantenimiento implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id_orden;
    private int id_equipo;
    private int id_tecnico;
    private int id_usuario;
    private TipoMantenimiento tipo_mantenimiento;
    private LocalDate fecha_programada;
    private LocalDate fecha_inicio;
    private LocalDate fecha_cierre;
    private String descripcion_trabajo;
    private Double costo_estimado;
    private Double costo_real;
    private EstadoOrden estado_orden;

    public OrdenMantenimiento() {
    }

    public OrdenMantenimiento(int id_orden, int id_equipo, int id_tecnico, int id_usuario,
                              TipoMantenimiento tipo_mantenimiento, LocalDate fecha_programada,
                              LocalDate fecha_inicio, LocalDate fecha_cierre,
                              String descripcion_trabajo, Double costo_estimado,
                              Double costo_real, EstadoOrden estado_orden) {
        this.id_orden = id_orden;
        this.id_equipo = id_equipo;
        this.id_tecnico = id_tecnico;
        this.id_usuario = id_usuario;
        this.tipo_mantenimiento = tipo_mantenimiento;
        this.fecha_programada = fecha_programada;
        this.fecha_inicio = fecha_inicio;
        this.fecha_cierre = fecha_cierre;
        this.descripcion_trabajo = descripcion_trabajo;
        this.costo_estimado = costo_estimado;
        this.costo_real = costo_real;
        this.estado_orden = estado_orden;
    }

    public int getId_orden() {
        return id_orden;
    }

    public void setId_orden(int id_orden) {
        this.id_orden = id_orden;
    }

    public int getId_equipo() {
        return id_equipo;
    }

    public void setId_equipo(int id_equipo) {
        this.id_equipo = id_equipo;
    }

    public int getId_tecnico() {
        return id_tecnico;
    }

    public void setId_tecnico(int id_tecnico) {
        this.id_tecnico = id_tecnico;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public TipoMantenimiento getTipo_mantenimiento() {
        return tipo_mantenimiento;
    }

    public void setTipo_mantenimiento(TipoMantenimiento tipo_mantenimiento) {
        this.tipo_mantenimiento = tipo_mantenimiento;
    }

    public LocalDate getFecha_programada() {
        return fecha_programada;
    }

    public void setFecha_programada(LocalDate fecha_programada) {
        this.fecha_programada = fecha_programada;
    }

    public LocalDate getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(LocalDate fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public LocalDate getFecha_cierre() {
        return fecha_cierre;
    }

    public void setFecha_cierre(LocalDate fecha_cierre) {
        this.fecha_cierre = fecha_cierre;
    }

    public String getDescripcion_trabajo() {
        return descripcion_trabajo;
    }

    public void setDescripcion_trabajo(String descripcion_trabajo) {
        this.descripcion_trabajo = descripcion_trabajo;
    }

    public Double getCosto_estimado() {
        return costo_estimado;
    }

    public void setCosto_estimado(Double costo_estimado) {
        this.costo_estimado = costo_estimado;
    }

    public Double getCosto_real() {
        return costo_real;
    }

    public void setCosto_real(Double costo_real) {
        this.costo_real = costo_real;
    }

    public EstadoOrden getEstado_orden() {
        return estado_orden;
    }

    public void setEstado_orden(EstadoOrden estado_orden) {
        this.estado_orden = estado_orden;
    }
}
