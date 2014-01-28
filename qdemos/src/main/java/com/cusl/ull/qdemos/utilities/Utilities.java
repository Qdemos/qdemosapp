package com.cusl.ull.qdemos.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.bbdd.models.UsuarioEleccion;
import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.cusl.ull.qdemos.bbdd.utilities.Conversores;

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
            List<UsuarioEleccion> elecciones = BBDD.getApplicationDataContext(ctx).participanteDao.search(false, "Idqdada = ? and Idfacebook = ?", new String[]{qdada.getID().toString(), idFacebook}, null, null, null, null, null);
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

}
