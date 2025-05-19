package Objects;

// Class representing a City
public class City {
    // Private attribute
    private String name;

    // Constructor that receives the City name
    public City(String name) {
        this.name = name;
    }

    // Getter method
    public String getName() {
        return name;
    }

    // Overridden toString method to return the City name
    @Override
    public String toString() {
        return name;
    }
}