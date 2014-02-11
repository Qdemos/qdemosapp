package com.cusl.ull.qdemos.server;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

import org.apache.http.client.methods.HttpUriRequest;

import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.os.AsyncTask;
import android.util.Log;

import com.cusl.ull.qdemos.interfaces.IStandardTaskListener;

/*
 * Clase encargada de lanzar la peticion a un servicio web y esperar como respuesta una Lista de JSON (ArrayJSON)
 */

public class RequestArrayJSONResponse extends AsyncTask<String, Void, Boolean>{

    private IStandardTaskListener listener;
    private HttpClient httpClient;
    private HttpUriRequest method;
    private JSONArray responseServer;

    @Override
    // Se ejecuta en segundo plano
    protected Boolean doInBackground(String... args) {
        try {
            responseServer=null;
            HttpResponse resp = httpClient.execute(method);
            try {
                responseServer = new JSONArray(EntityUtils.toString(resp.getEntity(), "UTF-8"));
            } catch (Exception e) {}
            return true;
        } catch (Exception e) {
            Log.e("ServicioRest","Error en los preparativos!", e);
            return false;
        }
    }

    // Este metodo se ejecuta cuando el 'doInBackground' ha terminado, y recibe como parametro
    // lo devuelto en el 'doInBackground'
    @Override
    protected void onPostExecute(Boolean result){
        if (listener != null) {
            listener.taskComplete(responseServer);
        }
    }

    public void setParams(IStandardTaskListener listener, HttpClient httpClient, HttpUriRequest method) {
        this.listener = listener;
        this.httpClient = httpClient;
        this.method = method;
    }

}
