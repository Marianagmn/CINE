package Objects;

import java.sql.Date;

public class Function {
    // Attributes
    private String hour;// 10:00, 12:00, 14:00...
    private String day;// Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
    private String cinema;// Cinema name
    private String room;// Room number
    private String format;// 2D, 3D, IMAX
    private Date date; // Date of the function

    // Constructor
    public Function(String hour, String day, String cinema, String room, String format, Date date) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


}
