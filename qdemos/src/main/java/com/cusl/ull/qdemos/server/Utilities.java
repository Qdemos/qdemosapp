package com.cusl.ull.qdemos.server;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.cusl.ull.qdemos.bbdd.utilities.Conversores;
import com.cusl.ull.qdemos.taskListeners.ResponseServer_actualizarQdada_TaskListener;
import com.cusl.ull.qdemos.taskListeners.ResponseServer_nuevaEleccion_TaskListener;
import com.cusl.ull.qdemos.taskListeners.ResponseServer_nuevaQdada_TaskListener;
import com.cusl.ull.qdemos.taskListeners.ResponseServer_nuevoUsuario_TaskListener;
import com.cusl.ull.qdemos.utilities.DatosQdada;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mobandme.ada.Entity;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 11/02/14.
 */
public class Utilities {

    public static void crearUsuario (Context ctx, String nombre, String idFB, String idGCM, boolean nuevo){
        if (!com.cusl.ull.qdemos.utilities.Utilities.haveInternet(ctx)){
            Toast.makeText(ctx, ctx.getString(R.string.conexion), Toast.LENGTH_LONG);
            return;
        }
        RequestSimpleResponse taskResquest = new RequestSimpleResponse();

        try {
            StringEntity body = new StringEntity(com.cusl.ull.qdemos.utilities.Utilities.getJSONServerFromUsuario(nombre, idFB, idGCM).toString(), "UTF-8");
            HttpPost post = ServerConnection.getPost(ctx.getResources().getString(R.string.ip_server), ctx.getResources().getString(R.string.port_server), "nuevoUsuario", body);
            taskResquest.setParams(new ResponseServer_nuevoUsuario_TaskListener(ctx, nombre, idFB, idGCM, nuevo), ServerConnection.getClient(), post);
            taskResquest.execute();
        } catch (Exception e){
            Log.i("Fallo al Enviar", "No se ha podido enviar el usuario al servidor");
        }
    }

    public static boolean crearQdada(Activity activity, ProgressDialog pd){
        if (!com.cusl.ull.qdemos.utilities.Utilities.haveInternet(activity)){
            Toast.makeText(activity, activity.getString(R.string.conexion), Toast.LENGTH_LONG);
            return false;
        }
        RequestSimpleResponse taskResquest = new RequestSimpleResponse();

        try {
            Qdada qdada = Conversores.fromDatosQdadaToQdada(activity, null);
            StringEntity body = new StringEntity(com.cusl.ull.qdemos.utilities.Utilities.getJSONServerFromQdada(qdada, DatosQdada.getFechas()).toString(), "UTF-8");
            HttpPost post = ServerConnection.getPost(activity.getResources().getString(R.string.ip_server), activity.getResources().getString(R.string.port_server), "nuevaQdada", body);
            taskResquest.setParams(new ResponseServer_nuevaQdada_TaskListener(activity, pd), ServerConnection.getClient(), post);
            taskResquest.execute();
        } catch (Exception e){
            pd.dismiss();
        }
        return false;
    }

    public static boolean crearEleccionQdada(Activity activity, ProgressDialog pd, String idQdada, String idFB, List<Date> fechas){
        if (!com.cusl.ull.qdemos.utilities.Utilities.haveInternet(activity)){
            Toast.makeText(activity, activity.getString(R.string.conexion), Toast.LENGTH_LONG);
            return false;
        }

        try {
            RequestSimpleResponse taskResquest = new RequestSimpleResponse();
            StringEntity body = new StringEntity(com.cusl.ull.qdemos.utilities.Utilities.getJSONServerFromEleccionQdada(idQdada, idFB, fechas).toString(), "UTF-8");
            HttpPost post = ServerConnection.getPost(activity.getResources().getString(R.string.ip_server), activity.getResources().getString(R.string.port_server), "nuevaEleccion", body);
            taskResquest.setParams(new ResponseServer_nuevaEleccion_TaskListener(activity, pd, idQdada, idFB, fechas), ServerConnection.getClient(), post);
            taskResquest.execute();
        } catch (Exception e){
            pd.dismiss();
        }
        return false;
    }

    // Funcion que se utiliza para conectarse con el servidor y ver si hay que actualizar la BBDD Local
    public static void sincronizarBBDD(final Context ctx){
        if (!com.cusl.ull.qdemos.utilities.Utilities.haveInternet(ctx)){
            return;
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                if (!BBDD.hayQueActualizar()){
                   return null;
                }
                // TODO: Mejorar la actualizacion de la variable de tiempo de ultima actualizacion con el servidor, ya que ahora no se tiene en cuenta si ha fallado algo durante esa actualizacion
                // TODO: ...Hacer esta mejora descrita en la linea anterior: Esto sería mejor hacerlo cuando alguien notifique al servidor que ha cambiado una Qdada. De manera que se envie una notificación
                // TODO: ...PUSH a todos los participantes para avisarlos de que deben actualizar
                BBDD.ultimaSincronizacionConServidor = new Date();
                // Esto es para pedir al servidor la info de las Qdadas que tenemos almacenadas en local
                // TODO: ¿Seria conveniente pedir tambien las que no tenemos almacenadas en local? ¿Por eso de cuando funcione en MultiDispositivo? Algo así de que a través de un IDFacebook te de todas las Qdadas
                String msg = "";
                try {
                    List<Qdada> qdadas = BBDD.getApplicationDataContext(ctx).qdadaDao.search(false, null, null, null, null, null, null, null);
                    ArrayList<String> ids = new ArrayList<String>();
                    for (Qdada qdada: qdadas){
                        ids.add(qdada.getIdQdada());
                    }
                    obtenerDatosQdadasServer(ctx, ids);
                } catch (Exception ex) {
                    System.out.println("Error al recuperar Qdadas");
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
    }

    // Función encargada de pedir un conjunto de Qdadas al servidor para ver si hay cambios respecto a los datos locales
    public static void obtenerDatosQdadasServer(Context ctx, ArrayList<String> idsQdada){
        try {
            if (idsQdada.isEmpty()){
                Log.i("Fallo al Enviar", "No se ha podido enviar los idsQdadas al servidor");
            } else {
                JSONObject data = new JSONObject();
                JSONArray idsJSON = new JSONArray(idsQdada);
                data.put("ids", idsJSON.toString());

                StringEntity body = new StringEntity(data.toString(), "UTF-8");

                RequestSimpleResponse taskResquest = new RequestSimpleResponse();
                // Hacemos una petición de tipo PUT, por si en un futuro se piensa que a la vez de recoger los datos del servidor, se quisiera corroborar que hay cambios en local que no están reflejados en el servidor
                // por si el usuario cambio algo y no se actualizó correctamente en el servidor por fallos de conexión o similares. Esto es una MEJORA FUTURA NO IMPLEMENTADA TODAVIA
                HttpPut put = ServerConnection.getPut(ctx.getResources().getString(R.string.ip_server), ctx.getResources().getString(R.string.port_server), "datosQdadas", body);
                taskResquest.setParams(new ResponseServer_actualizarQdada_TaskListener(ctx, false), ServerConnection.getClient(), put);
                taskResquest.execute();
            }
        } catch (Exception e){
            Log.i("Fallo al Enviar", "No se ha podido enviar los idsQdadas al servidor");
        }
    }

    // Función encargada de pedir una Qdada al servidor para ver si hay cambios respecto a los daots locales
    public static void obtenerDatosQdadaServer(Context ctx, String idQdada){
        try {
            RequestSimpleResponse taskResquest = new RequestSimpleResponse();
            HttpGet get = ServerConnection.getGet(ctx.getResources().getString(R.string.ip_server), ctx.getResources().getString(R.string.port_server), "datosQdada/" + idQdada);
            taskResquest.setParams(new ResponseServer_actualizarQdada_TaskListener(ctx, true), ServerConnection.getClient(), get);
            taskResquest.execute();
        } catch (Exception e){
            Log.i("Fallo al Enviar", "No se ha podido enviar el idQdada servidor");
        }
    }
}
