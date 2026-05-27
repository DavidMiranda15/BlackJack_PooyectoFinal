package com.mycompany.Poker.Vista;

import com.mycompany.Poker.Entidad.Usuario;
import com.mycompany.Poker.Vista.disenos.BotonCasino;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DialogoLogin extends JDialog {
    private JTextField txtNombre;
    private JPasswordField txtContrasena;
    private BotonCasino btnEntrar;
    private ClienteRed clienteRed;
    private Usuario usuarioLogueado = null; // Guardará el usuario si pasa el filtro

    public DialogoLogin(JFrame padre, ClienteRed clienteRed) {
        super(padre, "Iniciar Sesión - Casino Royal", true);
        this.clienteRed = clienteRed;

        setSize(360, 240);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel panelForm = new JPanel(new GridLayout(4, 1, 5, 2));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));
        panelForm.setBackground(new Color(20, 20, 20));

        JLabel lblNombre = new JLabel("Nombre de Usuario:");
        lblNombre.setForeground(Color.WHITE);
        txtNombre = new JTextField();

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setForeground(Color.WHITE);
        txtContrasena = new JPasswordField();

        panelForm.add(lblNombre);
        panelForm.add(txtNombre);
        panelForm.add(lblPass);
        panelForm.add(txtContrasena);

        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(new Color(20, 20, 20));
        panelBoton.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        btnEntrar = new BotonCasino("Acceder", new Color(219, 20, 46), Color.WHITE, 15);
        btnEntrar.setPreferredSize(new Dimension(140, 38));
        panelBoton.add(btnEntrar);

        add(panelForm, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);

       btnEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = txtNombre.getText().trim();
                String pass = new String(txtContrasena.getPassword()).trim();
                
                if (nombre.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(DialogoLogin.this, "Por favor introduce tus credenciales.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Aquí se conecta directo al servidor
                Usuario verificado = clienteRed.autenticarUsuario(nombre, pass);
                System.out.println("El servidor me regresó el objeto: " + verificado);
                // Ojo aquí: Si pusiste "juan" y no existe, 'verificado' DEBE SER NULL
                if (verificado != null) {
                    usuarioLogueado = verificado; 
                    JOptionPane.showMessageDialog(DialogoLogin.this, "¡Sesión iniciada correctamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); 
                } else {
                    // Si te está diciendo sesión iniciada con juan, es porque el if de arriba se está cumpliendo solo
                    JOptionPane.showMessageDialog(DialogoLogin.this, "Usuario o contraseña incorrectos.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Este método permite a la VentanaPrincipal extraer los datos del usuario logueado
    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }
}