package com.mycompany.Poker.Persistencia;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.mycompany.Poker.Entidad.Usuario;

public class UsuarioDAO {

    public Usuario buscarPorNombre(String nombre) {
        ObjectContainer db = ConexionDB.getConexion();
        Usuario ejemplo = new Usuario();
        ejemplo.setNombre(nombre);

        ObjectSet<Usuario> resultado = db.queryByExample(ejemplo);

        if (resultado.hasNext()) {
            return resultado.next(); // Regresa el usuario con su saldo almacenado [cite: 10]
        }
        return null; // No existe
    }

    // Guarda un usuario nuevo o actualiza el saldo de uno existente de forma automática [cite: 16]
    public void guardarOActualizar(Usuario usuario) {
        ObjectContainer db = ConexionDB.getConexion();
        try {
            Usuario usuarioExistente = buscarPorNombre(usuario.getNombre());

            if (usuarioExistente != null) {
                // Si ya existe, actualiza el saldo [cite: 16]
                usuarioExistente.setSaldo(usuario.getSaldo());
                db.store(usuarioExistente);
            } else {
                // Si es nuevo, lo registra por primera vez
                db.store(usuario);
            }
            db.commit(); // Asegura los cambios en el archivo [cite: 16]
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            ConexionDB.cerrarConexion();
        }
    }
}