package com.mycompany.Poker.Persistencia;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

public class ConexionDB {
    private static final String PATH = "usuarios.db4o";
    private static ObjectContainer db = null;

    public static ObjectContainer getConexion() {
        try {
            if (db == null) {
                db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), PATH);
            }
        } catch (Exception e) {
            db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), PATH);
        }
        return db;
    }

    public static void cerrarConexion() {
        if (db != null) {
            db.close();
            db = null; 
        }
    }
}