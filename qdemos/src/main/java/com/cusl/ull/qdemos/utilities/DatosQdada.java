package com.cusl.ull.qdemos.utilities;

import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.bbdd.models.UsuarioEleccion;
import com.facebook.model.GraphUser;
import com.mobandme.ada.annotations.TableField;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 15/01/14.
 */
public class DatosQdada {

    public static List<Usuario> invitados = new ArrayList<Usuario>();
    public static String titulo;
    public static String descripcion;
    public static Usuario creador;
    public static Double latitud;
    public static Double longitud;
    public static List<Date> fechas = new ArrayList<Date>();
    public static Date limite;
    public static Boolean reinvitacion;

    public static List<GraphUser> selectedUsers;

    public static void reset(){
        fechas = new ArrayList<Date>();
        titulo = null;
        descripcion = null;
        // TODO: Setear CREADOR al usuario actual mediante un quienSoy() o algo de eso
        latitud = null;
        longitud = null;
        limite = null;
        reinvitacion = null;
        selectedUsers = new ArrayList<GraphUser>();
    }

    public static List<GraphUser> getSelectedUsers() {
        return selectedUsers;
    }

    public static void setSelectedUsers(List<GraphUser> selectedU) {
        selectedUsers = selectedU;
    }

    public static void setNuevaFecha(int year, int month, int day, int hourOfDay, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hourOfDay, minute);
        fechas.add(calendar.getTime());
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

}
