package Objects;
public class Seat {
    // Attributes
    private int num; // Número de asiento
    private String letter; // Letra del asiento
    private boolean available; // true = disponible, false = no disponible
    private boolean isVIP; // true = VIP, false = normal
    private double priceStandard; // Precio estándar

    // Constructor
    public Seat(int num, String letter, boolean available, boolean isVIP, double priceStandard) {
        if (num <= 0) {
            throw new IllegalArgumentException("El número de asiento debe ser positivo.");
        }
        if (priceStandard < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        this.num = num;
        this.letter = letter;
        this.available = available;
        this.isVIP = isVIP;
        this.priceStandard = priceStandard;
    }

    // Getters and Setters
    public String getSeat() {
        return letter + num; 
    }

    public int getNum() {
        return num; 
    }

    public void setNum(int num) {
        if (num <= 0) {
            throw new IllegalArgumentException("El número de asiento debe ser positivo.");
        }
        this.num = num; 
    }

    public String getLetter() {
        return letter; 
    }

    public void setLetter(String letter) {
        this.letter = letter; 
    }

    public boolean isAvailable() {
        return available; 
    }

    public void setAvailable(boolean available) {
        this.available = available; 
    }

    public boolean isVIP() {
        return isVIP; 
    }

    public void setVIP(boolean isVIP) {
        this.isVIP = isVIP; // Establece si el asiento es VIP
    }

    public double getPriceStandard() {
        return priceStandard; // Devuelve el precio estándar del asiento
    }

    public void setPriceStandard(double priceStandard) {
        if (priceStandard < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        this.priceStandard = priceStandard; // Establece el precio estándar del asiento
    }

    // Methods

       // Method to reserve a seat

    public void reserveSeat() {
        if (available) {
            available = false; // Cambia la disponibilidad a no disponible
        } else {
            System.out.println("El asiento " + getSeat() + " ya está reservado.");
        }
    }

     // Method to cancel a reservation

    public void cancelReservation() {
        if (!available) {
            available = true; // Cambia la disponibilidad a disponible
        } else {
            System.out.println("El asiento " + getSeat() + " ya está disponible.");
        }
    }

       // Method to get the final price of the seat
    // If the seat is VIP, the price is 1.5 times the standard price

    public double getFinalPrice() {
        return isVIP ? priceStandard * 1.5 : priceStandard; 
    }

   // Method to get a brief description of the seat

    public String getBriefDescription() {
        return "Asiento " + getSeat() + " - " + (isVIP ? "VIP" : "Estándar") + " - " + (available ? "Disponible" : "Reservado");
    }
}