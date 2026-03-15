package es.uji.ei1027.sgovi.model;

public class Candidato {

    private String idAp;
    private String dni;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private String tipoAp; // "PAP" o "PATI"
    private String formacion;
    private String experiencia;
    private String disponibilidad;
    private Double latitud;
    private Double longitud;
    private boolean estadoAceptado;
    private String contrasena;
    private boolean activo;

    public Candidato() {
    }

    // --- GETTERS Y SETTERS ---
    public String getIdAp() { return idAp; }
    public void setIdAp(String idAp) { this.idAp = idAp; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getTipoAp() { return tipoAp; }
    public void setTipoAp(String tipoAp) { this.tipoAp = tipoAp; }

    public String getFormacion() { return formacion; }
    public void setFormacion(String formacion) { this.formacion = formacion; }

    public String getExperiencia() { return experiencia; }
    public void setExperiencia(String experiencia) { this.experiencia = experiencia; }

    public String getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(String disponibilidad) { this.disponibilidad = disponibilidad; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public boolean isEstadoAceptado() { return estadoAceptado; }
    public void setEstadoAceptado(boolean estadoAceptado) { this.estadoAceptado = estadoAceptado; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Candidato{" +
                "idAp='" + idAp + '\'' +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", tipoAp='" + tipoAp + '\'' +
                ", formacion='" + formacion + '\'' +
                ", experiencia='" + experiencia + '\'' +
                ", disponibilidad='" + disponibilidad + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", estadoAceptado=" + estadoAceptado +
                ", contrasena='" + contrasena + '\'' +
                ", activo=" + activo +
                '}';
    }
}
