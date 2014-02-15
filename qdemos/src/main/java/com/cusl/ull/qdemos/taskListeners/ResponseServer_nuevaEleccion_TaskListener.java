package com.cusl.ull.qdemos.taskListeners;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import com.cusl.ull.qdemos.Home;
import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.cusl.ull.qdemos.interfaces.IStandardTaskListener;
import com.cusl.ull.qdemos.utilities.DatosQdada;
import com.cusl.ull.qdemos.utilities.EleccionFecha;

import java.util.Date;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ResponseServer_nuevaEleccion_TaskListener implements IStandardTaskListener {

    Activity activity;
    ProgressDialog pd;
    String idQdada, idFB;
    List<Date> fechas;

    public ResponseServer_nuevaEleccion_TaskListener(Activity activity, ProgressDialog pd, String idQdada, String idFB, List<Date> fechas) {
        this.activity = activity;
        this.pd = pd;
        this.idQdada = idQdada;
        this.idFB = idFB;
        this.fechas = fechas;
    }

    @Override
    public void taskComplete(Object result) {
        if (result != null){
            String responseServer = (String) result;
            if ((responseServer != null) && (responseServer.equals("ok"))){
                BBDD.updateEleccionLocal(activity, idQdada, idFB, fechas);

                EleccionFecha.reset();
                Intent intent = new Intent(activity, Home.class);
                // Para eliminar el historial de activities visitadas ya que volvemos al HOME y asi el boton ATRAS no tenga ningun comportamiento, se resetee.
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                pd.dismiss();
                activity.startActivity(intent);
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