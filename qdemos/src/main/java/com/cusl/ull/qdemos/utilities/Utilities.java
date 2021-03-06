package com.cusl.ull.qdemos.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.bbdd.models.UsuarioEleccion;
import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.cusl.ull.qdemos.bbdd.utilities.Conversores;
import com.mobandme.ada.Entity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 17/01/14.
 */
public class Utilities {

    public static String getCamelCase(String init){
        if (init==null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.equals("")) {
                ret.append(Character.toUpperCase(word.charAt(0)));
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length()==init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }

    public static boolean haveInternet(Context ctx) {

        NetworkInfo info = (NetworkInfo) ((ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null || !info.isConnected()) {
            return false;
        }
        if (info.isRoaming()) {
            // here is the roaming option you can change it if you want to
            // disable internet while roaming, just return false
        }
        if (isConnectionFast(info.getType(), info.getSubtype()))
            return true;
        return false;
    }

    public static boolean isConnectionFast(int type, int subType){
        if(type== ConnectivityManager.TYPE_WIFI){
            return true;
        }else if(type==ConnectivityManager.TYPE_MOBILE){
            switch(subType){
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
            /*
             * Above API level 7, make sure to set android:targetSdkVersion
             * to appropriate level to use these
             */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        }else{
            return false;
        }
    }

    public static List<Usuario> ordenarInvitados (List<Usuario> invitados, List<Usuario> participantes){
        List<Usuario> ordenado = new ArrayList<Usuario>();
        List<Usuario> noparticipantesU = new ArrayList<Usuario>();
        for (Usuario user: invitados){
            if (participantes.contains(user)){
                ordenado.add(user);
            } else {
                noparticipantesU.add(user);
            }
        }
        ordenado.addAll(noparticipantesU);
        return ordenado;
    }

    public static Boolean isParticipante (Context ctx, Qdada qdada, String idFacebook){
        try {
            List<UsuarioEleccion> elecciones = BBDD.getApplicationDataContext(ctx).participanteDao.search(false, "Idqdada = ? and Idfacebook = ?", new String[]{qdada.getIdQdada().toString(), idFacebook}, null, null, null, null, null);
            if ((elecciones == null) || elecciones.isEmpty())
                return null;
            for (UsuarioEleccion ue: elecciones){
                if (ue.getFecha().equals(qdada.getFechaGanadora()))
                    return true;
            }
        } catch (Exception e){}
        return false;
    }

    public static boolean containsDate (List<Date> lista, Date fecha){
        for (Date date: lista){
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date);
            Calendar c2  = Calendar.getInstance();
            c2.setTime(fecha);
            if (   (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
                    && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
                    && (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH))
                    && (c1.get(Calendar.HOUR) == c2.get(Calendar.HOUR))
                    && (c1.get(Calendar.MINUTE) == c2.get(Calendar.MINUTE))
                    ){
                return true;
            }
        }
        return false;
    }

    public static boolean containsUsuario (List<Usuario> lista, String idFacebook){
        for (Usuario user: lista){;
            if (user.getIdfacebook().equals(idFacebook)){
                return true;
            }
        }
        return false;
    }

    public static boolean hayDatosSinGuardar(Context ctx){
        if (!DatosQdada.isEmpty() || (!EleccionFecha.isEmpty())){
            return true;
        } else {
            return false;
        }
    }

    // esta funcion se encarga de comprobar si una fecha esta vigente o ya paso, para no permitir crear qdadas con fechas anteriores a cuando se crea
    public static boolean isActual(int year, int month, int day, int hourOfDay, int minute){
        Calendar fechaPropuesta = Calendar.getInstance();
        Calendar actual = Calendar.getInstance();//cogemos el instante actual
        fechaPropuesta.set(year, month, day, hourOfDay, minute, 0);
        if (fechaPropuesta.before(actual)){  // Si la fechaPropuesta es anterior a la actual, no dejamos meter esa fecha, ya que ya pasó
            return false;
        }
        return true;
    }

    // Nos devuelve un objeto de tipo Qdada listo para pasar (en JSON) a la peticion del web service del servidor
    public static JSONObject getJSONServerFromQdada(Qdada qdada, List<Date> fechas){
        JSONObject data = new JSONObject();

        try {

            ArrayList<String> listFechas = new ArrayList<String>();
            for (Date fecha: fechas){
                listFechas.add(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(fecha));
            }
            JSONArray fechasJSON = new JSONArray(listFechas);

            ArrayList<String> listInvitados = new ArrayList<String>();
            ArrayList<String> listInvitadosNombre = new ArrayList<String>();
            for (Usuario user: qdada.getInvitados()){
                listInvitados.add(user.getIdfacebook());
                listInvitadosNombre.add(user.getNombre());
            }
            JSONArray invitadosJSON = new JSONArray(listInvitados);
            JSONArray invitadosNombreJSON = new JSONArray(listInvitadosNombre);

            data.put("titulo", qdada.getTitulo());
            data.put("descripcion", qdada.getDescripcion());
            data.put("creador", qdada.getCreador().getIdfacebook());
            data.put("invitados", invitadosJSON.toString());
            data.put("invitadosnombre", invitadosNombreJSON.toString());
            data.put("fechas", fechasJSON.toString());
            data.put("latitud", qdada.getLatitud());
            data.put("longitud", qdada.getLongitud());
            data.put("direccion", qdada.getDireccion());
            data.put("fecha", new SimpleDateFormat("dd-MM-yyyy HH:mm").format(qdada.getFechaGanadora()));
            data.put("reinvitacion", qdada.getReinvitacion());
        } catch (Exception e){}
        return data;
    }

    // Nos devuelve un objeto de tipo Usuario listo para pasar (en JSON) a la peticion del web service del servidor
    public static JSONObject getJSONServerFromUsuario(String nombre, String idFB, String idGCM){
        JSONObject data = new JSONObject();
        try {
            data.put("nombre", nombre);
            data.put("idfacebook", idFB);
            data.put("idgcm", idGCM);
        } catch (Exception e){}
        return data;
    }

    // Nos devuelve un objeto de tipo EleccionQdada listo para pasar (en JSON) a la peticion del web service del servidor
    public static JSONObject getJSONServerFromEleccionQdada(String idQdada, String idFB, List<Date> fechas){
        JSONObject data = new JSONObject();
        try {
            ArrayList<String> listFechas = new ArrayList<String>();
            for (Date fecha: fechas){
                listFechas.add(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(fecha));
            }
            JSONArray fechasJSON = new JSONArray(listFechas);
            data.put("fechas", fechasJSON.toString());
            data.put("idfacebook", idFB);
            data.put("idqdada", idQdada);
        } catch (Exception e){}
        return data;
    }

    public static Date dateStringZTToDate (String zt){
        Date ret = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(zt));
            ret = cal.getTime();
        } catch (Exception e){}
        return ret;
    }

    // Funcion que recupera un nombre a traves de un identificador. Esto es debido a que el servidor envia esta informacion de la siguiente maner: "nombre;id", es un string.
    public static String getNombreFromJSONServer (Context ctx, JSONArray nombres_ids, String id){
        try {
            for (int i=0; i< nombres_ids.length(); i++){
                String nombreid = nombres_ids.getString(i);
                String nombre = nombreid.split(";")[0];
                String ids = nombreid.split(";")[1];
                if (ids.equals(id)){
                    return nombre;
                }
            }
        } catch (Exception e){
            System.out.println("Fallo al recuperar el nombre de usuario");
        }
        return ctx.getString(R.string.no_disponible);
    }

    // Función encargada de convertir una Notificación Push en la información correspondiente que se almacene en la BBDD Local
    public static Qdada notificacionToBBDD (Context ctx, String notificacion){
        try {
            JSONObject datos = new JSONObject(notificacion);
            JSONArray nombresJSON = datos.getJSONArray("nombreinvitados");
            Usuario creador = BBDD.crearUsuarioIfNotExistOnlyLocal(ctx, datos.getString("idcreador"), getNombreFromJSONServer(ctx, nombresJSON, datos.getString("idcreador")));
            List<Usuario> invitados = new ArrayList<Usuario>();
            JSONArray invitadosJSON = datos.getJSONArray("invitados");
            for (int j=0; j< invitadosJSON.length(); j++){
               Usuario invitado = BBDD.crearUsuarioIfNotExistOnlyLocal(ctx, invitadosJSON.getString(j), getNombreFromJSONServer(ctx, nombresJSON, invitadosJSON.getString(j)));
               if (invitado != null){
                  invitados.add(invitado);
               }
            }
            Date limite = Utilities.dateStringZTToDate(datos.getString("limite"));
            Date ganadora = Utilities.dateStringZTToDate(datos.getString("fechaganadora"));
            Qdada qdada = new Qdada(ctx,
                                    datos.getString("idqdada"),
                                    datos.getString("titulo"),
                                    datos.getString("descripcion"),
                                    creador,
                                    invitados,
                                    datos.getDouble("latitud"),
                                    datos.getDouble("longitud"),
                                    datos.getString("direccion"),
                                    limite,
                                    datos.getBoolean("reinvitacion"),
                                    true,
                                    ganadora
                                   );
            if (BBDD.getQdadaByIDServer(ctx, datos.getString("idqdada")) != null){
                // Ya existe...
                System.out.println("Ya existe");
            } else {
                qdada.setStatus(Entity.STATUS_NEW);
                com.cusl.ull.qdemos.bbdd.utilities.BBDD.getApplicationDataContext(ctx).qdadaDao.add(qdada);
                com.cusl.ull.qdemos.bbdd.utilities.BBDD.getApplicationDataContext(ctx).qdadaDao.save();
            }
            List<Date> fechas = new ArrayList<Date>();
            JSONArray fechasJSON = datos.getJSONArray("fechas");
            for (int j=0; j< fechasJSON.length(); j++){
                Date fecha = Utilities.dateStringZTToDate(fechasJSON.getString(j));
                if (fecha != null)
                    fechas.add(fecha);
            }
            BBDD.setFechas(ctx, fechas, datos.getString("idqdada"));

            for (Date f: fechas){
                UsuarioEleccion ue = new UsuarioEleccion(ctx, creador.getIdfacebook(), f, qdada.getIdQdada());
                ue.setStatus(Entity.STATUS_NEW);
                BBDD.getApplicationDataContext(ctx).participanteDao.add(ue);
                BBDD.getApplicationDataContext(ctx).participanteDao.save();
            }

            return qdada;
        } catch (Exception e) {
            // TODO: Fallo grande, la notificación ha llegado pero hubo un error al parsearla y la Qdada no le ha llegado al usuario
            return null;
        }
    }

}
