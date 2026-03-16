package es.uji.ei1027.sgovi.model;

import java.time.LocalDateTime;

public class ActividadFormacion {

    private int idActividad;
    private int idFormador;
    private String tipoActividad;
    private String titulo;
    private String descripcion;     // nullable
    private LocalDateTime fechaActividad;
    private String lugar;           // nullable
    private Integer plazas;         // nullable

    public ActividadFormacion() {}

    public int getIdActividad() { return idActividad; }
    public void setIdActividad(int idActividad) { this.idActividad = idActividad; }

    public int getIdFormador() { return idFormador; }
    public void setIdFormador(int idFormador) { this.idFormador = idFormador; }

    public String getTipoActividad() { return tipoActividad; }
    public void setTipoActividad(String tipoActividad) { this.tipoActividad = tipoActividad; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaActividad() { return fechaActividad; }
    public void setFechaActividad(LocalDateTime fechaActividad) { this.fechaActividad = fechaActividad; }

    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    public Integer getPlazas() { return plazas; }
    public void setPlazas(Integer plazas) { this.plazas = plazas; }

    @Override
    public String toString() {
        return "ActividadFormacion{" +
                "idActividad=" + idActividad +
                ", idFormador=" + idFormador +
                ", tipoActividad='" + tipoActividad + '\'' +
                ", titulo='" + titulo + '\'' +
                ", fechaActividad=" + fechaActividad +
                ", lugar='" + lugar + '\'' +
                ", plazas=" + plazas +
                '}';
    }
}
