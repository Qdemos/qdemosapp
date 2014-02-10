package com.cusl.ull.qdemos.taskListeners;

import android.app.Activity;
import android.app.ProgressDialog;

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
            if ((result != null) && (!result.equals("err"))){
                DatosQdada.guardarEnLocal(activity, responseServer, pd);
            } else {
                pd.dismiss();
                Crouton.makeText(activity, R.string.error_bbdd, Style.ALERT).show();
            }
        } else {
            pd.dismiss();
            Crouton.makeText(activity, R.string.error_bbdd, Style.ALERT).show();
        }
    }
}