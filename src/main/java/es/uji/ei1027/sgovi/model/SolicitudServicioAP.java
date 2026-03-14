package es.uji.ei1027.sgovi.model;

import java.time.LocalDateTime;

public class SolicitudServicioAP {

    private int idSolicitud;
    private String idUsuario;
    private String tipoAsistencia;   // "PAP" o "PATI"
    private String estado;           // "en revisión", "aprobada",
    // "cerrada con contrato", "rechazada"
    private LocalDateTime fechaSolicitud;
    private String preferencias;     // nullable

    public SolicitudServicioAP() {}

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getTipoAsistencia() { return tipoAsistencia; }
    public void setTipoAsistencia(String tipoAsistencia) { this.tipoAsistencia = tipoAsistencia; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public String getPreferencias() { return preferencias; }
    public void setPreferencias(String preferencias) { this.preferencias = preferencias; }

    @Override
    public String toString() {
        return "SolicitudServicioAP{" +
                "idSolicitud=" + idSolicitud +
                ", idUsuario='" + idUsuario + '\'' +
                ", tipoAsistencia='" + tipoAsistencia + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaSolicitud=" + fechaSolicitud +
                ", preferencias='" + preferencias + '\'' +
                '}';
    }
}
