package Objects;
public class Seats {
    // Attributes
    private int num; // Seat number
    private String letter; // Letter of the seat (A, B, C, etc.)
    private boolean available; // true = available, false = reserved
    private boolean isVIP; // true = VIP, false = normal
    private double priceStandard; // Standard price of the seat

    // Constructor
    public Seats(int num, String letter, boolean available, boolean isVIP, double priceStandard) {

      // Validate inputs
        if (letter == null || letter.length() != 1) {
            throw new IllegalArgumentException("La letra del asiento debe ser una sola letra.");
        }
        if (num <= 0) {
            throw new IllegalArgumentException("El número de asiento debe ser positivo.");
        }
        if (priceStandard < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }

        // Initialize attributes
        this.num = num;
        this.letter = letter;
        this.available = available;
        this.isVIP = isVIP;
        this.priceStandard = priceStandard;
    }

    // Getters and Setters

    public String getSeat() {
        return letter + num;
    } // Returns the seat in the format "A1", "B2", etc.

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
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
        this.isVIP = isVIP;
    }

    public double getPriceStandard() {
        return priceStandard;
    }

    public void setPriceStandard(double priceStandard) {
        this.priceStandard = priceStandard;
    }

    // Methods

    // Method to reserve a seat

    public void reserveSeat() {
        if (available) {
            available = false; // Change the availability to reserved
        } else {
            System.out.println("El asiento " + getSeat() + " ya está reservado.");
        }
    }

     // Method to cancel a reservation

    public void cancelReservation() {
        if (!available) {
            available = true; // change the availability to available
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