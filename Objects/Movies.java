package Objects;
public class Movies {
    // Attributes
    private String language; // English or Spanish
    private String cast; // Actors
    private String director; // Director
    private String title; // Movie title
    private String genre; // Action, Comedy, Drama, Horror, etc.
    private int minutes; // Duration in minutes
    private String classification; // +16, +18, etc.
    private String synopsis; // Movie synopsis

    // Constructor
    public Movies(String language, String cast, String director, String title, String genre, int minutes,
                  String classification, String synopsis) {
        this.language = language;
        this.cast = cast;
        this.director = director;
        this.title = title;
        this.genre = genre;
        this.minutes = minutes;
        this.classification = classification;
        this.synopsis = synopsis;
    }

    // Getters and Setters
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Duration cannot be negative.");
        }
        this.minutes = minutes;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getMovieDetails() {
        return "Title: " + title + "\n" +
               "Director: " + director + "\n" +
               "Cast: " + cast + "\n" +
               "Language: " + language + "\n" +
               "Genre: " + genre + "\n" +
               "Duration: " + minutes + " minutes\n" +
               "Classification: " + classification + "\n" +
               "Synopsis: " + synopsis;
    }
}