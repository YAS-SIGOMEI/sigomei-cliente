package sigomei.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Equipo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id_equipo;
    private String nombre;
    private TipoEquipo tipo;
    private String marca;
    private String modelo;
    private String numero_serie;
    private String ubicacion_planta;
    private LocalDate fecha_instalacion;
    private EstadoOperativo estado_operativo;
    private Criticidad criticidad;

    public Equipo() {
    }

    public Equipo(int id_equipo, String nombre, TipoEquipo tipo, String marca, String modelo,
                  String numero_serie, String ubicacion_planta, LocalDate fecha_instalacion,
                  EstadoOperativo estado_operativo, Criticidad criticidad) {
        this.id_equipo = id_equipo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.marca = marca;
        this.modelo = modelo;
        this.numero_serie = numero_serie;
        this.ubicacion_planta = ubicacion_planta;
        this.fecha_instalacion = fecha_instalacion;
        this.estado_operativo = estado_operativo;
        this.criticidad = criticidad;
    }

    public int getId_equipo() {
        return id_equipo;
    }

    public void setId_equipo(int id_equipo) {
        this.id_equipo = id_equipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoEquipo getTipo() {
        return tipo;
    }

    public void setTipo(TipoEquipo tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNumero_serie() {
        return numero_serie;
    }

    public void setNumero_serie(String numero_serie) {
        this.numero_serie = numero_serie;
    }

    public String getUbicacion_planta() {
        return ubicacion_planta;
    }

    public void setUbicacion_planta(String ubicacion_planta) {
        this.ubicacion_planta = ubicacion_planta;
    }

    public LocalDate getFecha_instalacion() {
        return fecha_instalacion;
    }

    public void setFecha_instalacion(LocalDate fecha_instalacion) {
        this.fecha_instalacion = fecha_instalacion;
    }

    public EstadoOperativo getEstado_operativo() {
        return estado_operativo;
    }

    public void setEstado_operativo(EstadoOperativo estado_operativo) {
        this.estado_operativo = estado_operativo;
    }

    public Criticidad getCriticidad() {
        return criticidad;
    }

    public void setCriticidad(Criticidad criticidad) {
        this.criticidad = criticidad;
    }
}
