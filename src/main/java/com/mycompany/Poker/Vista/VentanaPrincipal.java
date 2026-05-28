package com.mycompany.Poker.Vista;

import com.mycompany.Poker.Entidad.Usuario;
import com.mycompany.Poker.Vista.disenos.BotonCasino;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaPrincipal extends JFrame {

    private ClienteRed clienteRed;
    private Usuario usuarioSesion = null;
    private JLabel lblLogo;

    public VentanaPrincipal() {
        setTitle("Casino Royal - Blackjack 21 Multijugador");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        clienteRed = new ClienteRed();

        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(18, 18, 18));
        panelHeader.setPreferredSize(new Dimension(1280, 90));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        lblLogo = new JLabel("♣ The Phantom Thieves ♦");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Arial", Font.BOLD, 24));
        panelHeader.add(lblLogo, BorderLayout.WEST);

        JPanel panelAuthBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        panelAuthBotones.setOpaque(false);

        BotonCasino btnRegistro = new BotonCasino(
                "Regístrate",
                new Color(219, 20, 46),
                Color.WHITE,
                15
        );
        btnRegistro.setPreferredSize(new Dimension(130, 38));

        BotonCasino btnAcceder = new BotonCasino(
                "Acceder",
                new Color(10, 10, 10),
                Color.WHITE,
                15
        );

        btnAcceder.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        btnAcceder.setPreferredSize(new Dimension(110, 38));

        panelAuthBotones.add(btnRegistro);
        panelAuthBotones.add(btnAcceder);
        panelHeader.add(panelAuthBotones, BorderLayout.EAST);

        String rutaImagen = "src/main/java/com/mycompany/Poker/Vista/imagenes/pantalla carga.png";
        java.io.File archivoImg = new java.io.File(rutaImagen);
        JLabel panelHero;

        if (archivoImg.exists()) {
            ImageIcon iconoOriginal = new ImageIcon(archivoImg.getAbsolutePath());
            Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(1280, 640, Image.SCALE_SMOOTH);
            panelHero = new JLabel(new ImageIcon(imagenEscalada));
            System.out.println("¡Imagen escalada y cargada con éxito!");
        } else {
            panelHero = new JLabel();
            panelHero.setBackground(new Color(28, 28, 28));
            panelHero.setOpaque(true);
        }

        panelHero.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(500, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        BotonCasino btnJugarAhora = new BotonCasino(
                "¡JUGAR AHORA!",
                new Color(10, 10, 10),
                Color.WHITE,
                20
        );
        btnJugarAhora.setFont(new Font("Arial", Font.BOLD, 18));
        btnJugarAhora.setPreferredSize(new Dimension(220, 50));

        panelHero.add(btnJugarAhora, gbc);

        add(panelHeader, BorderLayout.NORTH);
        add(panelHero, BorderLayout.CENTER);

        btnAcceder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DialogoLogin login = new DialogoLogin(VentanaPrincipal.this, clienteRed);
                login.setVisible(true);

                Usuario user = login.getUsuarioLogueado();

                if (user != null) {
                    usuarioSesion = user;

                    lblLogo.setText("♣ Sesión Activa: " + usuarioSesion.getNombre() + " ($" + usuarioSesion.getSaldo() + ") ♦");

                    btnAcceder.setEnabled(false);
                    btnRegistro.setEnabled(false);

                    panelHeader.revalidate();
                    panelHeader.repaint();

                    JOptionPane.showMessageDialog(VentanaPrincipal.this,
                            "¡Bienvenido de vuelta, " + usuarioSesion.getNombre() + "! El botón JUGAR AHORA está listo.",
                            "Autenticación Correcta", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        btnRegistro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DialogoRegistro registro = new DialogoRegistro(VentanaPrincipal.this, clienteRed);
                registro.setVisible(true);
            }
        });

        btnJugarAhora.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usuarioSesion == null) {
                    JOptionPane.showMessageDialog(VentanaPrincipal.this,
                            "No has iniciado sesión. Por favor, accede con tu cuenta para jugar.",
                            "Acceso Bloqueado", JOptionPane.WARNING_MESSAGE);
                } else {
                    new FrmJuego(usuarioSesion, clienteRed).setVisible(true);
                    dispose();
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}
