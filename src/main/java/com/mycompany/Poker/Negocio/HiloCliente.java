package com.mycompany.Poker.Negocio;

import com.mycompany.Poker.Entidad.Usuario;
import com.mycompany.Poker.Persistencia.UsuarioDAO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class HiloCliente implements Runnable {
    private Socket socket;
    public ObjectOutputStream salida; // Permite el broadcast desde otros hilos concurrentes
    private ObjectInputStream entrada;
    private UsuarioDAO usuarioDAO;
    public Usuario usuarioActual; // Facilita las validaciones cruzadas entre hilos de la mesa

    // VARIABLES DE JUEGO GLOBALES POR CLIENTE (Mesa Multijugador)
    public int puntosActuales = 0; 
    public boolean turnoTerminado = false; 
    public int apuestaActual = 0;
    public com.mycompany.Poker.Entidad.Carta cartaDealer1;
    public com.mycompany.Poker.Entidad.Carta cartaDealer2;

    public HiloCliente(Socket socket) {
        this.socket = socket;
        this.usuarioDAO = new UsuarioDAO();
    }

    @Override
    public void run() {
        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());

            // BUCLE PRINCIPAL: Mantiene vivo al servidor escuchando qué quiere el cliente
            while (true) {
                // Leemos el String del protocolo ("LOGIN", "REGISTRO", "MESA_UNIR")
                String accion = (String) entrada.readObject();
                if (accion == null) break;

                System.out.println("Comando recibido en el servidor: " + accion);

                // =========================================================================
                // PROTOCOLO: REGISTRO DE USUARIOS
                // =========================================================================
                if (accion.equals("REGISTRO")) {
                    Usuario usuarioRegistro = (Usuario) entrada.readObject();
                    Usuario existente = usuarioDAO.buscarPorNombre(usuarioRegistro.getNombre());
                    if (existente == null) {
                        usuarioDAO.guardarOActualizar(usuarioRegistro);
                        salida.writeObject("REGISTRO_OK");
                    } else {
                        salida.writeObject("REGISTRO_FAIL");
                    }
                    salida.flush(); // 🟥 Cambiado: Solo usamos flush para empujar los bytes
                } 
                
                // =========================================================================
                // PROTOCOLO: INICIO DE SESIÓN (LOGIN) (BLINDADO SIN RESET)
                // =========================================================================
                else if (accion.equals("LOGIN")) {
                    Usuario datosLogin = (Usuario) entrada.readObject();
                    usuarioActual = usuarioDAO.buscarPorNombre(datosLogin.getNombre());
                    
                    if (usuarioActual != null && usuarioActual.getContrasena().equals(datosLogin.getContrasena())) {
                        System.out.println("[Login] Acceso concedido a: " + usuarioActual.getNombre());
                        
                        // 🟥 TRUCO DE SEGURIDAD FRESCO:
                        // Creamos un clon limpio de los datos extraídos de db4o antes de enviarlos.
                        // Esto rompe la caché de serialización de red de forma nativa sin usar .reset()
                        Usuario usuarioEnviar = new Usuario();
                        usuarioEnviar.setNombre(usuarioActual.getNombre());
                        usuarioEnviar.setContrasena(usuarioActual.getContrasena());
                        usuarioEnviar.setSaldo(usuarioActual.getSaldo());
                        
                        salida.writeObject(usuarioEnviar);
                    } else {
                        System.out.println("[Login] Intento fallido para: " + datosLogin.getNombre());
                        salida.writeObject(null);
                    }
                    salida.flush(); // 🟥 Solo empujamos los datos de forma limpia
                }
                
                // =========================================================================
                // PROTOCOLO: ENTRAR A LA MESA DE JUEGO MULTIJUGADOR
                // =========================================================================
                else if (accion.equals("MESA_UNIR")) {
                    String nombre = (String) entrada.readObject();
                    this.usuarioActual = usuarioDAO.buscarPorNombre(nombre);
                    System.out.println("¡" + usuarioActual.getNombre() + " entró a la mesa permanente!");

                    // DIFUSIÓN: Avisamos a todos los demás hilos activos de la llegada del nuevo rival
                    for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                        if (hc != this && hc.usuarioActual != null) {
                            hc.salida.writeObject("NUEVO_JUGADOR:" + this.usuarioActual.getNombre());
                            hc.salida.flush();
                        }
                    }
                    
                    // 🟥 CONFIGURACIÓN ASÍNCRONA CORREGIDA: 🟥
                    // No repartimos nada aquí. La mesa se queda limpia esperando a que el cliente
                    // ponga su dinero y gatille el ciclo contable desde el bucle 'while' de abajo.
                    
                    // Bucle secundario: Procesamiento de las jugadas en tiempo real de la mano
                    while (true) {
                        Object peticion = entrada.readObject();
                        if (peticion == null) break;
                        
                        // JUGADA CONTABLE: Registro previo del monto de la apuesta y GATILLO DEL REPARTO
                        if (peticion instanceof String && ((String)peticion).startsWith("APUESTA:")) {
                            String[] partes = ((String)peticion).split(":");
                            this.apuestaActual = Integer.parseInt(partes[1]);
                            System.out.println(usuarioActual.getNombre() + " ha apostado: $" + this.apuestaActual);
                            
                            // 🟥 SINCRO DEL CASINO: En cuanto se recibe la apuesta, se liberan las cartas de default
                            repartirManoInicial();
                            continue;
                        }
                        
                        // JUGADA A: El cliente presionó "PEDIR CARTA"
                        if (peticion.equals("PEDIR_CARTA")) {
                            if (this.puntosActuales == 0) continue; // Candado de seguridad ante clicks huérfanos
                            if (this.turnoTerminado || this.puntosActuales > 21) continue;

                            com.mycompany.Poker.Entidad.Carta cartaRobada = ServidorBlackjack.mazoMesa.robarCarta();
                            
                            if (cartaRobada != null) {
                                this.puntosActuales += cartaRobada.getPuntosBlackjack();
                                System.out.println(usuarioActual.getNombre() + " robó: " + cartaRobada + " | Total: " + this.puntosActuales);
                                
                                String broadcastCarta = "CARTA_REPARTIDA:" + usuarioActual.getNombre() + ":" + 
                                                       cartaRobada.getPalo() + ":" + 
                                                       cartaRobada.getValor() + ":" + 
                                                       this.puntosActuales;
                                
                                for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                                    if (hc.usuarioActual != null) {
                                        hc.salida.writeObject(broadcastCarta);
                                        hc.salida.flush();
                                    }
                                }

                                // Si el jugador se pasa de 21, truena (BUST) y pierde su turno en automático
                                if (this.puntosActuales > 21) {
                                    this.turnoTerminado = true;
                                    String broadcastBust = "JUGADOR_BUST:" + usuarioActual.getNombre() + ":" + this.puntosActuales;
                                    
                                    for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                                        if (hc.usuarioActual != null) {
                                            hc.salida.writeObject(broadcastBust);
                                            hc.salida.flush();
                                        }
                                    }
                                    comprobarTurnoDealer();
                                }
                            }
                        }
                        
                        // JUGADA B: El cliente presionó "PLANTARSE"
                        else if (peticion.equals("PLANTARSE")) {
                            if (this.puntosActuales == 0) continue; // Candado de seguridad
                            if (this.turnoTerminado || this.puntosActuales > 21) continue;
                            
                            this.turnoTerminado = true;
                            System.out.println("El usuario " + usuarioActual.getNombre() + " se plantó con " + this.puntosActuales + " puntos.");
                            
                            String broadcastPlanto = "JUGADOR_PLANTADO:" + usuarioActual.getNombre() + ":" + this.puntosActuales;
                            
                            for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                                if (hc.usuarioActual != null) {
                                    hc.salida.writeObject(broadcastPlanto);
                                    hc.salida.flush();
                                }
                            }
                            comprobarTurnoDealer();
                        }
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("El cliente " + (usuarioActual != null ? usuarioActual.getNombre() : "Desconocido") + " se ha desconectado.");
        } finally {
            ServidorBlackjack.clientesConectados.remove(this);
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // =========================================================================
    // MÉTODOS DE CONTROL DE IA (EL DEALER) Y TRANSACCIONES OODB (DB4O)
    // =========================================================================
    private void comprobarTurnoDealer() {
        boolean todosTerminaron = true;
        
        // Verificamos si queda algún jugador con turno activo en la mesa concurrente
        for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
            if (hc.usuarioActual != null && !hc.turnoTerminado) {
                todosTerminaron = false;
                break;
            }
        }
        
        // Si ya terminaron todos los jugadores sentados, la casa abre sus naipes
        if (todosTerminaron) {
            System.out.println("=== REVELANDO CARTA OCULTA Y JUGANDO LA CASA ===");
            
            // BUSQUEDA DE CARTAS COMPARTIDAS ENTRE HILOS PARA EVITAR EL CONGELAMIENTO
            com.mycompany.Poker.Entidad.Carta cD1 = this.cartaDealer1;
            com.mycompany.Poker.Entidad.Carta cD2 = this.cartaDealer2;
            
            if (cD1 == null || cD2 == null) {
                for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                    if (hc.cartaDealer1 != null && hc.cartaDealer2 != null) {
                        cD1 = hc.cartaDealer1;
                        cD2 = hc.cartaDealer2;
                        break;
                    }
                }
            }

            // 1. Mandamos una orden de limpieza a las interfaces para quitar el "?"
            for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                try {
                    hc.salida.writeObject("DEALER_REVELAR");
                    hc.salida.flush();
                } catch (IOException e) { e.printStackTrace(); }
            }

            // 2. Inicializamos los puntos sumando las DOS cartas reales compartidas
            int puntosDealer = cD1.getPuntosBlackjack() + cD2.getPuntosBlackjack();
            
            // 3. Redibujamos de inmediato las dos cartas de forma abierta en los clientes
            String broadcastD1 = "DEALER_CARTA:" + cD1.getValor() + " de " + cD1.getPalo() + ":" + puntosDealer;
            String broadcastD2 = "DEALER_CARTA:" + cD2.getValor() + " de " + cD2.getPalo() + ":" + puntosDealer;
            
            for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                try {
                    hc.salida.writeObject(broadcastD1);
                    hc.salida.writeObject(broadcastD2);
                    hc.salida.flush();
                } catch (IOException e) { e.printStackTrace(); }
            }

            // Delay de suspenso
            try { Thread.sleep(1000); } catch (InterruptedException e) {}

            // 4. Bucle oficial: Si tras revelar las dos primeras no llega a 17, roba más cartas
            while (puntosDealer < 17) {
                com.mycompany.Poker.Entidad.Carta cartaExtra = ServidorBlackjack.mazoMesa.robarCarta();
                if (cartaExtra == null) break;
                
                puntosDealer += cartaExtra.getPuntosBlackjack();
                System.out.println("Dealer roba carta extra: " + cartaExtra + " | Total: " + puntosDealer);
                
                String broadcastExtra = "DEALER_CARTA:" + cartaExtra.getValor() + " de " + cartaExtra.getPalo() + ":" + puntosDealer;
                
                for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                    try {
                        hc.salida.writeObject(broadcastExtra);
                        hc.salida.flush();
                    } catch (IOException e) { e.printStackTrace(); }
                }
                
                try { Thread.sleep(1000); } catch (InterruptedException e) {}
            }
            
            // 5. Evaluación final y despacho de resultados
            System.out.println("=== EVALUACIÓN DE VEREDICTOS Y PERSISTENCIA FINANCIERA ===");
            for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                if (hc.usuarioActual != null) {
                    int tusPuntos = hc.puntosActuales;
                    int dineroEnJuego = hc.apuestaActual;
                    double nuevoSaldo = hc.usuarioActual.getSaldo();
                    String veredictoRed = "PERDISTE";

                    if (tusPuntos > 21) {
                        nuevoSaldo -= dineroEnJuego;
                    } else if (puntosDealer > 21) {
                        nuevoSaldo += dineroEnJuego; // El Dealer reventó, ganas
                        veredictoRed = "GANASTE";
                    } else if (tusPuntos > puntosDealer) {
                        nuevoSaldo += dineroEnJuego; // Le ganaste en puntaje, ganas
                        veredictoRed = "GANASTE";
                    } else if (tusPuntos < puntosDealer) {
                        nuevoSaldo -= dineroEnJuego; // Perdiste contra la casa
                    } else {
                        veredictoRed = "EMPATE"; // Recuperas lo apostado
                    }

                    hc.usuarioActual.setSaldo(nuevoSaldo);
                    hc.usuarioDAO.guardarOActualizar(hc.usuarioActual); 
                    System.out.println("[db4o] Saldo actualizado de " + hc.usuarioActual.getNombre() + ": $" + nuevoSaldo);

                    try {
                        hc.salida.writeObject("MESA_RESULTADO:" + puntosDealer + ":" + veredictoRed + ":" + nuevoSaldo);
                        hc.salida.flush();
                    } catch (IOException e) { e.printStackTrace(); }
                }
            }

            // Espera de cortesía de 3 segundos para que los clientes asimilen el resultado en pantalla
            try { Thread.sleep(3000); } catch (InterruptedException e) {}

            System.out.println("=== RESETEANDO MAZO GLOBAL Y LIMPIANDO MESAS ===");
            // 🟥 CORRECCIÓN EXTRAÍDA: Volvemos a barajar de forma segura las cartas sobre la referencia de red existente
            ServidorBlackjack.mazoMesa.barajar(); 

            // 1. Mandamos limpiar las pantallas e indicamos a los JFrames que reactiven los controles de apuestas
            for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                try {
                    hc.puntosActuales = 0;
                    hc.turnoTerminado = false;
                    hc.apuestaActual = 0;
                    hc.cartaDealer1 = null; 
                    hc.cartaDealer2 = null; 
                    
                    hc.salida.writeObject("MESA_REINICIAR");
                    hc.salida.flush();
                } catch (IOException e) { e.printStackTrace(); }
            }
            // 🟥 ELIMINADO EL MAPEO FORZADO DE REPARTO AUTOMÁTICO.
            // La mesa ahora se queda a la espera del gatillo contable limpio para la siguiente ronda.
        }
    }
    
    // Método centralizado para repartir la mano inicial (Rúbrica: 2 al jugador, 2 al dealer, 1 oculta)
    private void repartirManoInicial() {
        try {
            System.out.println("=== REPARTIENDO MANO DE BIENVENIDA A " + usuarioActual.getNombre() + " ===");
            
            // Forzamos el reseteo de marcas de juego para la nueva ronda antes de inyectar naipes
            this.puntosActuales = 0;
            this.turnoTerminado = false;

            // 1. Repartir 2 cartas obligatorias al jugador
            for (int i = 0; i < 2; i++) {
                com.mycompany.Poker.Entidad.Carta cJ = ServidorBlackjack.mazoMesa.robarCarta();
                if (cJ != null) {
                    this.puntosActuales += cJ.getPuntosBlackjack();
                    String broadcastCarta = "CARTA_REPARTIDA:" + usuarioActual.getNombre() + ":" + 
                                           cJ.getPalo() + ":" + cJ.getValor() + ":" + this.puntosActuales;
                    
                    for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                        if (hc.usuarioActual != null) {
                            hc.salida.writeObject(broadcastCarta);
                            hc.salida.flush();
                        }
                    }
                }
            }

            // 2. Repartir Primera Carta del Dealer (VISIBLE)
            this.cartaDealer1 = ServidorBlackjack.mazoMesa.robarCarta(); 
            if (this.cartaDealer1 != null) {
                String broadcastDealer1 = "DEALER_CARTA:" + this.cartaDealer1.getValor() + " de " + this.cartaDealer1.getPalo() + ":" + this.cartaDealer1.getPuntosBlackjack();
                for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                    hc.salida.writeObject(broadcastDealer1);
                    hc.salida.flush();
                }
            }

            // 3. Repartir Segunda Carta del Dealer (OCULTA)
            this.cartaDealer2 = ServidorBlackjack.mazoMesa.robarCarta(); 
            if (this.cartaDealer2 != null) {
                for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                    hc.salida.writeObject("DEALER_CARTA_OCULTA");
                    hc.salida.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Error en el reparto inicial: " + e.getMessage());
        }
    }
}