package com.mycompany.Poker.Negocio;

import com.mycompany.Poker.Entidad.Carta;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogicaJuego {

    public static List<Carta> crearMazo() {
        List<Carta> mazo = new ArrayList<>();
        String[] palos = {"Corazones", "Diamantes", "Treboles", "Picas"};

        for (String palo : palos) {
            for (int valor = 1; valor <= 13; valor++) {
                mazo.add(new Carta(valor, palo));
            }
        }
        Collections.shuffle(mazo);
        return mazo;
    }

    public static int calcularPuntaje(List<Carta> mano) {
        int total = 0;
        int ases = 0;

        for (Carta carta : mano) {
            int v = carta.getValor();
            if (v == 1) {
                ases++;
                total += 11;
            } else if (v >= 10) {
                total += 10;
            } else {
                total += v;
            }
        }

        while (total > 21 && ases > 0) {
            total -= 10;
            ases--;
        }

        return total;
    }

    public static int evaluarJugada(List<Carta> manoJugador, List<Carta> manoDealer) {
        int puntosJugador = calcularPuntaje(manoJugador);
        int puntosDealer = calcularPuntaje(manoDealer);

        if (puntosJugador > 21) {
            return -1;
        }

        if (puntosDealer > 21) {
            return 1;
        }

        if (puntosJugador > puntosDealer) {
            return 1;
        } else if (puntosDealer > puntosJugador) {
            return -1;
        } else {
            return 0;
        }
    }
}
