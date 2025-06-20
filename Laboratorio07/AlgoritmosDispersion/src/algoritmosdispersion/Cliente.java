package algoritmosdispersion;

/**
 * Modelo simple de cliente.
 */
public class Cliente{

    private final String dni;
    private String nombre;
    private String correo;

    public Cliente(String dni, String nombre, String correo) {
        this.dni = dni;
        this.nombre = nombre;
        this.correo = correo;
    }

    public String getDni() { return dni; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    @Override
    public String toString() {
        return nombre + " (" + dni + ")";
    }
}
