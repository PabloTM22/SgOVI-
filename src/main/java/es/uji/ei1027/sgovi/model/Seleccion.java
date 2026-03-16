package es.uji.ei1027.sgovi.model;

import java.time.LocalDateTime;

public class Seleccion {

    private int idSeleccion;
    private int idSolicitud;
    private String idAp;
    private String estado; // "propuesta","contactada","aceptada","descartada"
    private LocalDateTime fechaPropuesta;

    public Seleccion() {}

    public int getIdSeleccion() { return idSeleccion; }
    public void setIdSeleccion(int idSeleccion) { this.idSeleccion = idSeleccion; }

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }

    public String getIdAp() { return idAp; }
    public void setIdAp(String idAp) { this.idAp = idAp; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaPropuesta() { return fechaPropuesta; }
    public void setFechaPropuesta(LocalDateTime fechaPropuesta) { this.fechaPropuesta = fechaPropuesta; }

    @Override
    public String toString() {
        return "Seleccion{" +
                "idSeleccion=" + idSeleccion +
                ", idSolicitud=" + idSolicitud +
                ", idAp='" + idAp + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaPropuesta=" + fechaPropuesta +
                '}';
    }
}
