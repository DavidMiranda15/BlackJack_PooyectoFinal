package com.mycompany.Poker.Vista;

import com.mycompany.Poker.Entidad.Usuario;
import com.mycompany.Poker.Vista.disenos.BotonCasino;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DialogoRegistro extends JDialog {
    private JTextField txtNombre;
    private JPasswordField txtContrasena;
    private JTextField txtSaldo;
    private BotonCasino btnRegistrar;
    private ClienteRed clienteRed;

    public DialogoRegistro(JFrame padre, ClienteRed clienteRed) {
        super(padre, "Registro de Nuevo Usuario - Casino Royal", true);
        this.clienteRed = clienteRed;

        setSize(400, 320);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout());
        setResizable(false);

        // Formulario Estilo Oscuro P5
        JPanel panelForm = new JPanel(new GridLayout(6, 1, 5, 2));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));
        panelForm.setBackground(new Color(20, 20, 20));

        JLabel lblNombre = new JLabel("Nombre de Usuario:");
        lblNombre.setForeground(Color.WHITE);
        txtNombre = new JTextField();

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setForeground(Color.WHITE);
        txtContrasena = new JPasswordField();

        JLabel lblSaldo = new JLabel("Saldo o Depósito Inicial ($):");
        lblSaldo.setForeground(Color.WHITE);
        txtSaldo = new JTextField("1000"); // Bono sugerido

        panelForm.add(lblNombre);
        panelForm.add(txtNombre);
        panelForm.add(lblPass);
        panelForm.add(txtContrasena);
        panelForm.add(lblSaldo);
        panelForm.add(txtSaldo);

        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(new Color(20, 20, 20));
        panelBoton.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        btnRegistrar = new BotonCasino("Crear Cuenta", new Color(219, 20, 46), Color.WHITE, 15);
        btnRegistrar.setPreferredSize(new Dimension(180, 40));
        panelBoton.add(btnRegistrar);

        add(panelForm, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);

        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = txtNombre.getText().trim();
                String pass = new String(txtContrasena.getPassword()).trim();
                String saldoStr = txtSaldo.getText().trim();

                if (nombre.isEmpty() || pass.isEmpty() || saldoStr.isEmpty()) {
                    JOptionPane.showMessageDialog(DialogoRegistro.this, "Todos los campos son obligatorios.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    double saldo = Double.parseDouble(saldoStr);
                    if (saldo < 0) {
                        JOptionPane.showMessageDialog(DialogoRegistro.this, "El saldo no puede ser negativo.", "Monto Inválido", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // 1. Armamos el objeto de la entidad
                    Usuario nuevo = new Usuario();
                    nuevo.setNombre(nombre);
                    nuevo.setContrasena(pass);
                    nuevo.setSaldo(saldo);

                    // --- CONEXIÓN REAL CON EL SERVIDOR ---
                    // Le mandamos el objeto al cliente de red para que lo procese el HiloCliente
                    boolean exito = clienteRed.registrarUsuario(nuevo);

                    if (exito) {
                        JOptionPane.showMessageDialog(DialogoRegistro.this, "¡Usuario registrado con éxito en la base de datos!", "Registro Completado", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // Se cierra el registro listo
                    } else {
                        // El servidor devolvió REGISTRO_FAIL porque buscarPorNombre encontró un duplicado en db4o
                        JOptionPane.showMessageDialog(DialogoRegistro.this, "El nombre de usuario ya está en uso. Elige otro.", "Error de Registro", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(DialogoRegistro.this, "El saldo debe ser un número entero o decimal.", "Error de Format", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}