package com.cusl.ull.qdemos.taskListeners;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.cusl.ull.qdemos.interfaces.IStandardTaskListener;
import com.mobandme.ada.Entity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResponseServer_actualizarQdada_TaskListener implements IStandardTaskListener {

    Context ctx;
    static final String TAG = "Respuesta Servidor";

    public ResponseServer_actualizarQdada_TaskListener(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void taskComplete(Object result) {
        try {
            if (result != null){
                String responseServer = (String) result;
                if ((responseServer != null) && (!responseServer.equals("err")) && (!responseServer.equals("err_no_exist"))){
                    JSONObject datos = (JSONObject) result;
                    String idQdada = datos.getString("idqdada");
                    JSONArray elecciones = datos.getJSONArray("usuarioselecciones");
                    for (int i=0; i < elecciones.length(); i++){
                        JSONObject eleccion = elecciones.getJSONObject(i);
                        String idfacebook = eleccion.getString("idfacebook");
                        BBDD.crearUsuarioByIdFacebook(ctx, idfacebook);
                        List<Date> fechas = new ArrayList<Date>();
                        // TODO: Recuperar fechas de eleccion que estan en una Lista, ver como se pueden coger
                        BBDD.updateEleccionLocal(ctx, idQdada, idfacebook, fechas);
                    }
                } else {
                    Toast.makeText(ctx, R.string.error_bbdd+" 1", Toast.LENGTH_SHORT);
                }
            } else {
                Toast.makeText(ctx, R.string.error_bbdd+" 2", Toast.LENGTH_SHORT);
            }
        } catch (Exception e){
            Toast.makeText(ctx, R.string.error_bbdd+" 0", Toast.LENGTH_SHORT);
        }
    }
}