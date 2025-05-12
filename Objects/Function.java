package Objects;

public class Function {
    private Movies movie;
    private String time;

    public Function(Movies movie, String time) {
        this.movie = movie;
        this.time = time;
    }

    public Movies getMovie() {
        return movie;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return movie.getTitle() + " - " + time;
    }
}