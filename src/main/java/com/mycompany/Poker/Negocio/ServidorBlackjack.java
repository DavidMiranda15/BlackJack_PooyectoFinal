package com.mycompany.Poker.Negocio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServidorBlackjack {

    private static final int PUERTO = 12345;

    public static List<HiloCliente> clientesConectados = Collections.synchronizedList(new ArrayList<>());
    public static com.mycompany.Poker.Entidad.Mazo mazoMesa = new com.mycompany.Poker.Entidad.Mazo();

    public static void main(String[] args) {
        System.out.println("=== SERVIDOR BLACKJACK INICIADO ===");

        int limiteApuestaCargado = 100;
        try {
            com.mycompany.Poker.Entidad.ConfiguracionJuego config = new com.mycompany.Poker.Entidad.ConfiguracionJuego();

            java.beans.BeanInfo info = java.beans.Introspector.getBeanInfo(config.getClass());
            java.beans.PropertyDescriptor[] descriptores = info.getPropertyDescriptors();

            for (java.beans.PropertyDescriptor pd : descriptores) {
                if (pd.getName().equals("apuestaMinima")) {
                    java.lang.reflect.Method metodoGet = pd.getReadMethod();
                    limiteApuestaCargado = (int) metodoGet.invoke(config);
                    System.out.println("[Introspeccion] Configuracion cargada dinamicamente via BeanInfo.");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al aplicar introspección: " + e.getMessage());
        }

        System.out.println("Apuesta minima de la mesa establecida en: $" + limiteApuestaCargado);

        mazoMesa.barajar();
        System.out.println("Mazo inicial listo y barajado con " + mazoMesa.cartasRestantes() + " cartas.");

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Escuchando en el puerto " + PUERTO + "...");

            while (true) {
                Socket socketCliente = servidor.accept();
                System.out.println("Nuevo cliente conectado desde: " + socketCliente.getInetAddress());

                HiloCliente nuevoHilo = new HiloCliente(socketCliente);

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
