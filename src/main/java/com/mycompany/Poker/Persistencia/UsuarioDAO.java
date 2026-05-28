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
            return resultado.next();
        }
        return null;
    }

    public void guardarOActualizar(Usuario usuario) {
        ObjectContainer db = ConexionDB.getConexion();
        try {
            Usuario usuarioExistente = buscarPorNombre(usuario.getNombre());

            if (usuarioExistente != null) {
                usuarioExistente.setSaldo(usuario.getSaldo());
                db.store(usuarioExistente);
            } else {
                db.store(usuario);
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            ConexionDB.cerrarConexion();
        }
    }
}
