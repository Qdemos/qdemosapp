package com.cusl.ull.qdemos.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.cusl.ull.qdemos.Home;
import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.bbdd.models.UsuarioEleccion;
import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.cusl.ull.qdemos.bbdd.utilities.Conversores;
import com.cusl.ull.qdemos.server.RequestSimpleResponse;
import com.cusl.ull.qdemos.server.ServerConnection;
import com.cusl.ull.qdemos.taskListeners.ResponseServer_nuevaQdada_TaskListener;
import com.facebook.model.GraphUser;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


/**
 * Created by Paco on 15/01/14.
 */
public class DatosQdada {

    private static String titulo;
    private static String descripcion;
    private static Usuario creador;

    private static String direccion;

    private static Double latitud;
    private static Double longitud;
    private static List<Date> fechas = new ArrayList<Date>();
    private static Date limite;
    private static Boolean reinvitacion;

    private static List<GraphUser> invitados = new ArrayList<GraphUser>();

    public static void reset(Context ctx){
        fechas = new ArrayList<Date>();
        titulo = null;
        descripcion = null;
        creador = com.cusl.ull.qdemos.bbdd.utilities.BBDD.quienSoy(ctx);
        latitud = null;
        longitud = null;
        direccion=null;
        limite = null;
        reinvitacion = null;
        invitados = new ArrayList<GraphUser>();
    }

    public static boolean isEmpty(){
        if (!fechas.isEmpty())
            return false;
        if ((titulo != null) && (!titulo.trim().isEmpty()))
            return false;
        if ((descripcion != null) && (!descripcion.trim().isEmpty()))
            return false;
        if ((direccion != null) && (!direccion.trim().isEmpty()))
            return false;
        if (!invitados.isEmpty())
            return false;
        return true;
    }

    public static List<GraphUser> getSelectedUsers() {
        return invitados;
    }

    public static void setSelectedUsers(List<GraphUser> selectedU) {
        invitados = selectedU;
    }

    public static boolean setNuevaFecha(int year, int month, int day, int hourOfDay, int minute){
        Calendar calendar = Calendar.getInstance();
        Calendar actual = Calendar.getInstance();
        calendar.set(year, month, day, hourOfDay, minute, 0);
        if (Utilities.containsDate(fechas, calendar.getTime())){
            return false;
        }
        fechas.add(calendar.getTime());
        Collections.sort(fechas, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });
        return true;
    }

    public static void setTitulo (String tit){
        titulo = tit;
    }

    public static void setDescripcion (String desc){
        descripcion = desc;
    }

    public static void setCoordenadas (Double lat, Double lng){
        latitud = lat;
        longitud = lng;
    }

    public static void setLimite (Date lim){
        limite = lim;
    }

    public static void setReinvitacion (Boolean reinvit){
        reinvitacion = reinvit;
    }

    public static void setDireccion(String direccion) {
        DatosQdada.direccion = direccion;
    }

    public static String getTitulo() {
        return titulo;
    }

    public static String getDescripcion() {
        return descripcion;
    }

    public static Usuario getCreador() {
        return creador;
    }

    public static String getDireccion() {
        return direccion;
    }

    public static Double getLatitud() {
        return latitud;
    }

    public static Double getLongitud() {
        return longitud;
    }

    public static List<Date> getFechas() {
        return fechas;
    }

    public static Date getLimite() {
        return limite;
    }

    public static Boolean getReinvitacion() {
        return reinvitacion;
    }

    public static List<GraphUser> getInvitados() {
        return invitados;
    }

    public static boolean validarFechas (Activity activity){
        if ((DatosQdada.getFechas() != null) && (!DatosQdada.getFechas().isEmpty()))
            return true;
        Crouton.makeText(activity, R.string.validar_fechas, Style.ALERT).show();
        return false;
    }

    public static boolean validarInvitados (Activity activity){
        if ((DatosQdada.getInvitados() != null) && (!DatosQdada.getInvitados().isEmpty()))
            return true;
        Crouton.makeText(activity, R.string.validar_invitados, Style.ALERT).show();
        return false;
    }

    public static boolean validarInfo (Activity activity){
        if ((DatosQdada.getTitulo() == null) || (DatosQdada.getTitulo().trim().isEmpty())){
            Crouton.makeText(activity, R.string.validar_titulo, Style.ALERT).show();
            return false;
        } /*else if ((DatosQdada.getLatitud() == null) || (DatosQdada.getLongitud() == null) || (DatosQdada.getDireccion() == null) || (DatosQdada.getDireccion().trim().isEmpty())){
            Crouton.makeText(activity, R.string.validar_direccion, Style.ALERT).show();
            return false;
        }*/
        return true;
    }

    public static boolean guardarEnServidor(Activity activity, ProgressDialog pd){
        RequestSimpleResponse taskResquest = new RequestSimpleResponse();

        try {
            Qdada qdada = Conversores.fromDatosQdadaToQdada(activity, null);
            StringEntity body = new StringEntity(Utilities.getJSONServerFromQdada(qdada, DatosQdada.getFechas()).toString(), "UTF-8");
            HttpPost post = ServerConnection.getPost(activity.getResources().getString(R.string.ip_server), activity.getResources().getString(R.string.port_server), "nuevaQdada/", body);
            taskResquest.setParams(new ResponseServer_nuevaQdada_TaskListener(activity, pd), ServerConnection.getClient(), post);
            taskResquest.execute();
        } catch (Exception e){
            pd.dismiss();
        }
        return false;
    }

    public static void guardarEnLocal (Activity activity, String idserver, ProgressDialog pd){
        try {
            Qdada qdada = Conversores.fromDatosQdadaToQdada(activity, idserver);
            if (qdada == null){
                pd.dismiss();
                return;
            }
            qdada.setStatus(Entity.STATUS_NEW);
            com.cusl.ull.qdemos.bbdd.utilities.BBDD.getApplicationDataContext(activity).qdadaDao.add(qdada);
            com.cusl.ull.qdemos.bbdd.utilities.BBDD.getApplicationDataContext(activity).qdadaDao.save();

            for (Date date: DatosQdada.getFechas()){
                UsuarioEleccion ue = new UsuarioEleccion(activity, DatosQdada.getCreador().getIdfacebook(), date, idserver);
                ue.setStatus(Entity.STATUS_NEW);
                BBDD.getApplicationDataContext(activity).participanteDao.add(ue);
                BBDD.getApplicationDataContext(activity).participanteDao.save();
            }

            BBDD.setFechas(activity, DatosQdada.getFechas(), idserver);

            // TODO: Pasar algun Bundle para mostrar un Crouton de EXITO en el fragment del HOME
            Intent intent = new Intent(activity, Home.class);
            // Para eliminar el historial de activities visitadas ya que volvemos al HOME y asi el boton ATRAS no tenga ningun comportamiento, se resetee.
            DatosQdada.reset(activity);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pd.dismiss();
            activity.startActivity(intent);
            activity.finish();

        } catch (Exception e) {
            pd.dismiss();
            Crouton.makeText(activity, R.string.error_bbdd, Style.ALERT).show();
        }
    }

    public static void setLongitud(Double longitud) {
        DatosQdada.longitud = longitud;
    }

    public static void setLatitud(Double latitud) {
        DatosQdada.latitud = latitud;
    }


}
