package com.cusl.ull.qdemos.server;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.utilities.Conversores;
import com.cusl.ull.qdemos.taskListeners.ResponseServer_nuevaEleccion_TaskListener;
import com.cusl.ull.qdemos.taskListeners.ResponseServer_nuevaQdada_TaskListener;
import com.cusl.ull.qdemos.taskListeners.ResponseServer_nuevoUsuario_TaskListener;
import com.cusl.ull.qdemos.utilities.DatosQdada;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 11/02/14.
 */
public class Utilities {

    public static void crearUsuario (Context ctx, String nombre, String idFB, String idGCM, boolean nuevo){
        if (!com.cusl.ull.qdemos.utilities.Utilities.haveInternet(ctx))
            return;
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
        if (!com.cusl.ull.qdemos.utilities.Utilities.haveInternet(activity))
            return false;
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
        if (!com.cusl.ull.qdemos.utilities.Utilities.haveInternet(activity))
            return false;
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
}
