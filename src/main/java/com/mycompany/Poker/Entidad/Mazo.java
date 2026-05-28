package com.mycompany.Poker.Entidad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mazo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Carta> cartas;

    public Mazo() {
        cartas = new ArrayList<>();
        String[] palos = {"CORAZONES", "DIAMANTES", "TREBOLES", "PICAS"};
        
        for (String palo : palos) {
            for (int v = 1; v <= 13; v++) {
                cartas.add(new Carta(v, palo));
            }
        }
    }

    public void barajar() {
        Collections.shuffle(cartas);
    }

    public Carta robarCarta() {
        if (!cartas.isEmpty()) {
            return cartas.remove(0);
        }
        return null;
    }

    public int cartasRestantes() {
        return cartas.size();
    }
}