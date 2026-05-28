package com.mycompany.Poker.Vista;

import com.mycompany.Poker.Entidad.Usuario;
import java.io.*;
import java.net.Socket;

public class ClienteRed {

    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private String ip = "localhost";
    private int puerto = 12345;

    public void conectar() {
        try {
            socket = new Socket(ip, puerto);
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());
            System.out.println("Conectado al servidor de Poker.");
        } catch (IOException e) {
            System.out.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }

    public Usuario autenticarUsuario(String nombre, String contrasena) {
        try (Socket socketTemp = new Socket(ip, puerto); ObjectOutputStream salidaTemp = new ObjectOutputStream(socketTemp.getOutputStream()); ObjectInputStream entradaTemp = new ObjectInputStream(socketTemp.getInputStream())) {

            salidaTemp.writeObject("LOGIN");

            Usuario datosLogin = new Usuario();
            datosLogin.setNombre(nombre);
            datosLogin.setContrasena(contrasena);

            salidaTemp.writeObject(datosLogin);
            salidaTemp.flush();

            return (Usuario) entradaTemp.readObject();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error en la autenticación de red: " + e.getMessage());
            return null;
        }
    }

    public boolean registrarUsuario(Usuario nuevoUsuario) {
        try (Socket socketTemp = new Socket(ip, puerto); ObjectOutputStream salidaTemp = new ObjectOutputStream(socketTemp.getOutputStream()); ObjectInputStream entradaTemp = new ObjectInputStream(socketTemp.getInputStream())) {

            salidaTemp.writeObject("REGISTRO");

            salidaTemp.writeObject(nuevoUsuario);
            salidaTemp.flush();

            String respuesta = (String) entradaTemp.readObject();
            return "REGISTRO_OK".equals(respuesta);

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error en el registro de red: " + e.getMessage());
            return false;
        }
    }

    public boolean conectarMesa(String nombreUsuario) {
        try {
            socket = new Socket(ip, puerto);
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());

            salida.writeObject("MESA_UNIR");
            salida.writeObject(nombreUsuario);
            salida.flush();

            return true;
        } catch (IOException e) {
            System.out.println("Error al conectar estabilidad de mesa: " + e.getMessage());
            return false;
        }
    }

    public void enviarComandoJuego(String comando) {
        try {
            if (salida != null) {
                salida.writeObject(comando);
                salida.flush();
            }
        } catch (IOException e) {
            System.out.println("Error al enviar comando de juego: " + e.getMessage());
        }
    }

    public ObjectInputStream getEntrada() {
        return this.entrada;
    }
}
