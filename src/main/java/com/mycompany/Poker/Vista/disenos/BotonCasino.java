package com.mycompany.Poker.Vista.disenos;

import javax.swing.*;
import java.awt.*;

public class BotonCasino extends JButton {
    private int radioEsquinas;

    // El constructor recibe el texto, el color de fondo, el color de la letra y qué tan redondo lo quieres
    public BotonCasino(String texto, Color fondo, Color textoColor, int radioEsquinas) {
        super(texto);
        this.radioEsquinas = radioEsquinas;
        
        setFont(new Font("Arial", Font.BOLD, 14));
        setForeground(textoColor);
        setBackground(fondo);
        setCursor(new Cursor(Cursor.HAND_CURSOR)); // Pone la manita de click automáticamente
        
        // Apagamos los estilos rígidos por defecto de Windows y Java Swing
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    // Aquí es donde ocurre la magia: borramos el dibujo cuadrado de Java y pintamos curvas suaves
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Activa el suavizado de bordes (Antialiasing) para que las curvas no se vean pixeladas
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Pintamos el fondo con esquinas redondeadas
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radioEsquinas, radioEsquinas);
        
        // Dejamos que Java pinte el texto encima del fondo que ya dibujamos
        super.paintComponent(g);
        g2.dispose();
    }
}