package com.cusl.ull.qdemos.taskListeners;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.cusl.ull.qdemos.interfaces.IStandardTaskListener;
import com.mobandme.ada.Entity;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ResponseServer_nuevoUsuario_TaskListener implements IStandardTaskListener {

    Context ctx;
    static final String TAG = "Respuesta Servidor";
    String nombre, idFB, idGcm;
    boolean nuevo;

    public ResponseServer_nuevoUsuario_TaskListener(Context ctx, String nombre, String idFB, String idGcm, boolean nuevo) {
        this.ctx = ctx;
        this.nombre = nombre;
        this.idFB = idFB;
        this.idGcm = idGcm;
        this.nuevo = nuevo;
    }

    @Override
    public void taskComplete(Object result) {
        if (result != null){
            String responseServer = (String) result;
            if ((responseServer != null) && (responseServer.equals("ok"))){
                if (nuevo){
                    if (!BBDD.existo(ctx)) {
                        Usuario user = new Usuario(nombre, idFB, idGcm);
                        try {
                            user.setStatus(Entity.STATUS_NEW);
                            BBDD.getApplicationDataContext(ctx).usuarioDao.add(user);
                            BBDD.getApplicationDataContext(ctx).usuarioDao.save();
                            Log.i(TAG, "Usuario Creado Correctamente");
                        } catch (Exception e){}
                    }
                } else {
                    try {
                        Usuario usuario = BBDD.quienSoy(ctx);
                        usuario.setIdgcm(idGcm);
                        usuario.setStatus(Entity.STATUS_UPDATED);
                        BBDD.getApplicationDataContext(ctx).usuarioDao.save(usuario);
                        Log.i(TAG, "Usuario Actualizado Correctamente");
                    } catch (Exception e){}
                }
            } else {
                Toast.makeText(ctx, R.string.error_bbdd, Toast.LENGTH_SHORT);
            }
        } else {
            Toast.makeText(ctx, R.string.error_bbdd, Toast.LENGTH_SHORT);
        }
    }
}