package Objetos;

public class Peliculas {
    private String idioma;
    private String actores;
    private String director;
    private String titulo;
    private String genero;
    private int minutos;
    private String clasificacion;
    private String formato;

    public Peliculas(String idioma, String actores, String director, String titulo, String genero, int minutos,
            String clasificacion, String formato) {
        this.idioma = idioma;
        this.actores = actores;
        this.director = director;
        this.titulo = titulo;
        this.genero = genero;
        this.minutos = minutos;
        this.clasificacion = clasificacion;
        this.formato = formato;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getActores() {
        return actores;
    }

    public void setActores(String actores) {
        this.actores = actores;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

}