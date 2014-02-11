package com.cusl.ull.qdemos.server;

import android.content.Context;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.taskListeners.ResponseServer_nuevoUsuario_TaskListener;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

/**
 * Created by Paco on 11/02/14.
 */
public class Utilities {

    public static void crearUsuario (Context ctx, String nombre, String idFB, String idGCM){
        RequestSimpleResponse taskResquest = new RequestSimpleResponse();

        try {
            StringEntity body = new StringEntity(com.cusl.ull.qdemos.utilities.Utilities.getJSONServerFromUsuario(nombre, idFB, idGCM).toString(), "UTF-8");
            HttpPost post = ServerConnection.getPost(ctx.getResources().getString(R.string.ip_server), ctx.getResources().getString(R.string.port_server), "nuevoUsuario", body);
            taskResquest.setParams(new ResponseServer_nuevoUsuario_TaskListener(ctx), ServerConnection.getClient(), post);
            taskResquest.execute();
        } catch (Exception e){
            System.out.println(e);
        }
    }
}
