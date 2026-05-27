package com.mycompany.Poker.Vista;

import com.mycompany.Poker.Entidad.Usuario;
import com.mycompany.Poker.Vista.disenos.BotonCasino;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaPrincipal extends JFrame {

    private ClienteRed clienteRed;
    private Usuario usuarioSesion = null; // Guardará al usuario cuando haga Login exitoso
    private JLabel lblLogo;

    public VentanaPrincipal() {
        // 1. Configuración del JFrame Principal (Resolución HD)
        setTitle("Casino Royal - Blackjack 21 Multijugador");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setLayout(new BorderLayout());

        clienteRed = new ClienteRed();


        // 2. CREACIÓN DEL HEADER (Barra Superior Estilizada y Forzada)
        // =========================================================================
        // 2. CREACIÓN DEL HEADER (Barra Superior Tipo Casino Premium)
        // =========================================================================
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(18, 18, 18)); // Negro grafito mate integrado con el marco
        panelHeader.setPreferredSize(new Dimension(1280, 90));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        // Logo del Casino - Dorado elegante refinado (Combina con la corona del fondo)
        lblLogo = new JLabel("♣ The Phantom Thieves ♦");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Arial", Font.BOLD, 24));
        panelHeader.add(lblLogo, BorderLayout.WEST);

        // Panel de Botones de Autenticación
        JPanel panelAuthBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        panelAuthBotones.setOpaque(false);

        // REEMPLAZO 1: Cambiamos el JButton viejo por tu nueva clase redondeada 'BotonCasino'
        BotonCasino btnRegistro = new BotonCasino(
                "Regístrate",
                new Color(219, 20, 46), // Rojo Fuego P5
                Color.WHITE, // Texto Blanco
                15
        );
        btnRegistro.setPreferredSize(new Dimension(130, 38));

        // Acceder: Negro mate puro con letras blancas (Estilo minimalista de los menús de Atlus)
        BotonCasino btnAcceder = new BotonCasino(
                "Acceder",
                new Color(10, 10, 10), // Negro Puro
                Color.WHITE, // Texto Blanco
                15
        );

        // Le pintamos un contorno dorado delgado hecho a mano que respete la curva
        btnAcceder.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        btnAcceder.setPreferredSize(new Dimension(110, 38));

        panelAuthBotones.add(btnRegistro);
        panelAuthBotones.add(btnAcceder);
        panelHeader.add(panelAuthBotones, BorderLayout.EAST);

        // =========================================================================
        // 3. CREACIÓN DEL HERO BANNER CON TU IMAGEN DE FONDO
        // =========================================================================
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
        gbc.insets = new Insets(500, 10, 10, 10); // Margen de 500 para aventarlo hasta abajo
        gbc.anchor = GridBagConstraints.CENTER;

        // REEMPLAZO 3: Aquí está el truco del botón de abajo. 
        // Cambiamos 'new JButton' por tu nueva clase 'BotonCasino' para que use el motor redondeado
        BotonCasino btnJugarAhora = new BotonCasino(
                "¡JUGAR AHORA!",
                new Color(10, 10, 10), // Fondo Negro Absoluto
                Color.WHITE, // Texto Blanco de alto impacto
                20 // Esquinas más redondeadas
        );
        btnJugarAhora.setFont(new Font("Arial", Font.BOLD, 18));
        btnJugarAhora.setPreferredSize(new Dimension(220, 50));

        panelHero.add(btnJugarAhora, gbc);

        // 4. AGREGAR PANELES AL JFRAME
        add(panelHeader, BorderLayout.NORTH);
        add(panelHero, BorderLayout.CENTER);

        // 5. MANEJO DE EVENTOS
        btnAcceder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DialogoLogin login = new DialogoLogin(VentanaPrincipal.this, clienteRed);
                login.setVisible(true); // Se pausa aquí hasta que se cierre el modal

                // Recuperamos el usuario que inició sesión
                Usuario user = login.getUsuarioLogueado();
                
                if (user != null) {
                    usuarioSesion = user; // Guardamos la sesión en el frame
                    
                    // 1. Cambiamos el texto del logo de arriba con los datos reales
                    lblLogo.setText("♣ Sesión Activa: " + usuarioSesion.getNombre() + " ($" + usuarioSesion.getSaldo() + ") ♦");
                    
                    // 2. Deshabilitamos los botones para que no intenten loguearse doble
                    btnAcceder.setEnabled(false);
                    btnRegistro.setEnabled(false);
                    
                    // 3. TRUCO MÁGICO: Obligamos a Java a redibujar el Header inmediatamente
                    panelHeader.revalidate();
                    panelHeader.repaint();
                    
                    JOptionPane.showMessageDialog(VentanaPrincipal.this, 
                            "¡Bienvenido de vuelta, " + usuarioSesion.getNombre() + "! El botón JUGAR AHORA está listo.", 
                            "Autenticación Correcta", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // REGÍSTRATE
        btnRegistro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abre el formulario completo de registro de tres campos
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
                    dispose(); // Cerramos la landing page
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
