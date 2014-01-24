package com.cusl.ull.qdemos.utilities;

import android.app.Activity;
import android.content.Context;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.bbdd.models.Fecha;
import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.bbdd.models.UsuarioEleccion;
import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.cusl.ull.qdemos.bbdd.utilities.Conversores;
import com.facebook.model.GraphUser;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;

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
    private static List<Fecha> fechas = new ArrayList<Fecha>();
    private static Date limite;
    private static Boolean reinvitacion;

    private static List<GraphUser> invitados;

    public static void reset(Context ctx){
        fechas = new ArrayList<Fecha>();
        titulo = null;
        descripcion = null;
        creador = BBDD.quienSoy(ctx);
        latitud = null;
        longitud = null;
        direccion=null;
        limite = null;
        reinvitacion = null;
        invitados = new ArrayList<GraphUser>();
    }

    public static List<GraphUser> getSelectedUsers() {
        return invitados;
    }

    public static void setSelectedUsers(List<GraphUser> selectedU) {
        invitados = selectedU;
    }

    public static boolean setNuevaFecha(int year, int month, int day, int hourOfDay, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hourOfDay, minute, 0);
        Fecha nuevFecha = new Fecha(calendar.getTime());
        if (fechas.contains(nuevFecha)){
            return false;
        }
        fechas.add(nuevFecha);
        Collections.sort(fechas, new Comparator<Fecha>() {
            public int compare(Fecha o1, Fecha o2) {
                return o1.getFecha().compareTo(o2.getFecha());
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

    public static List<Fecha> getFechas() {
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
        } else if (/*(DatosQdada.getLatitud() == null) || (DatosQdada.getLongitud() == null) ||*/ (DatosQdada.getDireccion() == null) || (DatosQdada.getDireccion().trim().isEmpty())){
            // TODO: Descomentar lo de LAT y LNG
            Crouton.makeText(activity, R.string.validar_direccion, Style.ALERT).show();
            return false;
        }
        return true;
    }

    public static boolean guardarBBDD (Context ctx){
        try {
            Qdada qdada = Conversores.fromDatosQdadaToQdada(ctx);
            if (qdada == null)
                return false;
            BBDD.appDataContext.qdadaDao.save(qdada);
            UsuarioEleccion ue = qdada.getParticipantes().get(0);
            ue.setIdqdada(qdada.getID());
            ue.setStatus(Entity.STATUS_UPDATED);
            BBDD.appDataContext.qdadaDao.save();
            return true;
        } catch (AdaFrameworkException e) {
            return false;
        }
    }
}
