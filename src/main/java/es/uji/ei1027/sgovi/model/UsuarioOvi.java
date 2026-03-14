package es.uji.ei1027.sgovi.model;

public class UsuarioOvi {
    private String idUsuario;
    private String dni;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private boolean consentimientoLopd;
    private String contrasena;
    private boolean aceptadoTecnico;

    public UsuarioOvi() {}

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isConsentimientoLopd() {
        return consentimientoLopd;
    }

    public void setConsentimientoLopd(boolean consentimientoLopd) {
        this.consentimientoLopd = consentimientoLopd;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public boolean isAceptadoTecnico() {
        return aceptadoTecnico;
    }

    public void setAceptadoTecnico(boolean aceptadoTecnico) {
        this.aceptadoTecnico = aceptadoTecnico;
    }

    public String toString() {
        return "UsuarioOVI{" +
                "idUsuario='" + idUsuario + '\'' +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", consentimientoLopd=" + consentimientoLopd +
                ", aceptadoTecnico=" + aceptadoTecnico +
                '}';
    }
}
