package com.cusl.ull.qdemos.bbdd.utilities;

import android.content.Context;
import android.util.Log;

import com.cusl.ull.qdemos.bbdd.dao.QdemosDataContext;
import com.cusl.ull.qdemos.bbdd.models.Usuario;

/**
 * Created by Paco on 13/01/14.
 */

// Funciones tipicas de BBDD
public class BBDD {

    public static QdemosDataContext appDataContext;

    public static void initBBDD (Context ctx){
        getApplicationDataContext(ctx);
    }

    // Inicializar los DAOs, para poder acceder a los datos
    public static QdemosDataContext getApplicationDataContext(Context ctx) {
        if (BBDD.appDataContext == null) {
            try {
                BBDD.appDataContext = new QdemosDataContext(ctx);
            } catch (Exception e) {
                Log.e("BBDD", "Error inicializando QdemosDataContext: " + e.getMessage());
            }
        }
        return BBDD.appDataContext;
    }

    public static Usuario quienSoy (Context ctx){
        try {
            return BBDD.getApplicationDataContext(ctx).usuarioDao.search(false, "Idgcm != ?", new String[] { "null" }, null, null, null, null, null).get(0);
        } catch (Exception e){
            return null;
        }
    }

    public static void crearUsuarioIfNotExist (Context ctx, String nombre, String idFB){
        try{
            // TODO: Rellenar el usuario correctamente con los datos de IDGCM y demás
            if (existo(ctx)){
                //TODO: Corroborar que el IDGCM es el correcto tanto en local como en el servidor
            } else {
                Usuario user = new Usuario(nombre, idFB, "1111");
                BBDD.getApplicationDataContext(ctx).usuarioDao.save(user);
            }
        } catch (Exception e){}
    }

    public static boolean existo(Context ctx){
        try {
            if (BBDD.getApplicationDataContext(ctx).usuarioDao.search(false, "Idgcm != ?", new String[] { "null" }, null, null, null, null, null).size() > 0)
                return true;
            else
                return false;
        } catch (Exception e){
            return false;
        }
    }

    public static boolean existeUsuario (Context ctx, String idFB){
        try {
            if (BBDD.getApplicationDataContext(ctx).usuarioDao.search(false, "Idfacebook = ?", new String[] { idFB }, null, null, null, null, null).size() > 0)
                return true;
            else
                return false;
        } catch (Exception e){
            return false;
        }
    }
}
