package Objects;

// Representative class
public class Cinema {
    // Private attribute
    private String nombre;

    // Constructor that receives the cinema name
    public Cinema(String nombre) {
        this.nombre = nombre;
    }
    // Getter method
    public String getNombre() {
        return nombre;
    }

    // Overridden toString method to return the cinema name
    @Override
    public String toString() {
        return nombre;
    }
}
