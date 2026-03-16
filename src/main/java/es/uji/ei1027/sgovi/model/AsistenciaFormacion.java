package es.uji.ei1027.sgovi.model;

import java.time.LocalDateTime;

public class AsistenciaFormacion {

    private int idAsistencia;
    private int idActividad;
    private String idUsuario;       // nullable (o id_ap, nunca los dos)
    private String idAp;            // nullable
    private LocalDateTime fechaInscripcion;
    private boolean asistio;
    private String certificadoRuta; // nullable

    public AsistenciaFormacion() {}

    public int getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(int idAsistencia) { this.idAsistencia = idAsistencia; }

    public int getIdActividad() { return idActividad; }
    public void setIdActividad(int idActividad) { this.idActividad = idActividad; }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getIdAp() { return idAp; }
    public void setIdAp(String idAp) { this.idAp = idAp; }

    public LocalDateTime getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDateTime fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    public boolean isAsistio() { return asistio; }
    public void setAsistio(boolean asistio) { this.asistio = asistio; }

    public String getCertificadoRuta() { return certificadoRuta; }
    public void setCertificadoRuta(String certificadoRuta) { this.certificadoRuta = certificadoRuta; }

    @Override
    public String toString() {
        return "AsistenciaFormacion{" +
                "idAsistencia=" + idAsistencia +
                ", idActividad=" + idActividad +
                ", idUsuario='" + idUsuario + '\'' +
                ", idAp='" + idAp + '\'' +
                ", fechaInscripcion=" + fechaInscripcion +
                ", asistio=" + asistio +
                ", certificadoRuta='" + certificadoRuta + '\'' +
                '}';
    }
}

