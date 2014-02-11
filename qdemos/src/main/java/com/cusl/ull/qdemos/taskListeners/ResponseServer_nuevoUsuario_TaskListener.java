package com.cusl.ull.qdemos.taskListeners;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.interfaces.IStandardTaskListener;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ResponseServer_nuevoUsuario_TaskListener implements IStandardTaskListener {

    Context ctx;
    static final String TAG = "Respuesta Servidor";

    public ResponseServer_nuevoUsuario_TaskListener(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void taskComplete(Object result) {
        if (result != null){
            String responseServer = (String) result;
            if ((result != null) && (result.equals("ok"))){
                Log.i(TAG, "Usuario Creado Correctamente");
            } else {
                Crouton.makeText((Activity) ctx, R.string.error_bbdd, Style.ALERT).show();
            }
        } else {
            Crouton.makeText((Activity) ctx, R.string.error_bbdd, Style.ALERT).show();
        }
    }
}