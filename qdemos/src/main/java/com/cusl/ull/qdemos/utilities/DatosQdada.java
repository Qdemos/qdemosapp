package com.cusl.ull.qdemos.utilities;

import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.bbdd.models.UsuarioEleccion;
import com.facebook.model.GraphUser;
import com.mobandme.ada.annotations.TableField;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 15/01/14.
 */
public class DatosQdada {

    public static String titulo;
    public static String descripcion;
    public static Usuario creador;

    public static String direccion;
    public static Double latitud;
    public static Double longitud;
    public static List<Date> fechas = new ArrayList<Date>();
    public static Date limite;
    public static Boolean reinvitacion;

    public static List<GraphUser> invitados;

    public static void reset(){
        fechas = new ArrayList<Date>();
        titulo = null;
        descripcion = null;
        // TODO: Setear CREADOR al usuario actual mediante un quienSoy() o algo de eso
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

    public static void setNuevaFecha(int year, int month, int day, int hourOfDay, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hourOfDay, minute);
        fechas.add(calendar.getTime());
        Collections.sort(fechas, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });
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
}
