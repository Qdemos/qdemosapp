package com.cusl.ull.qdemos.bbdd.utilities;

import android.content.Context;

import com.cusl.ull.qdemos.bbdd.models.Fecha;
import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.bbdd.models.UsuarioEleccion;
import com.cusl.ull.qdemos.utilities.DatosQdada;
import com.facebook.model.GraphUser;

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
            List<UsuarioEleccion> eleccion = new ArrayList<UsuarioEleccion>();
            UsuarioEleccion ue = new UsuarioEleccion(ctx, DatosQdada.getCreador(), fromListDateToListFecha(DatosQdada.getFechas()));
            BBDD.appDataContext.participanteDao.save(ue);
            eleccion.add(ue);
            Qdada qdada =  new Qdada(ctx, DatosQdada.getTitulo(), DatosQdada.getDescripcion(), DatosQdada.getCreador(), Conversores.fromGraphUserToUsuario(ctx, DatosQdada.getInvitados()), eleccion, DatosQdada.getLatitud(), DatosQdada.getLongitud(), DatosQdada.getDireccion(), DatosQdada.getFechas(), DatosQdada.getLimite(), DatosQdada.getReinvitacion());
            return qdada;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static List<Fecha> fromListDateToListFecha(List<Date> dates){
        List<Fecha> fechas = new ArrayList<Fecha>();
        for (Date date: dates){
            fechas.add(fromDateToFecha(date));
        }
        return fechas;
    }

    public static Fecha fromDateToFecha(Date date){
        return new Fecha(date);
    }
}
