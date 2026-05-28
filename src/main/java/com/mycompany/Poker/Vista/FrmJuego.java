package com.mycompany.Poker.Vista;

import com.mycompany.Poker.Entidad.Usuario;
import com.mycompany.Poker.Vista.disenos.BotonCasino;
import com.mycompany.Poker.Vista.disenos.ComponenteCarta;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrmJuego extends JFrame {

    private Usuario usuarioSesion;
    private ClienteRed clienteRed;

    private JLabel lblPuntosDealer;
    private JLabel lblInfoApuesta;
    private JPanel panelCartasDealer;

    private JPanel[] panelesAsientos = new JPanel[4];
    private JLabel[] lblNombresAsientos = new JLabel[4];
    private JPanel[] miniTapetesAsientos = new JPanel[4];
    private int asientosOcupados = 0;
    private JTextField txtMontoApuesta;
    private boolean apuestaEnviada = false;

    public FrmJuego(Usuario usuarioSesion, ClienteRed clienteRed) {
        this.usuarioSesion = usuarioSesion;
        this.clienteRed = clienteRed;

        setTitle("Mesa de Blackjack 21 - Phantom Thieves");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(15, 15, 15));

        JPanel panelDealer = new JPanel(new BorderLayout());
        panelDealer.setOpaque(false);
        panelDealer.setBorder(BorderFactory.createEmptyBorder(20, 50, 10, 50));

        lblPuntosDealer = new JLabel("DEALER (Puntos: 0)", JLabel.CENTER);
        lblPuntosDealer.setForeground(new Color(200, 200, 200));
        lblPuntosDealer.setFont(new Font("Arial", Font.BOLD, 16));
        panelDealer.add(lblPuntosDealer, BorderLayout.NORTH);

        panelCartasDealer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelCartasDealer.setBackground(new Color(10, 92, 44));
        panelCartasDealer.setPreferredSize(new Dimension(1180, 140));
        panelCartasDealer.setBorder(BorderFactory.createLineBorder(new Color(219, 20, 46), 2, true)); // Contorno rojo sutil
        panelDealer.add(panelCartasDealer, BorderLayout.CENTER);

        JPanel panelMesaMultijugador = new JPanel(new GridLayout(1, 4, 20, 0));
        panelMesaMultijugador.setOpaque(false);
        panelMesaMultijugador.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        for (int i = 0; i < 4; i++) {
            panelesAsientos[i] = new JPanel(new BorderLayout());
            panelesAsientos[i].setOpaque(false);

            lblNombresAsientos[i] = new JLabel("[ Asiento Vacío ]", JLabel.CENTER);
            lblNombresAsientos[i].setForeground(new Color(100, 100, 100));
            lblNombresAsientos[i].setFont(new Font("Arial", Font.BOLD, 14));
            panelesAsientos[i].add(lblNombresAsientos[i], BorderLayout.NORTH);

            miniTapetesAsientos[i] = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            miniTapetesAsientos[i].setBackground(new Color(10, 80, 38));
            miniTapetesAsientos[i].setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50), 1, true));
            panelesAsientos[i].add(miniTapetesAsientos[i], BorderLayout.CENTER);

            panelMesaMultijugador.add(panelesAsientos[i]);
        }

        lblNombresAsientos[0].setText(usuarioSesion.getNombre().toUpperCase() + " (Puntos: 0)");
        lblNombresAsientos[0].setForeground(Color.WHITE);
        miniTapetesAsientos[0].setBorder(BorderFactory.createLineBorder(new Color(219, 20, 46), 2, true));
        asientosOcupados = 1;

        JPanel panelControles = new JPanel(new BorderLayout());
        panelControles.setBackground(new Color(25, 25, 25));
        panelControles.setPreferredSize(new Dimension(1280, 100));
        panelControles.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));

        JPanel panelInfoDinero = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelInfoDinero.setOpaque(false);

        lblInfoApuesta = new JLabel("Saldo: $" + usuarioSesion.getSaldo());
        lblInfoApuesta.setForeground(new Color(212, 175, 55));
        lblInfoApuesta.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblTxtApuesta = new JLabel("Apuesta: $");
        lblTxtApuesta.setForeground(Color.WHITE);
        lblTxtApuesta.setFont(new Font("Arial", Font.BOLD, 16));

        txtMontoApuesta = new JTextField("100", 5);
        txtMontoApuesta.setFont(new Font("Arial", Font.BOLD, 14));
        txtMontoApuesta.setBackground(new Color(40, 40, 40));
        txtMontoApuesta.setForeground(Color.WHITE);
        txtMontoApuesta.setCaretColor(Color.WHITE);

        panelInfoDinero.add(lblInfoApuesta);
        panelInfoDinero.add(lblTxtApuesta);
        panelInfoDinero.add(txtMontoApuesta);

        panelControles.add(panelInfoDinero, BorderLayout.WEST);
        JPanel panelBotonesAccion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        panelBotonesAccion.setOpaque(false);

        BotonCasino btnPedir = new BotonCasino("PEDIR CARTA", new Color(219, 20, 46), Color.WHITE, 15);
        btnPedir.setPreferredSize(new Dimension(160, 45));
        btnPedir.setFont(new Font("Arial", Font.BOLD, 14));

        BotonCasino btnPlantarse = new BotonCasino("PLANTARSE", new Color(10, 10, 10), Color.WHITE, 15);
        btnPlantarse.setPreferredSize(new Dimension(160, 45));
        btnPlantarse.setFont(new Font("Arial", Font.BOLD, 14));
        btnPlantarse.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        panelBotonesAccion.add(btnPedir);
        panelBotonesAccion.add(btnPlantarse);
        panelControles.add(panelBotonesAccion, BorderLayout.EAST);

        add(panelDealer, BorderLayout.NORTH);
        add(panelMesaMultijugador, BorderLayout.CENTER);
        add(panelControles, BorderLayout.SOUTH);

        btnPedir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!apuestaEnviada) {
                    try {
                        int monto = Integer.parseInt(txtMontoApuesta.getText().trim());
                        if (monto <= 0 || monto > usuarioSesion.getSaldo()) {
                            JOptionPane.showMessageDialog(FrmJuego.this,
                                    "Monto de apuesta inválido o fondos insuficientes.",
                                    "Error de Casino", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        FrmJuego.this.clienteRed.enviarComandoJuego("APUESTA:" + monto);
                        System.out.println("[Red] Apuesta inicial procesada: $" + monto);

                        txtMontoApuesta.setEnabled(false);
                        apuestaEnviada = true;

                        return;

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(FrmJuego.this,
                                "Por favor ingresa un número válido.",
                                "Error de Formato", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                System.out.println("[Red] Solicitando carta extra a la mesa...");
                FrmJuego.this.clienteRed.enviarComandoJuego("PEDIR_CARTA");
            }
        });

        btnPlantarse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FrmJuego.this.clienteRed.enviarComandoJuego("PLANTARSE");

                btnPedir.setEnabled(false);
                btnPlantarse.setEnabled(false);
            }
        });

        if (this.clienteRed.conectarMesa(usuarioSesion.getNombre())) {

            Thread hiloEscucha = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        java.io.ObjectInputStream entradaJuego = FrmJuego.this.clienteRed.getEntrada();

                        while (true) {
                            String mensaje = (String) entradaJuego.readObject();

                            if (mensaje.startsWith("NUEVO_JUGADOR:")) {
                                String nombreOtro = mensaje.split(":")[1];

                                SwingUtilities.invokeLater(() -> {
                                    if (asientosOcupados < 4) {
                                        int asientoAsignado = asientosOcupados;

                                        lblNombresAsientos[asientoAsignado].setText(nombreOtro.toUpperCase() + " (Puntos: 0)");
                                        lblNombresAsientos[asientoAsignado].setForeground(new Color(212, 175, 55));
                                        miniTapetesAsientos[asientoAsignado].setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 1, true));

                                        asientosOcupados++;

                                        JOptionPane.showMessageDialog(FrmJuego.this, "¡" + nombreOtro + " se ha sentado en el Asiento " + (asientoAsignado + 1) + "!");
                                    }
                                });
                            } else if (mensaje.startsWith("CARTA_REPARTIDA:")) {
                                String[] datos = mensaje.split(":");
                                String jugadorQueRobo = datos[1];
                                String palo = datos[2];
                                String valorCarta = datos[3];
                                String puntosTotales = datos[4];

                                SwingUtilities.invokeLater(() -> {
                                    for (int i = 0; i < 4; i++) {
                                        if (lblNombresAsientos[i].getText().startsWith(jugadorQueRobo.toUpperCase())) {

                                            ComponenteCarta cartaVisual = new ComponenteCarta(valorCarta, palo);

                                            miniTapetesAsientos[i].add(cartaVisual);
                                            lblNombresAsientos[i].setText(jugadorQueRobo.toUpperCase() + " (Puntos: " + puntosTotales + ")");

                                            miniTapetesAsientos[i].revalidate();
                                            miniTapetesAsientos[i].repaint();
                                            break;
                                        }
                                    }
                                });
                            } else if (mensaje.startsWith("JUGADOR_PLANTADO:")) {
                                String[] datos = mensaje.split(":");
                                String jugadorPlantado = datos[1];
                                String puntosFinales = datos[2];

                                SwingUtilities.invokeLater(() -> {
                                    for (int i = 0; i < 4; i++) {
                                        if (lblNombresAsientos[i].getText().startsWith(jugadorPlantado.toUpperCase())) {

                                            lblNombresAsientos[i].setText(jugadorPlantado.toUpperCase() + " (PLANTADO: " + puntosFinales + ")");
                                            lblNombresAsientos[i].setForeground(new Color(46, 204, 113));
                                            break;
                                        }
                                    }
                                });
                            } else if (mensaje.startsWith("JUGADOR_BUST:")) {
                                String[] datos = mensaje.split(":");
                                String jugadorBust = datos[1];
                                String puntosFinales = datos[2];

                                SwingUtilities.invokeLater(() -> {
                                    if (jugadorBust.equalsIgnoreCase(usuarioSesion.getNombre())) {
                                        btnPedir.setEnabled(false);
                                        btnPlantarse.setEnabled(false);
                                        JOptionPane.showMessageDialog(FrmJuego.this, "¡Te has pasado de 21! Has perdido esta ronda.", "¡BUST!", JOptionPane.ERROR_MESSAGE);
                                    }

                                    for (int i = 0; i < 4; i++) {
                                        if (lblNombresAsientos[i].getText().startsWith(jugadorBust.toUpperCase())) {
                                            lblNombresAsientos[i].setText(jugadorBust.toUpperCase() + " (BUST: " + puntosFinales + ")");
                                            lblNombresAsientos[i].setForeground(new Color(231, 76, 60));
                                            miniTapetesAsientos[i].setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 2, true));
                                            break;
                                        }
                                    }
                                });
                            } else if (mensaje.startsWith("DEALER_CARTA:")) {

                                String[] datos = mensaje.split(":");
                                String cartaTexto = datos[1];
                                String puntosDealer = datos[2];

                                SwingUtilities.invokeLater(() -> {
                                    lblPuntosDealer.setText("DEALER (Puntos: " + puntosDealer + ")");

                                    String valor = cartaTexto.split(" ")[0];

                                    String paloCompleto = "PICAS";
                                    if (cartaTexto.contains("COR")) {
                                        paloCompleto = "CORAZONES";
                                    } else if (cartaTexto.contains("DIA")) {
                                        paloCompleto = "DIAMANTES";
                                    } else if (cartaTexto.contains("TRE")) {
                                        paloCompleto = "TREBOLES";
                                    }

                                    ComponenteCarta cartaDealerVisual = new ComponenteCarta(valor, paloCompleto);

                                    panelCartasDealer.add(cartaDealerVisual);
                                    panelCartasDealer.revalidate();
                                    panelCartasDealer.repaint();
                                });
                            } else if (mensaje.equals("DEALER_REVELAR")) {
                                SwingUtilities.invokeLater(() -> {
                                    panelCartasDealer.removeAll();
                                    panelCartasDealer.revalidate();
                                    panelCartasDealer.repaint();
                                });
                            } else if (mensaje.equals("DEALER_CARTA_OCULTA")) {
                                SwingUtilities.invokeLater(() -> {
                                    ComponenteCarta cartaOculta = new ComponenteCarta();

                                    panelCartasDealer.add(cartaOculta);
                                    panelCartasDealer.revalidate();
                                    panelCartasDealer.repaint();
                                });
                            } else if (mensaje.startsWith("MESA_RESULTADO:")) {
                                String[] datos = mensaje.split(":");
                                int puntosDealer = Integer.parseInt(datos[1]);
                                String veredicto = datos[2];
                                double saldoActualizado = Double.parseDouble(datos[3]);

                                SwingUtilities.invokeLater(() -> {
                                    usuarioSesion.setSaldo(saldoActualizado);
                                    lblInfoApuesta.setText("Saldo: $" + saldoActualizado);

                                    String mensajeAlerta = "";
                                    switch (veredicto) {
                                        case "GANASTE":
                                            mensajeAlerta = "¡Le ganaste a la casa! ¡FELICIDADES!";
                                            break;
                                        case "PERDISTE":
                                            mensajeAlerta = "El Dealer gana esta ronda. Suerte para la próxima.";
                                            break;
                                        default:
                                            mensajeAlerta = "Empate con la casa. Recuperas tus fondos.";
                                            break;
                                    }

                                    JOptionPane.showMessageDialog(FrmJuego.this,
                                            "El Dealer terminó con " + puntosDealer + " puntos.\n\n" + mensajeAlerta,
                                            "Fin de la Ronda", JOptionPane.INFORMATION_MESSAGE);
                                });
                            } else if (mensaje.equals("MESA_REINICIAR")) {
                                SwingUtilities.invokeLater(() -> {
                                    panelCartasDealer.removeAll();
                                    lblPuntosDealer.setText("DEALER (Puntos: 0)");
                                    panelCartasDealer.revalidate();
                                    panelCartasDealer.repaint();

                                    for (int i = 0; i < 4; i++) {
                                        String textoActual = lblNombresAsientos[i].getText();

                                        if (!textoActual.equals("[ Asiento Vacío ]")) {
                                            String nombreLimpio = textoActual.split(" ")[0];

                                            lblNombresAsientos[i].setText(nombreLimpio + " (Puntos: 0)");

                                            if (i == 0) {
                                                lblNombresAsientos[i].setForeground(Color.WHITE);
                                                miniTapetesAsientos[i].setBorder(BorderFactory.createLineBorder(new Color(219, 20, 46), 2, true));
                                            } else {
                                                lblNombresAsientos[i].setForeground(new Color(212, 175, 55));
                                                miniTapetesAsientos[i].setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 1, true));
                                            }
                                        }

                                        miniTapetesAsientos[i].removeAll();
                                        miniTapetesAsientos[i].revalidate();
                                        miniTapetesAsientos[i].repaint();
                                    }

                                    btnPedir.setEnabled(true);
                                    btnPlantarse.setEnabled(true);
                                    txtMontoApuesta.setEnabled(true);
                                    apuestaEnviada = false;

                                });
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println("Conexión de la mesa de juego finalizada o perdida.");
                    }
                }
            });
            hiloEscucha.start();
        }
    }
}
