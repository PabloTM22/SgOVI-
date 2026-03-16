package es.uji.ei1027.sgovi.model;

import java.time.LocalDateTime;

public class ComunicacionUsuario {

    private int idComunicacion;
    private int idSeleccion;
    private String idUsuario;   // nullable
    private String idAp;        // nullable
    private String idTecnico;   // nullable
    private String mensaje;
    private LocalDateTime fechaEnvio;

    public ComunicacionUsuario() {}

    public int getIdComunicacion() { return idComunicacion; }
    public void setIdComunicacion(int idComunicacion) { this.idComunicacion = idComunicacion; }

    public int getIdSeleccion() { return idSeleccion; }
    public void setIdSeleccion(int idSeleccion) { this.idSeleccion = idSeleccion; }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getIdAp() { return idAp; }
    public void setIdAp(String idAp) { this.idAp = idAp; }

    public String getIdTecnico() { return idTecnico; }
    public void setIdTecnico(String idTecnico) { this.idTecnico = idTecnico; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    @Override
    public String toString() {
        return "ComunicacionUsuario{" +
                "idComunicacion=" + idComunicacion +
                ", idSeleccion=" + idSeleccion +
                ", idUsuario='" + idUsuario + '\'' +
                ", idAp='" + idAp + '\'' +
                ", idTecnico='" + idTecnico + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", fechaEnvio=" + fechaEnvio +
                '}';
    }
}
