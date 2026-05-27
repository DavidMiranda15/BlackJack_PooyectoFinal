package com.mycompany.Poker.Entidad;
import java.io.Serializable;

// JavaBean estándar para cumplir con los requerimientos de la Unidad III
public class ConfiguracionJuego implements Serializable {
    private static final long serialVersionUID = 1L;

    private int apuestaMinima = 100; // El valor por defecto de tu mesa

    // Constructor vacío obligatorio para cumplir el estándar JavaBean
    public ConfiguracionJuego() {
    }

    public int getApuestaMinima() {
        return apuestaMinima;
    }

    public void setApuestaMinima(int apuestaMinima) {
        this.apuestaMinima = apuestaMinima;
    }
}