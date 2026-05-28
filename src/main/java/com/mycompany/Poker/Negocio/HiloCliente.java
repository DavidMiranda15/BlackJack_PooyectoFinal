package com.mycompany.Poker.Negocio;

import com.mycompany.Poker.Entidad.Usuario;
import com.mycompany.Poker.Persistencia.UsuarioDAO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class HiloCliente implements Runnable {

    private Socket socket;
    public ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private UsuarioDAO usuarioDAO;
    public Usuario usuarioActual;
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

            while (true) {
                String accion = (String) entrada.readObject();
                if (accion == null) {
                    break;
                }

                System.out.println("Comando recibido en el servidor: " + accion);

                if (accion.equals("REGISTRO")) {
                    Usuario usuarioRegistro = (Usuario) entrada.readObject();
                    Usuario existente = usuarioDAO.buscarPorNombre(usuarioRegistro.getNombre());
                    if (existente == null) {
                        usuarioDAO.guardarOActualizar(usuarioRegistro);
                        salida.writeObject("REGISTRO_OK");
                    } else {
                        salida.writeObject("REGISTRO_FAIL");
                    }
                    salida.flush();
                } else if (accion.equals("LOGIN")) {
                    Usuario datosLogin = (Usuario) entrada.readObject();
                    usuarioActual = usuarioDAO.buscarPorNombre(datosLogin.getNombre());

                    if (usuarioActual != null && usuarioActual.getContrasena().equals(datosLogin.getContrasena())) {
                        System.out.println("[Login] Acceso concedido a: " + usuarioActual.getNombre());

                        Usuario usuarioEnviar = new Usuario();
                        usuarioEnviar.setNombre(usuarioActual.getNombre());
                        usuarioEnviar.setContrasena(usuarioActual.getContrasena());
                        usuarioEnviar.setSaldo(usuarioActual.getSaldo());

                        salida.writeObject(usuarioEnviar);
                    } else {
                        System.out.println("[Login] Intento fallido para: " + datosLogin.getNombre());
                        salida.writeObject(null);
                    }
                    salida.flush();
                } else if (accion.equals("MESA_UNIR")) {
                    String nombre = (String) entrada.readObject();
                    this.usuarioActual = usuarioDAO.buscarPorNombre(nombre);
                    System.out.println("¡" + usuarioActual.getNombre() + " entró a la mesa permanente!");

                    for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                        if (hc != this && hc.usuarioActual != null) {
                            hc.salida.writeObject("NUEVO_JUGADOR:" + this.usuarioActual.getNombre());
                            hc.salida.flush();
                        }
                    }

                    while (true) {
                        Object peticion = entrada.readObject();
                        if (peticion == null) {
                            break;
                        }

                        if (peticion instanceof String && ((String) peticion).startsWith("APUESTA:")) {
                            String[] partes = ((String) peticion).split(":");
                            this.apuestaActual = Integer.parseInt(partes[1]);
                            System.out.println(usuarioActual.getNombre() + " ha apostado: $" + this.apuestaActual);

                            repartirManoInicial();
                            continue;
                        }

                        if (peticion.equals("PEDIR_CARTA")) {
                            if (this.puntosActuales == 0) {
                                continue;
                            }
                            if (this.turnoTerminado || this.puntosActuales > 21) {
                                continue;
                            }

                            com.mycompany.Poker.Entidad.Carta cartaRobada = ServidorBlackjack.mazoMesa.robarCarta();

                            if (cartaRobada != null) {
                                this.puntosActuales += cartaRobada.getPuntosBlackjack();
                                System.out.println(usuarioActual.getNombre() + " robó: " + cartaRobada + " | Total: " + this.puntosActuales);

                                String broadcastCarta = "CARTA_REPARTIDA:" + usuarioActual.getNombre() + ":"
                                        + cartaRobada.getPalo() + ":"
                                        + cartaRobada.getValor() + ":"
                                        + this.puntosActuales;

                                for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                                    if (hc.usuarioActual != null) {
                                        hc.salida.writeObject(broadcastCarta);
                                        hc.salida.flush();
                                    }
                                }

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
                        } else if (peticion.equals("PLANTARSE")) {
                            if (this.puntosActuales == 0) {
                                continue;
                            }
                            if (this.turnoTerminado || this.puntosActuales > 21) {
                                continue;
                            }

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

    private void comprobarTurnoDealer() {
        boolean todosTerminaron = true;

        for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
            if (hc.usuarioActual != null && !hc.turnoTerminado) {
                todosTerminaron = false;
                break;
            }
        }

        if (todosTerminaron) {
            System.out.println("=== REVELANDO CARTA OCULTA Y JUGANDO LA CASA ===");

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

            for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                try {
                    hc.salida.writeObject("DEALER_REVELAR");
                    hc.salida.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            int puntosDealer = cD1.getPuntosBlackjack() + cD2.getPuntosBlackjack();

            String broadcastD1 = "DEALER_CARTA:" + cD1.getValor() + " de " + cD1.getPalo() + ":" + puntosDealer;
            String broadcastD2 = "DEALER_CARTA:" + cD2.getValor() + " de " + cD2.getPalo() + ":" + puntosDealer;

            for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                try {
                    hc.salida.writeObject(broadcastD1);
                    hc.salida.writeObject(broadcastD2);
                    hc.salida.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            while (puntosDealer < 17) {
                com.mycompany.Poker.Entidad.Carta cartaExtra = ServidorBlackjack.mazoMesa.robarCarta();
                if (cartaExtra == null) {
                    break;
                }

                puntosDealer += cartaExtra.getPuntosBlackjack();
                System.out.println("Dealer roba carta extra: " + cartaExtra + " | Total: " + puntosDealer);

                String broadcastExtra = "DEALER_CARTA:" + cartaExtra.getValor() + " de " + cartaExtra.getPalo() + ":" + puntosDealer;

                for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                    try {
                        hc.salida.writeObject(broadcastExtra);
                        hc.salida.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }

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
                        nuevoSaldo += dineroEnJuego;
                        veredictoRed = "GANASTE";
                    } else if (tusPuntos > puntosDealer) {
                        nuevoSaldo += dineroEnJuego;
                        veredictoRed = "GANASTE";
                    } else if (tusPuntos < puntosDealer) {
                        nuevoSaldo -= dineroEnJuego;
                    } else {
                        veredictoRed = "EMPATE";
                    }

                    hc.usuarioActual.setSaldo(nuevoSaldo);
                    hc.usuarioDAO.guardarOActualizar(hc.usuarioActual);
                    System.out.println("[db4o] Saldo actualizado de " + hc.usuarioActual.getNombre() + ": $" + nuevoSaldo);

                    try {
                        hc.salida.writeObject("MESA_RESULTADO:" + puntosDealer + ":" + veredictoRed + ":" + nuevoSaldo);
                        hc.salida.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }

            System.out.println("=== RESETEANDO MAZO GLOBAL Y LIMPIANDO MESAS ===");
            ServidorBlackjack.mazoMesa.barajar();

            for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                try {
                    hc.puntosActuales = 0;
                    hc.turnoTerminado = false;
                    hc.apuestaActual = 0;
                    hc.cartaDealer1 = null;
                    hc.cartaDealer2 = null;

                    hc.salida.writeObject("MESA_REINICIAR");
                    hc.salida.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void repartirManoInicial() {
        try {
            System.out.println("=== REPARTIENDO MANO DE BIENVENIDA A " + usuarioActual.getNombre() + " ===");

            this.puntosActuales = 0;
            this.turnoTerminado = false;

            for (int i = 0; i < 2; i++) {
                com.mycompany.Poker.Entidad.Carta cJ = ServidorBlackjack.mazoMesa.robarCarta();
                if (cJ != null) {
                    this.puntosActuales += cJ.getPuntosBlackjack();
                    String broadcastCarta = "CARTA_REPARTIDA:" + usuarioActual.getNombre() + ":"
                            + cJ.getPalo() + ":" + cJ.getValor() + ":" + this.puntosActuales;

                    for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                        if (hc.usuarioActual != null) {
                            hc.salida.writeObject(broadcastCarta);
                            hc.salida.flush();
                        }
                    }
                }
            }

            this.cartaDealer1 = ServidorBlackjack.mazoMesa.robarCarta();
            if (this.cartaDealer1 != null) {
                String broadcastDealer1 = "DEALER_CARTA:" + this.cartaDealer1.getValor() + " de " + this.cartaDealer1.getPalo() + ":" + this.cartaDealer1.getPuntosBlackjack();
                for (HiloCliente hc : ServidorBlackjack.clientesConectados) {
                    hc.salida.writeObject(broadcastDealer1);
                    hc.salida.flush();
                }
            }

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
