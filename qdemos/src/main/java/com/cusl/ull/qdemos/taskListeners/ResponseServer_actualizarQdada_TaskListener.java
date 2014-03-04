package com.cusl.ull.qdemos.taskListeners;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.cusl.ull.qdemos.interfaces.IStandardTaskListener;
import com.mobandme.ada.Entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ResponseServer_actualizarQdada_TaskListener implements IStandardTaskListener {

    Context ctx;
    static final String TAG = "Respuesta Servidor";
    boolean onlyOne;

    public ResponseServer_actualizarQdada_TaskListener(Context ctx, boolean onlyOne) {
        this.ctx = ctx;
        this.onlyOne = onlyOne;
    }

    @Override
    public void taskComplete(Object result) {
        try {
            if (result != null){
                String responseServer = (String) result;
                if ((responseServer != null) && (!responseServer.equals("err")) && (!responseServer.equals("err_no_exist"))){
                    if (onlyOne){
                        JSONObject datos = new JSONObject(responseServer);
                        updateLocalOne(datos);
                    } else {
                        JSONArray arrayDatos = new JSONArray(responseServer);
                        JSONObject datos;
                        for (int i=0; i<arrayDatos.length(); i++){
                            datos = new JSONObject(arrayDatos.getString(i));
                            updateLocalAll(datos);
                        }
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

    public void updateLocalOne (JSONObject datos) throws Exception {
        String idQdada = datos.getString("idqdada");
        JSONArray elecciones = datos.getJSONArray("usuarioselecciones");
        for (int i=0; i < elecciones.length(); i++){
            JSONObject eleccion = elecciones.getJSONObject(i);
            String idfacebook = eleccion.getString("idfacebook");
            BBDD.crearUsuarioByIdFacebook(ctx, idfacebook);
            List<Date> fechas = new ArrayList<Date>();
            JSONArray fechasJSON = eleccion.getJSONArray("fechas");
            for (int j=0; j< fechasJSON.length(); j++){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(fechasJSON.getString(j)));
                Date date = cal.getTime();
                fechas.add(date);
            }
            BBDD.updateEleccionLocal(ctx, idQdada, idfacebook, fechas);
        }
    }

    public void updateLocalAll (JSONObject datos) throws Exception {
        String idQdada = datos.getString("idqdada");
        JSONArray elecciones = new JSONArray(datos.getString("usuarioselecciones"));
        for (int i=0; i < elecciones.length(); i++){
            JSONObject eleccion = elecciones.getJSONObject(i);
            String idfacebook = eleccion.getString("idfacebook");
            BBDD.crearUsuarioByIdFacebook(ctx, idfacebook);
            List<Date> fechas = new ArrayList<Date>();
            JSONArray fechasJSON = eleccion.getJSONArray("fechas");
            for (int j=0; j< fechasJSON.length(); j++){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(fechasJSON.getString(j)));
                Date date = cal.getTime();
                fechas.add(date);
            }
            BBDD.updateEleccionLocal(ctx, idQdada, idfacebook, fechas);
        }
    }
}