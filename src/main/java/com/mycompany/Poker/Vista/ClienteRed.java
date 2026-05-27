package com.mycompany.Poker.Vista;

import com.mycompany.Poker.Entidad.Usuario;
import java.io.*;
import java.net.Socket;

public class ClienteRed {
    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private String ip = "localhost";
    private int puerto = 12345; // Asegúrate de que coincida con tu Servidor

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
        // Creamos un socket temporal solo para esta petición
        try (Socket socketTemp = new Socket(ip, puerto);
             ObjectOutputStream salidaTemp = new ObjectOutputStream(socketTemp.getOutputStream());
             ObjectInputStream entradaTemp = new ObjectInputStream(socketTemp.getInputStream())) {
            
            // 1. Enviamos el protocolo de lo que queremos hacer
            salidaTemp.writeObject("LOGIN");
            
            // 2. Creamos el objeto temporal con las credenciales
            Usuario datosLogin = new Usuario();
            datosLogin.setNombre(nombre);
            datosLogin.setContrasena(contrasena);
            
            // 3. Lo mandamos al servidor
            salidaTemp.writeObject(datosLogin);
            salidaTemp.flush();

            // 4. Retornamos la respuesta del servidor (un Usuario o null)
            return (Usuario) entradaTemp.readObject();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error en la autenticación de red: " + e.getMessage());
            return null; 
        }
    }

    // MÉTODO PARA REGISTRAR CONTRA EL SERVIDOR (Abriendo y cerrando su propia conexión)
    public boolean registrarUsuario(Usuario nuevoUsuario) {
        // Creamos un socket temporal dedicado exclusivamente a registrar
        try (Socket socketTemp = new Socket(ip, puerto);
             ObjectOutputStream salidaTemp = new ObjectOutputStream(socketTemp.getOutputStream());
             ObjectInputStream entradaTemp = new ObjectInputStream(socketTemp.getInputStream())) {
            
            // 1. Enviamos el protocolo de Registro
            salidaTemp.writeObject("REGISTRO");
            
            // 2. Enviamos el objeto con los 3 datos cargados
            salidaTemp.writeObject(nuevoUsuario);
            salidaTemp.flush();

            // 3. Escuchamos la respuesta del servidor
            String respuesta = (String) entradaTemp.readObject();
            return "REGISTRO_OK".equals(respuesta);

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error en el registro de red: " + e.getMessage());
            return false;
        }
    }
    // MÉTODO PARA ENTRAR A LA MESA (Usando las variables globales estables que ya tienes)
    public boolean conectarMesa(String nombreUsuario) {
        try {
            // Inicializamos tus variables globales para mantener la tubería abierta
            socket = new Socket(ip, puerto);
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());

            // Enviamos el protocolo al servidor
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

    // Un getter simple para que la ventana pueda leer lo que llega por la tubería global
    public ObjectInputStream getEntrada() {
        return this.entrada;
    }
}
