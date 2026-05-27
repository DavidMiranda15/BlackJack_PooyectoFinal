package com.mycompany.Poker.Negocio;

import com.mycompany.Poker.Entidad.Carta;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogicaJuego {
    
    // Genera un mazo completo de 52 cartas y lo baraja aleatoriamente
    public static List<Carta> crearMazo() {
        List<Carta> mazo = new ArrayList<>();
        String[] palos = {"Corazones", "Diamantes", "Treboles", "Picas"};
        
        for (String palo : palos) {
            for (int valor = 1; valor <= 13; valor++) {
                mazo.add(new Carta(valor, palo));
            }
        }
        // Baraja el mazo de forma aleatoria
        Collections.shuffle(mazo);
        return mazo;
    }
    
    // Calcula el puntaje total de una mano de cartas, optimizando el valor del As (1 u 11)
    public static int calcularPuntaje(List<Carta> mano) {
        int total = 0;
        int ases = 0;
        
        for (Carta carta : mano) {
            int v = carta.getValor();
            if (v == 1) {
                ases++;
                total += 11; // Contamos el As inicialmente como 11
            } else if (v >= 10) {
                total += 10; // J, Q, K valen 10 en Blackjack
            } else {
                total += v;  // Cartas del 2 al 9 valen su número
            }
        }
        
        // Si el puntaje pasa de 21 y tenemos ases contados como 11, los reducimos a 1
        while (total > 21 && ases > 0) {
            total -= 10;
            ases--;
        }
        
        return total;
    }
    
    // Compara la jugada final del jugador contra el Dealer para ver si gana, pierde o empata
    // Retorna: 1 si gana el jugador, -1 si gana la casa, 0 si hay empate (Push)
    public static int evaluarJugada(List<Carta> manoJugador, List<Carta> manoDealer) {
        int puntosJugador = calcularPuntaje(manoJugador);
        int puntosDealer = calcularPuntaje(manoDealer);
        
        // Si el jugador se pasa de 21, pierde automáticamente
        if (puntosJugador > 21) {
            return -1;
        }
        
        // Si el Dealer se pasa de 21 (y el jugador no), el jugador gana
        if (puntosDealer > 21) {
            return 1;
        }
        
        // Si nadie se pasó, gana el que tenga mayor puntaje
        if (puntosJugador > puntosDealer) {
            return 1;
        } else if (puntosDealer > puntosJugador) {
            return -1;
        } else {
            return 0; // Empate absoluto, se devuelve la apuesta intacta
        }
    }
}