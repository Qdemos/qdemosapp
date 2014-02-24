package com.cusl.ull.qdemos.taskListeners;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.interfaces.IStandardTaskListener;
import com.cusl.ull.qdemos.utilities.DatosQdada;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ResponseServer_nuevaQdada_TaskListener implements IStandardTaskListener {

    Activity activity;
    ProgressDialog pd;

    public ResponseServer_nuevaQdada_TaskListener(Activity activity, ProgressDialog pd) {
        this.activity = activity;
        this.pd = pd;
    }

    @Override
    public void taskComplete(Object result) {
        if (result != null){
            String responseServer = (String) result;
            if ((responseServer != null) && (!responseServer.equals("err"))){
                // Ojo, es importante limpiar las " porque el servidor nos devuelve el id entre comillas, estas incluidas.
                DatosQdada.guardarEnLocal(activity, responseServer.replace("\"", ""), pd);
            } else {
                pd.dismiss();
                Toast.makeText(activity, R.string.error_bbdd, Toast.LENGTH_SHORT);
            }
        } else {
            pd.dismiss();
            Toast.makeText(activity, R.string.error_bbdd, Toast.LENGTH_SHORT);
        }
    }
}