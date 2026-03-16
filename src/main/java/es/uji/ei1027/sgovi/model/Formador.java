package es.uji.ei1027.sgovi.model;

public class Formador {

    private int idFormador;
    private String dni;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;  // nullable

    public Formador() {}

    public int getIdFormador() { return idFormador; }
    public void setIdFormador(int idFormador) { this.idFormador = idFormador; }

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

    @Override
    public String toString() {
        return "Formador{" +
                "idFormador=" + idFormador +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
