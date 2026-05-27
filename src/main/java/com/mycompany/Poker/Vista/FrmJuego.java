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

    // Componentes visuales globales
    private JLabel lblPuntosDealer;
    private JLabel lblInfoApuesta;
    private JPanel panelCartasDealer;

    // Estructuras simétricas para la mesa dividida en 4 asientos multijugador
    private JPanel[] panelesAsientos = new JPanel[4];
    private JLabel[] lblNombresAsientos = new JLabel[4];
    private JPanel[] miniTapetesAsientos = new JPanel[4];
    private int asientosOcupados = 0;
    private JTextField txtMontoApuesta;
    private boolean apuestaEnviada = false;

    public FrmJuego(Usuario usuarioSesion, ClienteRed clienteRed) {
        this.usuarioSesion = usuarioSesion;
        this.clienteRed = clienteRed;

        // =========================================================================
        // 1. CONFIGURACIÓN DE LA VENTANA (Resolución HD)
        // =========================================================================
        setTitle("Mesa de Blackjack 21 - Phantom Thieves");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(15, 15, 15)); // Fondo negro mate P5

        // =========================================================================
        // 2. ZONA NORTE: ÁREA DEL DEALER (LA CASA)
        // =========================================================================
        JPanel panelDealer = new JPanel(new BorderLayout());
        panelDealer.setOpaque(false);
        panelDealer.setBorder(BorderFactory.createEmptyBorder(20, 50, 10, 50));

        lblPuntosDealer = new JLabel("DEALER (Puntos: 0)", JLabel.CENTER);
        lblPuntosDealer.setForeground(new Color(200, 200, 200));
        lblPuntosDealer.setFont(new Font("Arial", Font.BOLD, 16));
        panelDealer.add(lblPuntosDealer, BorderLayout.NORTH);

        // Tapete verde esmeralda para las cartas del Dealer
        panelCartasDealer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelCartasDealer.setBackground(new Color(10, 92, 44));
        panelCartasDealer.setPreferredSize(new Dimension(1180, 140));
        panelCartasDealer.setBorder(BorderFactory.createLineBorder(new Color(219, 20, 46), 2, true)); // Contorno rojo sutil
        panelDealer.add(panelCartasDealer, BorderLayout.CENTER);

        // =========================================================================
        // 3. ZONA CENTRAL: ÁREA MULTIJUGADOR (Dividida en 4 Asientos)
        // =========================================================================
        JPanel panelMesaMultijugador = new JPanel(new GridLayout(1, 4, 20, 0));
        panelMesaMultijugador.setOpaque(false);
        panelMesaMultijugador.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        // Inicializamos los 4 asientos vacíos de la mesa
        for (int i = 0; i < 4; i++) {
            panelesAsientos[i] = new JPanel(new BorderLayout());
            panelesAsientos[i].setOpaque(false);

            // Etiqueta del jugador en este asiento
            lblNombresAsientos[i] = new JLabel("[ Asiento Vacío ]", JLabel.CENTER);
            lblNombresAsientos[i].setForeground(new Color(100, 100, 100)); // Gris si está vacío
            lblNombresAsientos[i].setFont(new Font("Arial", Font.BOLD, 14));
            panelesAsientos[i].add(lblNombresAsientos[i], BorderLayout.NORTH);

            // Mini tapete verde esmeralda exclusivo de este asiento para sus cartas
            miniTapetesAsientos[i] = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            miniTapetesAsientos[i].setBackground(new Color(10, 80, 38)); // Verde tapete un poco más oscuro
            miniTapetesAsientos[i].setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50), 1, true)); // Borde gris apagado
            panelesAsientos[i].add(miniTapetesAsientos[i], BorderLayout.CENTER);

            // Lo metemos a la cuadrícula de la mesa
            panelMesaMultijugador.add(panelesAsientos[i]);
        }

        // --- TU ASIENTO AUTOMÁTICO ---
        // El jugador local (tú) siempre toma el primer asiento disponible (Asiento 0)
        lblNombresAsientos[0].setText(usuarioSesion.getNombre().toUpperCase() + " (Puntos: 0)");
        lblNombresAsientos[0].setForeground(Color.WHITE);
        miniTapetesAsientos[0].setBorder(BorderFactory.createLineBorder(new Color(219, 20, 46), 2, true)); // Borde rojo P5 para resaltar que eres tú
        asientosOcupados = 1; // Ya estás tú sentado

        // =========================================================================
        // 4. ZONA SUR: CONTROLES Y ACCIONES DE JUEGO
        // =========================================================================
        JPanel panelControles = new JPanel(new BorderLayout());
        panelControles.setBackground(new Color(25, 25, 25)); // Gris carbón oscuro para la barra inferior
        panelControles.setPreferredSize(new Dimension(1280, 100));
        panelControles.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));

        // Información de dinero a la izquierda de la barra
        JPanel panelInfoDinero = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelInfoDinero.setOpaque(false);

        lblInfoApuesta = new JLabel("Saldo: $" + usuarioSesion.getSaldo());
        lblInfoApuesta.setForeground(new Color(212, 175, 55)); // Dorado
        lblInfoApuesta.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblTxtApuesta = new JLabel("Apuesta: $");
        lblTxtApuesta.setForeground(Color.WHITE);
        lblTxtApuesta.setFont(new Font("Arial", Font.BOLD, 16));

        // Cuadro de texto para ingresar la apuesta libremente
        txtMontoApuesta = new JTextField("100", 5);
        txtMontoApuesta.setFont(new Font("Arial", Font.BOLD, 14));
        txtMontoApuesta.setBackground(new Color(40, 40, 40));
        txtMontoApuesta.setForeground(Color.WHITE);
        txtMontoApuesta.setCaretColor(Color.WHITE);

        panelInfoDinero.add(lblInfoApuesta);
        panelInfoDinero.add(lblTxtApuesta);
        panelInfoDinero.add(txtMontoApuesta);

        panelControles.add(panelInfoDinero, BorderLayout.WEST);
        // Panel contenedor de los botones (Centro-Derecha)
        JPanel panelBotonesAccion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        panelBotonesAccion.setOpaque(false);

        // Botón Pedir Carta (Hit) - Rojo Carmesí P5
        BotonCasino btnPedir = new BotonCasino("PEDIR CARTA", new Color(219, 20, 46), Color.WHITE, 15);
        btnPedir.setPreferredSize(new Dimension(160, 45));
        btnPedir.setFont(new Font("Arial", Font.BOLD, 14));

        // Botón Plantarse (Stand) - Negro puro con contorno blanco sutil
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

        // =========================================================================
        // 5. ENSAMBLAR TODO EN EL JFRAME
        // =========================================================================
        add(panelDealer, BorderLayout.NORTH);
        add(panelMesaMultijugador, BorderLayout.CENTER);
        add(panelControles, BorderLayout.SOUTH);

        // =========================================================================
        // 6. MANEJO DE EVENTOS REALES DE RED
        // =========================================================================
        // PEDIR CARTA (Hit)
       btnPedir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // CASO A: Primer clic de la ronda. Solo confirmamos la apuesta.
                if (!apuestaEnviada) {
                    try {
                        int monto = Integer.parseInt(txtMontoApuesta.getText().trim());
                        if (monto <= 0 || monto > usuarioSesion.getSaldo()) {
                            JOptionPane.showMessageDialog(FrmJuego.this, 
                                "Monto de apuesta inválido o fondos insuficientes.", 
                                "Error de Casino", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Enviamos la apuesta al servidor para que nos despache las 2 de default
                        FrmJuego.this.clienteRed.enviarComandoJuego("APUESTA:" + monto);
                        System.out.println("[Red] Apuesta inicial procesada: $" + monto);
                        
                        txtMontoApuesta.setEnabled(false); 
                        apuestaEnviada = true; // Activamos el candado
                        
                        // 🟥 LA CLAVE: Usamos un return para detener el flujo aquí. 
                        // No mandamos el comando "PEDIR_CARTA" todavía para no duplicar tiros.
                        return; 
                        
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(FrmJuego.this, 
                            "Por favor ingresa un número válido.", 
                            "Error de Formato", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                // CASO B: A partir del segundo clic de la ronda, ya funciona como un "Pedir" normal de carta extra
                System.out.println("[Red] Solicitando carta extra a la mesa...");
                FrmJuego.this.clienteRed.enviarComandoJuego("PEDIR_CARTA");
            }
        });

        btnPlantarse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. Le avisamos al servidor que nos plantamos
                FrmJuego.this.clienteRed.enviarComandoJuego("PLANTARSE");

                // 2. Apagamos tus botones temporalmente para esperar el turno de la casa
                btnPedir.setEnabled(false);
                btnPlantarse.setEnabled(false);
            }
        });

        // =========================================================================
        // 7. CONEXIÓN PERMANENTE MULTIJUGADOR 
        // =========================================================================
        if (this.clienteRed.conectarMesa(usuarioSesion.getNombre())) {

            // Hilo secundario de escucha continua por el Socket permanente de la mesa
            Thread hiloEscucha = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Recuperamos el ObjectInputStream estable mediante tu método getEntrada()
                        java.io.ObjectInputStream entradaJuego = FrmJuego.this.clienteRed.getEntrada();

                        while (true) {
                            // Escuchamos de forma indefinida las respuestas de la mesa del Servidor
                            String mensaje = (String) entradaJuego.readObject();

                            // ACCIÓN: Un nuevo contrincante entró a la partida
                            if (mensaje.startsWith("NUEVO_JUGADOR:")) {
                                String nombreOtro = mensaje.split(":")[1];

                                SwingUtilities.invokeLater(() -> {
                                    if (asientosOcupados < 4) {
                                        int asientoAsignado = asientosOcupados;

                                        // Activamos visualmente el siguiente asiento para el rival
                                        lblNombresAsientos[asientoAsignado].setText(nombreOtro.toUpperCase() + " (Puntos: 0)");
                                        lblNombresAsientos[asientoAsignado].setForeground(new Color(212, 175, 55)); // Letras doradas
                                        miniTapetesAsientos[asientoAsignado].setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 1, true));

                                        asientosOcupados++;

                                        JOptionPane.showMessageDialog(FrmJuego.this, "¡" + nombreOtro + " se ha sentado en el Asiento " + (asientoAsignado + 1) + "!");
                                    }
                                });
                            } // ACCIÓN: El servidor repartió una carta a alguien de la mesa
                            else if (mensaje.startsWith("CARTA_REPARTIDA:")) {
                                String[] datos = mensaje.split(":"); // (Tu variable mensaje)
                                String jugadorQueRobo = datos[1];
                                String palo = datos[2];
                                String valorCarta = datos[3];
                                String puntosTotales = datos[4];

                                SwingUtilities.invokeLater(() -> {
                                    for (int i = 0; i < 4; i++) {
                                        if (lblNombresAsientos[i].getText().startsWith(jugadorQueRobo.toUpperCase())) {

                                            // 🟥 INSTANCIAMOS TU NUEVO COMPONENTE VECTORIAL PREMIUM 🟥
                                            ComponenteCarta cartaVisual = new ComponenteCarta(valorCarta, palo);

                                            // La añadimos directamente al tapete de su columna
                                            miniTapetesAsientos[i].add(cartaVisual);
                                            lblNombresAsientos[i].setText(jugadorQueRobo.toUpperCase() + " (Puntos: " + puntosTotales + ")");

                                            miniTapetesAsientos[i].revalidate();
                                            miniTapetesAsientos[i].repaint();
                                            break;
                                        }
                                    }
                                });
                            }// =========================================================================
                            // ACCIÓN C: UN RIVAL SE PLANTÓ (ESTA ES LA LÓGICA NUEVA)
                            // =========================================================================
                            else if (mensaje.startsWith("JUGADOR_PLANTADO:")) {
                                String[] datos = mensaje.split(":");
                                String jugadorPlantado = datos[1];
                                String puntosFinales = datos[2];

                                SwingUtilities.invokeLater(() -> {
                                    for (int i = 0; i < 4; i++) {
                                        if (lblNombresAsientos[i].getText().startsWith(jugadorPlantado.toUpperCase())) {
                                            // Pintamos su cabecera en verde brillante indicando congelado
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
                                    // 1. Si el que se pasó eres tú mismo, te apagamos los controles
                                    if (jugadorBust.equalsIgnoreCase(usuarioSesion.getNombre())) {
                                        btnPedir.setEnabled(false);
                                        btnPlantarse.setEnabled(false);
                                        JOptionPane.showMessageDialog(FrmJuego.this, "¡Te has pasado de 21! Has perdido esta ronda.", "¡BUST!", JOptionPane.ERROR_MESSAGE);
                                    }

                                    // 2. Buscamos el asiento en la cuadrícula para actualizar el diseño visual
                                    for (int i = 0; i < 4; i++) {
                                        if (lblNombresAsientos[i].getText().startsWith(jugadorBust.toUpperCase())) {
                                            lblNombresAsientos[i].setText(jugadorBust.toUpperCase() + " (BUST: " + puntosFinales + ")");
                                            lblNombresAsientos[i].setForeground(new Color(231, 76, 60)); // Rojo carmesí sutil de eliminado
                                            miniTapetesAsientos[i].setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 2, true));
                                            break;
                                        }
                                    }
                                });
                            } // ACCIÓN E: EL DEALER ROBO UNA CARTA AUTOMÁTICA
                            else if (mensaje.startsWith("DEALER_CARTA:")) {

                                String[] datos = mensaje.split(":");
                                String cartaTexto = datos[1]; // Viene en formato "Valor de PAL" (ej: "A de PICAS")
                                String puntosDealer = datos[2];

                                SwingUtilities.invokeLater(() -> {
                                    lblPuntosDealer.setText("DEALER (Puntos: " + puntosDealer + ")");

                                    // Parseamos el texto para separar el valor y el palo
                                    // Como el servidor manda "DEALER_CARTA:Valor de PAL", lo extraemos limpiamente:
                                    String valor = cartaTexto.split(" ")[0];

                                    // Mapeamos el substring de red al nombre del palo completo para el componente
                                    String paloCompleto = "PICAS";
                                    if (cartaTexto.contains("COR")) {
                                        paloCompleto = "CORAZONES";
                                    } else if (cartaTexto.contains("DIA")) {
                                        paloCompleto = "DIAMANTES";
                                    } else if (cartaTexto.contains("TRE")) {
                                        paloCompleto = "TREBOLES";
                                    }

                                    // 🟥 INSTANCIAMOS EL COMPONENTE PARA LA CASA 🟥
                                    ComponenteCarta cartaDealerVisual = new ComponenteCarta(valor, paloCompleto);

                                    panelCartasDealer.add(cartaDealerVisual);
                                    panelCartasDealer.revalidate();
                                    panelCartasDealer.repaint();
                                });
                            }else if (mensaje.equals("DEALER_REVELAR")) {
                                SwingUtilities.invokeLater(() -> {
                                    // Vaciamos el tapete del Dealer (borra el "?" rojo)
                                    panelCartasDealer.removeAll();
                                    panelCartasDealer.revalidate();
                                    panelCartasDealer.repaint();
                                });
                            } 
                            
                            else if (mensaje.equals("DEALER_CARTA_OCULTA")) {
                                SwingUtilities.invokeLater(() -> {
                                    // Instanciamos el componente usando el constructor vacío (Boca abajo)
                                    ComponenteCarta cartaOculta = new ComponenteCarta();

                                    panelCartasDealer.add(cartaOculta);
                                    panelCartasDealer.revalidate();
                                    panelCartasDealer.repaint();
                                });
                            } // ACCIÓN F: SE DEFINIERON LOS RESULTADOS
                            else if (mensaje.startsWith("MESA_RESULTADO:")) {
                                String[] datos = mensaje.split(":");
                                int puntosDealer = Integer.parseInt(datos[1]);
                                String veredicto = datos[2];
                                double saldoActualizado = Double.parseDouble(datos[3]);

                                SwingUtilities.invokeLater(() -> {
                                    // Actualizamos inmediatamente tu objeto local de sesión y tu letrero de saldo
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
                                    // 1. Limpiamos las cartas visuales del Dealer y su marcador
                                    panelCartasDealer.removeAll();
                                    lblPuntosDealer.setText("DEALER (Puntos: 0)");
                                    panelCartasDealer.revalidate();
                                    panelCartasDealer.repaint();

                                    // 2. Limpiamos los mini-tapetes de los 4 asientos y restauramos sus cabeceras
                                    for (int i = 0; i < 4; i++) {
                                        // Guardamos el nombre original del jugador para no perderlo al limpiar
                                        String textoActual = lblNombresAsientos[i].getText();

                                        if (!textoActual.equals("[ Asiento Vacío ]")) {
                                            // Extraemos solo el nombre limpio antes del paréntesis
                                            String nombreLimpio = textoActual.split(" ")[0];

                                            // Restauramos el texto base
                                            lblNombresAsientos[i].setText(nombreLimpio + " (Puntos: 0)");

                                            // Devolvemos el color original según si eres tú o un rival
                                            if (i == 0) {
                                                lblNombresAsientos[i].setForeground(Color.WHITE);
                                                miniTapetesAsientos[i].setBorder(BorderFactory.createLineBorder(new Color(219, 20, 46), 2, true));
                                            } else {
                                                lblNombresAsientos[i].setForeground(new Color(212, 175, 55));
                                                miniTapetesAsientos[i].setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 1, true));
                                            }
                                        }

                                        // Vaciamos las cartas físicas de este tapete en particular
                                        miniTapetesAsientos[i].removeAll();
                                        miniTapetesAsientos[i].revalidate();
                                        miniTapetesAsientos[i].repaint();
                                    }

                                    // 3. ¡EL REGRESO A LA ACCIÓN!: Reactivamos tus botones locales
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
