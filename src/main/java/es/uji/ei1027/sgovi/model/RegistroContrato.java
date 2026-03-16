package es.uji.ei1027.sgovi.model;
import java.time.LocalDate;

public class RegistroContrato {
    private int idContrato;
    private int idSeleccion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;     // nullable
    private String pdfRuta;         // nullable

    public RegistroContrato() {}

    public int getIdContrato() { return idContrato; }
    public void setIdContrato(int idContrato) { this.idContrato = idContrato; }

    public int getIdSeleccion() { return idSeleccion; }
    public void setIdSeleccion(int idSeleccion) { this.idSeleccion = idSeleccion; }

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
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", pdfRuta='" + pdfRuta + '\'' +
                '}';
    }
}


