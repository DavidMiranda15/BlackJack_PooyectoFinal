package com.mycompany.Poker.Vista.disenos;

import javax.swing.*;
import java.awt.*;

public class BotonCasino extends JButton {

    private int radioEsquinas;

    public BotonCasino(String texto, Color fondo, Color textoColor, int radioEsquinas) {
        super(texto);
        this.radioEsquinas = radioEsquinas;

        setFont(new Font("Arial", Font.BOLD, 14));
        setForeground(textoColor);
        setBackground(fondo);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radioEsquinas, radioEsquinas);

        super.paintComponent(g);
        g2.dispose();
    }
}
