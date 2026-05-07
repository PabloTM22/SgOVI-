

package es.uji.ei1027.sgovi.model;

import java.time.LocalDate;

public class RegistroContrato {
    private int idContrato;
    private int idSeleccion;
    private String idTecnico;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String pdfRuta;

    public RegistroContrato() {}

    public int getIdContrato() { return idContrato; }
    public void setIdContrato(int idContrato) { this.idContrato = idContrato; }

    public int getIdSeleccion() { return idSeleccion; }
    public void setIdSeleccion(int idSeleccion) { this.idSeleccion = idSeleccion; }

    public String getIdTecnico() { return idTecnico; }
    public void setIdTecnico(String idTecnico) { this.idTecnico = idTecnico; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public String getPdfRuta() { return pdfRuta; }
    public void setPdfRuta(String pdfRuta) { this.pdfRuta = pdfRuta; }

    @Override
    public String toString() {
        return "RegistroContrato{" +
                "idContrato=" + idContrato +
                ", idSeleccion=" + idSeleccion +
                ", idTecnico='" + idTecnico + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", pdfRuta='" + pdfRuta + '\'' +
                '}';
    }
}


