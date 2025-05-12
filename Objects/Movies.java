package Objects;
public class Movies {
    private String title;
    private String genre;
    private String rating;
    private String description;

    public Movies(String title, String genre, String rating, String description) {
        this.title = title;
        this.genre = genre;
        this.rating = rating;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public String getClassification() {  // Method name to match what's used in CinemaGUI
        return rating;
    }
    
    public String getSinopsis() {  // Method name to match what's used in CinemaGUI
        return description;
    }

    @Override
    public String toString() {
        return title + " (" + genre + ")";
    }
}