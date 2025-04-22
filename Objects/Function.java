package Objects;

import java.time.LocalDate;

public class Function {
    // Attributes
    private String hour; // 10:00, 12:00, 14:00...
    private String day; // Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
    private String cinema; // Cinema name
    private String room; // Room number
    private String format; // 2D, 3D, IMAX
    private LocalDate date; // Date of the function

    // Constructor
    public Function(String hour, String day, String cinema, String room, String format, LocalDate date) {
        this.hour = hour;
        this.day = day;
        this.cinema = cinema;
        this.room = room;
        this.format = format;
        this.date = date;
    }

    // Getters and Setters
    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getCinema() {
        return cinema;
    }

    public void setCinema(String cinema) {
        this.cinema = cinema;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Methods

    // Method to display function details
    public void functionDetails() {
        System.out.println("Function Details:");
        System.out.println("Hour: " + hour);
        System.out.println("Day: " + day);
        System.out.println("Cinema: " + cinema);
        System.out.println("Room: " + room);
        System.out.println("Format: " + format);
        System.out.println("Date: " + date);
    }

    // Method to check if the function is available on a specific date
    public boolean isAvailable(LocalDate date) {
        return this.date.equals(date);
    }

    // Method to check if the function is available on a specific day
    public boolean isAvailableD(String day) {
        return this.day.equalsIgnoreCase(day);
    }

    // Method to check if the function is available at a specific hour
    public boolean isAvailableH(String hour) {
        return this.hour.equalsIgnoreCase(hour);
    }

    // Method to check if the function is available in a specific cinema
    public boolean isAvailableC(String cinema) {
        return this.cinema.equalsIgnoreCase(cinema);
    }

    // Method to check if the function is available in a specific format
    public boolean isFormat(String format) {
        return this.format.equalsIgnoreCase(format);
    }

    // Method to get a brief description of the function
    public String getBriefDescription() {
        return "Cinema: " + cinema + ", Room: " + room + ", Format: " + format + ", Hour: " + hour;
    }
}