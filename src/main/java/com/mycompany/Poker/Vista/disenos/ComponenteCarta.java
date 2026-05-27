package com.mycompany.Poker.Vista.disenos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ComponenteCarta extends JPanel {

    public ComponenteCarta(String valor, String palo) {
        // Redimensionamos el panel al tamaño de una carta real a escala (80x115 px)
        setPreferredSize(new Dimension(80, 115));
        setMaximumSize(new Dimension(80, 115));
        setMinimumSize(new Dimension(80, 115));
        
        // Estilo de la tarjeta física
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        
        // Borde redondeado sutil y sombra con los bordes de Swing
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(5, 8, 5, 8)
        ));

        // Determinamos el símbolo Unicode y el color según el palo original
        String simboloPalo = "";
        Color colorPalo = Color.BLACK;

        switch (palo.toUpperCase()) {
            case "CORAZONES":
                simboloPalo = "♥";
                colorPalo = new Color(219, 20, 46); // Rojo carmesí
                break;
            case "DIAMANTES":
                simboloPalo = "♦";
                colorPalo = new Color(219, 20, 46);
                break;
            case "TREBOLES":
                simboloPalo = "♣";
                colorPalo = new Color(20, 20, 20); // Negro puro
                break;
            case "PICAS":
                simboloPalo = "♠";
                colorPalo = new Color(20, 20, 20);
                break;
            default:
                simboloPalo = "?";
                break;
        }

        // 1. Índice superior izquierdo (Valor de la carta, ej: "A", "K", "10")
        JLabel lblIndice = new JLabel(valor);
        lblIndice.setFont(new Font("Arial", Font.BOLD, 16));
        lblIndice.setForeground(colorPalo);
        lblIndice.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblIndice, BorderLayout.NORTH);

        // 2. Símbolo gigante en el centro (♥, ♦, ♣, ♠)
        JLabel lblCentro = new JLabel(simboloPalo, JLabel.CENTER);
        lblCentro.setFont(new Font("Arial", Font.PLAIN, 36));
        lblCentro.setForeground(colorPalo);
        add(lblCentro, BorderLayout.CENTER);
    }

    // Dibujamos esquinas redondeadas en el JPanel para que parezca naipe real
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.dispose();
    }
    
    public ComponenteCarta() {
        setPreferredSize(new Dimension(80, 115));
        setMaximumSize(new Dimension(80, 115));
        setMinimumSize(new Dimension(80, 115));
        
        // El reverso de la carta estilo Casino: Fondo rojo o azul con un diseño sencillo
        setBackground(new Color(219, 20, 46)); // Rojo Casino
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));

        JLabel lblReverso = new JLabel("?", JLabel.CENTER);
        lblReverso.setFont(new Font("Arial", Font.BOLD, 32));
        lblReverso.setForeground(Color.WHITE);
        add(lblReverso, BorderLayout.CENTER);
    }
}