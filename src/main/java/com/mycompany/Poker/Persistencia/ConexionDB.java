package com.mycompany.Poker.Persistencia;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

public class ConexionDB {
    private static final String PATH = "usuarios.db4o";
    private static ObjectContainer db = null;

    // Obtiene la conexión activa (Singleton)
    public static ObjectContainer getConexion() {
        // En lugar de isClosed(), intentamos una operación ligera o simplemente
        // dejamos que db4o maneje la instancia, o la recreamos si es null.
        try {
            if (db == null) {
                db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), PATH);
            }
        } catch (Exception e) {
            // Si el archivo se quedó bloqueado o cerrado por error, forzamos abrirlo de nuevo
            db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), PATH);
        }
        return db;
    }

    // Cierra la base de datos de forma segura
    public static void cerrarConexion() {
        if (db != null) {
            db.close();
            db = null; // Súper importante: lo volvemos null para que el método getConexion sepa que debe reabrirlo
        }
    }
}