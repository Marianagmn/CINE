package Objects;

// Class representing a Function
public class Function {
    // Private attributes
    private Movies movie;
    private String time;

    // Constructor
    public Function(Movies movie, String time) {
        this.movie = movie;
        this.time = time;
    }

    // Getters methods
    public Movies getMovie() {
        return movie;
    }

    public String getTime() {
        return time;
    }

    // Overridden toString method to return the Function name
    @Override
    public String toString() {
        return movie.getTitle() + " - " + time;
    }
}