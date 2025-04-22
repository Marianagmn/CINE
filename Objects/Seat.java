package Objects;

public class Seat{

    // Attributes
    private int num;// 1, 2, 3, 4, 5, 6, 7, 8, 9, 10... 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30
    private String letter;// A, B, C, D, E, F, G, H, I, J
    private boolean available;// true = available, false = not available
    private boolean VIP;// true = VIP, false = normal
    private double priceStandard; //Standard price

    // Constructor
    public Seat(int num, String letter, boolean available, boolean vIP, double priceStandard) {
        this.num = num;
        this.letter = letter;
        this.available = available;
        VIP = vIP;
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
        return VIP;
    }
    public void setVIP(boolean vIP) {
        VIP = vIP;
    }
    public double getPriceStandard() {
        return priceStandard;
    }
    public void setPriceStandard(double priceStandard) {
        this.priceStandard = priceStandard;
    }

    // toString method
    @Override
    public String toString() {
        return "Seat{" + getSeat()+
                "Available= " + available +
                "VIP= " + VIP +
                "Price= " + priceStandard +
                '}';
    }
}
