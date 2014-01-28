package com.cusl.ull.qdemos.bbdd.utilities;

import android.content.Context;

import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.bbdd.models.UsuarioEleccion;
import com.cusl.ull.qdemos.utilities.DatosQdada;
import com.facebook.model.GraphUser;
import com.mobandme.ada.Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 17/01/14.
 */
public class Conversores {

    public static Usuario fromGrapUserToUsuario (Context ctx, GraphUser user){
        Usuario u = new Usuario(user.getName(), user.getId(), null);
        if (!BBDD.existeUsuario(ctx, user.getId())){
            try{
                BBDD.getApplicationDataContext(ctx).usuarioDao.save(u);
            } catch (Exception e){}
        } else {
                u = BBDD.getUsuario(ctx, user.getId());
        }
        return u;
    }

    public static List<Usuario> fromGraphUserToUsuario (Context ctx, List<GraphUser> users){
        List<Usuario> usuarios = new ArrayList<Usuario>();
        if (users != null){
            for (GraphUser user: users){
                usuarios.add(fromGrapUserToUsuario(ctx, user));
            }
        }
        return usuarios;
    }

    public static Qdada fromDatosQdadaToQdada (Context ctx){
        try {
            Qdada qdada =  new Qdada(ctx, DatosQdada.getTitulo(), DatosQdada.getDescripcion(), DatosQdada.getCreador(), Conversores.fromGraphUserToUsuario(ctx, DatosQdada.getInvitados()), DatosQdada.getLatitud(), DatosQdada.getLongitud(), DatosQdada.getDireccion(), DatosQdada.getLimite(), DatosQdada.getReinvitacion());
            return qdada;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

}
