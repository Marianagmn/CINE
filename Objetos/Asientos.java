package Objetos;

public class Asientos {
    private int numero;
    private String letraFila;
    private boolean ocupado;
    private boolean preferencial;
    private boolean precio;

    public Asientos(int numero, String letraFila, boolean ocupado, boolean preferencial, boolean precio) {
        this.numero = numero;
        this.letraFila = letraFila;
        this.ocupado = ocupado;
        this.preferencial = preferencial;
        this.precio = precio;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getLetraFila() {
        return letraFila;
    }

    public void setLetraFila(String letraFila) {
        this.letraFila = letraFila;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public boolean isPreferencial() {
        return preferencial;
    }

    public void setPreferencial(boolean preferencial) {
        this.preferencial = preferencial;
    }

    public boolean isPrecio() {
        return precio;
    }

    public void setPrecio(boolean precio) {
        this.precio = precio;
    }

}
