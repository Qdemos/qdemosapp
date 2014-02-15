package com.cusl.ull.qdemos.server;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.cusl.ull.qdemos.bbdd.utilities.Conversores;
import com.cusl.ull.qdemos.taskListeners.ResponseServer_actualizarQdada_TaskListener;
import com.cusl.ull.qdemos.taskListeners.ResponseServer_nuevaEleccion_TaskListener;
import com.cusl.ull.qdemos.taskListeners.ResponseServer_nuevaQdada_TaskListener;
import com.cusl.ull.qdemos.taskListeners.ResponseServer_nuevoUsuario_TaskListener;
import com.cusl.ull.qdemos.utilities.DatosQdada;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
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
        RequestSimpleResponse taskResquest = new RequestSimpleResponse();

        try {
            StringEntity body = new StringEntity(com.cusl.ull.qdemos.utilities.Utilities.getJSONServerFromEleccionQdada(idQdada, idFB, fechas).toString(), "UTF-8");
            HttpPost post = ServerConnection.getPost(activity.getResources().getString(R.string.ip_server), activity.getResources().getString(R.string.port_server), "nuevaEleccion", body);
            taskResquest.setParams(new ResponseServer_nuevaEleccion_TaskListener(activity, pd, idQdada, idFB, fechas), ServerConnection.getClient(), post);
            taskResquest.execute();
        } catch (Exception e){
            pd.dismiss();
        }
        return false;
    }

    public static void sincronizarBBDD(final Context ctx){
        if (!com.cusl.ull.qdemos.utilities.Utilities.haveInternet(ctx)){
            return;
        }
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                if (!BBDD.hayQueActualizar())
                    return null;
                // TODO: Mejorar la actualizacion de la variable de tiempo de ultima actualizacion con el servidor, ya que ahora no se tiene en cuenta si ha fallado algo durante esa actualizacion
                BBDD.ultimaSincronizacionConServidor = new Date();
                String msg = "";
                try {
                    List<Qdada> qdadas = BBDD.getApplicationDataContext(ctx).qdadaDao.search(false, null, null, null, null, null, null, null);
                    // TODO: Ver si hacer esto en una sola petición, que se le pase el id de todas las Qdadas, y devuelva la informacion todas juntas
                    for (Qdada qdada: qdadas){
                        obtenerDatosQdadaServer(ctx, qdada.getIdQdada());
                    }
                } catch (Exception ex) {}
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
    }

    public static void obtenerDatosQdadaServer(Context ctx, String idQdada){
        try {
            RequestSimpleResponse taskResquest = new RequestSimpleResponse();
            HttpGet get = ServerConnection.getGet(ctx.getResources().getString(R.string.ip_server), ctx.getResources().getString(R.string.port_server), "datosQdada/" + idQdada);
            taskResquest.setParams(new ResponseServer_actualizarQdada_TaskListener(ctx), ServerConnection.getClient(), get);
            taskResquest.execute();
        } catch (Exception e){
            Log.i("Fallo al Enviar", "No se ha podido enviar el usuario al servidor");
        }
    }
}
