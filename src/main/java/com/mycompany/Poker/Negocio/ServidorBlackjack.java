package com.mycompany.Poker.Negocio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServidorBlackjack {
    private static final int PUERTO = 12345;

    // LA CLAVE MULTIJUGADOR: Una lista global y sincronizada compartida por todos los hilos
    public static List<HiloCliente> clientesConectados = Collections.synchronizedList(new ArrayList<>());
    public static com.mycompany.Poker.Entidad.Mazo mazoMesa = new com.mycompany.Poker.Entidad.Mazo();

    public static void main(String[] args) {
        System.out.println("=== SERVIDOR BLACKJACK INICIADO ===");
        
        // =========================================================================
        // REQUERIMIENTO UNIDAD III: INTROSPECCIÓN DINÁMICA DE JAVABEANS
        // =========================================================================
        int limiteApuestaCargado = 100; // Valor de respaldo
        try {
            // Instanciamos el Bean de configuración
            com.mycompany.Poker.Entidad.ConfiguracionJuego config = new com.mycompany.Poker.Entidad.ConfiguracionJuego();
            
            // Usamos el Introspector de Java para obtener los descriptores de propiedades del Bean
            java.beans.BeanInfo info = java.beans.Introspector.getBeanInfo(config.getClass());
            java.beans.PropertyDescriptor[] descriptores = info.getPropertyDescriptors();
            
            // Buscamos dinámicamente la propiedad "apuestaMinima" mediante reflexión e introspección
            for (java.beans.PropertyDescriptor pd : descriptores) {
                if (pd.getName().equals("apuestaMinima")) {
                    // Invocamos dinámicamente el método GET (getApuestaMinima)
                    java.lang.reflect.Method metodoGet = pd.getReadMethod();
                    limiteApuestaCargado = (int) metodoGet.invoke(config);
                    System.out.println("[Introspección] Configuración cargada dinámicamente vía BeanInfo.");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al aplicar introspección: " + e.getMessage());
        }

        System.out.println("Apuesta mínima de la mesa establecida en: $" + limiteApuestaCargado);
        // =========================================================================

        // Barajamos los naipes al encender el casino
        mazoMesa.barajar();
        System.out.println("Mazo inicial listo y barajado con " + mazoMesa.cartasRestantes() + " cartas.");

        // =========================================================================
        // APERTURA DEL PUERTO Y BUCLE DE CONEXIONES (SOCKETS TCP/IP)
        // =========================================================================
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Escuchando en el puerto " + PUERTO + "...");

            while (true) {
                // Se queda esperando a que un cliente se conecte
                Socket socketCliente = servidor.accept();
                System.out.println("Nuevo cliente conectado desde: " + socketCliente.getInetAddress());

                // Crea un hilo independiente para manejar a este cliente (Concurrencia)
                HiloCliente nuevoHilo = new HiloCliente(socketCliente);
                
                // 1. ANTES de iniciar el hilo, lo metemos en la lista de la mesa global
                clientesConectados.add(nuevoHilo);

                Thread hilo = new Thread(nuevoHilo);
                hilo.start();
            }
        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}