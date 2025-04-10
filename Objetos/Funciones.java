package Objetos;

import java.sql.Date;

public class Funciones {
    private String hora;
    private String dia;
    private String mes;
    private String numDia;
    private String cine;

    public Funciones(String hora, String dia, String mes, String numDia, String cine) {
        this.hora = hora;
        this.dia = dia;
        this.mes = mes;
        this.numDia = numDia;
        this.cine = cine;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getNumDia() {
        return numDia;
    }

    public void setNumDia(String numDia) {
        this.numDia = numDia;
    }

    public String getCine() {
        return cine;
    }

    public void setCine(String cine) {
        this.cine = cine;
    }

}
