package com.cusl.ull.qdemos.bbdd.utilities;

import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.facebook.model.GraphUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paco on 17/01/14.
 */
public class Conversores {

    public static Usuario fromGrapUserToUsuario (GraphUser user){
        return new Usuario(user.getName(), user.getId(), null);
    }

    public static List<Usuario> fromGraphUserToUsuario (List<GraphUser> users){
        List<Usuario> usuarios = new ArrayList<Usuario>();
        if (users != null){
            for (GraphUser user: users){
                usuarios.add(fromGrapUserToUsuario(user));
            }
        }
        return usuarios;
    }
}
