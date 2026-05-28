package com.mycompany.Poker.Entidad;
import java.io.Serializable;

public class ConfiguracionJuego implements Serializable {
    private static final long serialVersionUID = 1L;

    private int apuestaMinima = 100; 

    public ConfiguracionJuego() {
    }

    public int getApuestaMinima() {
        return apuestaMinima;
    }

    public void setApuestaMinima(int apuestaMinima) {
        this.apuestaMinima = apuestaMinima;
    }
}