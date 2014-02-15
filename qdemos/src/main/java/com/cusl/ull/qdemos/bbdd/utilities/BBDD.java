package com.cusl.ull.qdemos.bbdd.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.bbdd.dao.QdemosDataContext;
import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.models.QdadaFechas;
import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.bbdd.models.UsuarioEleccion;
import com.cusl.ull.qdemos.utilities.Utilities;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.mobandme.ada.Entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 13/01/14.
 */

// Funciones tipicas de BBDD
public class BBDD {

    public static QdemosDataContext appDataContext;

    public static Date ultimaSincronizacionConServidor;

    static final String TAG = "En Local";

    public static void initBBDD (Context ctx){
        getApplicationDataContext(ctx);
    }

    // Inicializar los DAOs, para poder acceder a los datos
    public static QdemosDataContext getApplicationDataContext(Context ctx) {
        if (BBDD.appDataContext == null) {
            try {
                BBDD.appDataContext = new QdemosDataContext(ctx);
            } catch (Exception e) {
                Log.e("BBDD", "Error inicializando QdemosDataContext: " + e.getMessage());
            }
        }
        return BBDD.appDataContext;
    }

    public static Usuario quienSoy (Context ctx){
        try {
            return BBDD.getApplicationDataContext(ctx).usuarioDao.getMyUser();
        } catch (Exception e){
            return null;
        }
    }

    public static void crearUsuarioIfNotExist (Context ctx, String nombre, String idFB, String idGcm){
        try{
            if (idGcm.isEmpty()){
                idGcm = "1";
            }
            com.cusl.ull.qdemos.server.Utilities.crearUsuario(ctx, nombre, idFB, idGcm, true);
        } catch (Exception e){}
    }

    public static void crearUsuarioByIdFacebook (Context ctx, String idFB){
        if (BBDD.getApplicationDataContext(ctx).usuarioDao.exist(idFB))
            return;
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            Request request = Request.newMeRequest(session,
                    new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            // If the response is successful
                            if (user != null) {
                                Usuario usuario = new Usuario(user.getName(), user.getId(), null);
                                try {
                                    usuario.setStatus(Entity.STATUS_NEW);
                                    BBDD.appDataContext.usuarioDao.add(usuario);
                                    BBDD.appDataContext.usuarioDao.save();
                                    Log.i(TAG, "Usuario Creado Correctamente");
                                } catch (Exception e){}
                            }
                            if (response.getError() != null) {
                                // Handle errors, will do so later.
                            }
                        }
                    });
            request.executeAsync();
        } else {
            // TODO: Comprobar si se repite mucho esto, y si es asi hacer un hilo que cada cierto tiempo cumprueba si existen en BBDD Local usuarios sin nombres para setearselos si se puede
            // TODO: A colación de lo anterior, ver si procede actualizar los nombres (En Local y en el Servidor) cada cierto tiempo, por si los usuarios se los cambian en Facebook.
            Usuario user = new Usuario(ctx.getString(R.string.no_disponible), idFB, null);
            try {
                user.setStatus(Entity.STATUS_NEW);
                BBDD.getApplicationDataContext(ctx).usuarioDao.add(user);
                BBDD.getApplicationDataContext(ctx).usuarioDao.save();
                Log.i(TAG, "Usuario Creado Correctamente");
            } catch (Exception e){}
        }
    }

    public static boolean existo(Context ctx){
        try {
            if (BBDD.getApplicationDataContext(ctx).usuarioDao.getMyUser() != null)
                return true;
            else
                return false;
        } catch (Exception e){
            return false;
        }
    }

    public static boolean existeUsuario (Context ctx, String idFB){
        try {
            if (BBDD.getApplicationDataContext(ctx).usuarioDao.getPorIdFacebook(idFB) != null)
                return true;
            else
                return false;
        } catch (Exception e){
            return false;
        }
    }

    public static Usuario getUsuario (Context ctx, String idFB){
        try {
            return BBDD.getApplicationDataContext(ctx).usuarioDao.getPorIdFacebook(idFB);
        } catch (Exception e){
            return null;
        }
    }

    public static List<Date> miEleccion (Context ctx, String idQdada, String idFacebook){
        List<Date> fechas = new ArrayList<Date>();
        try {
            List<UsuarioEleccion> elecciones = BBDD.getApplicationDataContext(ctx).participanteDao.search(false, "Idqdada = ? and Idfacebook = ?", new String[]{idQdada, idFacebook}, null, null, null, null, null);
            for (UsuarioEleccion eleccion: elecciones){
                fechas.add(eleccion.getFecha());
            }
        } catch (Exception e){}
        return fechas;
    }

    public static void updateMiEleccion (Activity activity, String idQdada, String idFacebook, List<Date> fechas){
        ProgressDialog pd = ProgressDialog.show(activity, activity.getResources().getText(R.string.esperar), activity.getResources().getText(R.string.procesando));
        pd.setIndeterminate(false);
        pd.setCancelable(false);
        com.cusl.ull.qdemos.server.Utilities.crearEleccionQdada(activity, pd, idQdada, idFacebook, fechas);
    }

    public static void updateEleccionLocal (Context ctx, String idQdada, String idFacebook, List<Date> fechas){
        try{
            List<UsuarioEleccion> elecciones = BBDD.getApplicationDataContext(ctx).participanteDao.search(false, "Idqdada = ? and Idfacebook = ?", new String[]{idQdada, idFacebook}, null, null, null, null, null);
            if (elecciones != null){
                for (UsuarioEleccion eleccion: elecciones){
                    eleccion.setStatus(Entity.STATUS_DELETED);
                    BBDD.getApplicationDataContext(ctx).participanteDao.remove(eleccion);
                    BBDD.getApplicationDataContext(ctx).participanteDao.save(eleccion);
                }
            }
            for (Date fecha: fechas){
                UsuarioEleccion ue = new UsuarioEleccion(ctx, idFacebook, fecha, idQdada);
                ue.setStatus(Entity.STATUS_NEW);
                BBDD.getApplicationDataContext(ctx).participanteDao.add(ue);
                BBDD.getApplicationDataContext(ctx).participanteDao.save();
            }
            calcularFechaGanadora(ctx, idQdada);
        } catch (Exception e){}
    }

    public static Qdada getQdadaFromJSON (Context ctx, String qdadajson){
        com.cusl.ull.qdemos.bbdd.models.Qdada datos = new Gson().fromJson(qdadajson, com.cusl.ull.qdemos.bbdd.models.Qdada.class);
        try {
            Qdada qdada = BBDD.getQdadaByIDServer(ctx, datos.getIdQdada());
            return qdada;
        } catch (Exception e){
            Toast.makeText(ctx, R.string.error_bbdd_r, Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public static void setFechas(Context ctx, List<Date> fechas, String idqdada) {
        try {
            for (Date fecha: fechas){
                QdadaFechas qf = new QdadaFechas(idqdada, fecha);
                qf.setStatus(Entity.STATUS_NEW);
                BBDD.getApplicationDataContext(ctx).qdadaFechasDao.add(qf);
                BBDD.getApplicationDataContext(ctx).qdadaFechasDao.save();
            }
            calcularFechaGanadora(ctx, idqdada);
        } catch (Exception e){}
    }

    // Función que se encarga de saber cual es la fecha más repetida de los participantes para conocer la que mejor se adapta a la Qdada.
    public static void calcularFechaGanadora (Context ctx,  String idqdada){
        List<Date> totales = new ArrayList<Date>();
        List<Date> comparador = new ArrayList<Date>();
        try {
            List<UsuarioEleccion> elecciones = BBDD.getApplicationDataContext(ctx).participanteDao.search(false, "Idqdada = ?", new String[]{idqdada}, null, null, null, null, null);
            for (UsuarioEleccion ele: elecciones){
                totales.add(ele.getFecha());
            }
            Date fechaGanadora = getFechaGanadora(ctx, totales);
            Qdada qdada = BBDD.getQdadaByIDServer(ctx, idqdada);
            qdada.setFechaGanadora(fechaGanadora);
            qdada.setStatus(Entity.STATUS_UPDATED);
            BBDD.getApplicationDataContext(ctx).qdadaDao.save(qdada);

        } catch (Exception e){}
    }

    // Función que se encarga de saber cual es la fecha más repetida de los participantes para conocer la que mejor se adapta a la Qdada.
    public static Date getFechaGanadora (Context ctx,  List<Date> fechasGuardadas, List<Date> nuevasFechas){
        List<Date> totales = new ArrayList<Date>();
        totales.addAll(fechasGuardadas);
        totales.addAll(nuevasFechas);
        return getFechaGanadora(ctx, totales);
    }

    // Función que se encarga de saber cual es la fecha más repetida de los participantes para conocer la que mejor se adapta a la Qdada.
    public static Date getFechaGanadora (Context ctx,  List<Date> totales){
        List<Date> comparador = new ArrayList<Date>();
        List<Integer> repeticiones = new ArrayList<Integer>();
        int mayor=-1, indice=0;
        for (Date fecha: totales){
            if (comparador.contains(fecha)){
                int index = comparador.indexOf(fecha);
                int repetido = (repeticiones.get(index)+1);
                if (repetido > mayor){
                    mayor = repetido;
                    indice = index;
                }
                repeticiones.set(index, repetido);
            } else {
                comparador.add(fecha);
                repeticiones.add(0);
            }
        }
        return comparador.get(indice);
    }

    public static int numeroParticipantes (Context ctx, String idqdada){
        int participantes = 0;
        try {
            Qdada qdada = BBDD.getQdadaByIDServer(ctx, idqdada);
            List<UsuarioEleccion> elecciones = BBDD.getApplicationDataContext(ctx).participanteDao.search(false, "Idqdada = ?", new String[]{idqdada}, null, null, null, null, null);
            for (UsuarioEleccion ele: elecciones){
                if (ele.getFecha().equals(qdada.getFechaGanadora())){
                    participantes++;
                }
            }
        } catch (Exception e){}
        return participantes;
    }

    public static List<Usuario> getParticipantes (Context ctx, String idqdada){
        List<Usuario> usuarios = new ArrayList<Usuario>();
        try {
            Qdada qdada = BBDD.getQdadaByIDServer(ctx, idqdada);
            List<UsuarioEleccion> elecciones = BBDD.getApplicationDataContext(ctx).participanteDao.search(false, "Idqdada = ?", new String[]{idqdada}, null, null, null, null, null);
            for (UsuarioEleccion ue: elecciones){
                if (ue.getFecha().equals(qdada.getFechaGanadora())){
                    if (!Utilities.containsUsuario(usuarios, ue.getIdusuario())){
                        Usuario user = BBDD.getApplicationDataContext(ctx).usuarioDao.getPorIdFacebook(ue.getIdusuario());
                        usuarios.add(user);
                    }
                }
            }
        } catch (Exception e){}
        return usuarios;
    }

    public static List<Date> getFechas (Context ctx, String idqdada){
        List<Date> fechas = new ArrayList<Date>();
        try {
            List<QdadaFechas> qf = BBDD.getApplicationDataContext(ctx).qdadaFechasDao.search(false, "Idqdada = ?", new String[]{idqdada}, null, null, null, null, null);
            for (QdadaFechas item: qf){
                fechas.add(item.getFecha());
            }
        } catch (Exception e){}
        return fechas;
    }

    public static boolean soyParticipante (Context ctx, String idQdada, String idFB, Date fechaGanadora){
        try {
            List<UsuarioEleccion> elecciones = BBDD.getApplicationDataContext(ctx).participanteDao.search(false, "Idqdada = ? and Idfacebook = ?", new String[]{idQdada, idFB}, null, null, null, null, null);
            for (UsuarioEleccion ue: elecciones){
                if (fechaGanadora.equals(ue.getFecha())){
                    return true;
                }
            }
        } catch (Exception e){}
        return false;
    }

    public static boolean tengoEleccion (Context ctx, String idQdada, String idFB){
        try {
            List<UsuarioEleccion> elecciones = BBDD.getApplicationDataContext(ctx).participanteDao.search(false, "Idqdada = ? and Idfacebook = ?", new String[]{idQdada, idFB}, null, null, null, null, null);
            if ((elecciones != null) && (!elecciones.isEmpty()))
                return true;
        } catch (Exception e){}
        return false;
    }

    public static boolean qdadaHistorica (Context ctx, Qdada qdada){
        if (qdada.getFechaGanadora().before(new Date())){
            return true;
        }
        return false;
    }

    public static Qdada getQdadaByIDServer(Context ctx, String idserver){
        try {
            Qdada qdada = BBDD.getApplicationDataContext(ctx).qdadaDao.search(false, "Idqdada = ?", new String[]{idserver}, null, null, null, null, null).get(0);
            return qdada;
        } catch (Exception e){
            return null;
        }
    }

    public static boolean updateIdQdadaServer (Context ctx, Long idqdada, String idqdadaserver){
        try{
            Qdada qdada = BBDD.getApplicationDataContext(ctx).qdadaDao.getElementByID(idqdada);
            qdada.setIdQdada(idqdadaserver);
            qdada.setStatus(Entity.STATUS_UPDATED);
            BBDD.getApplicationDataContext(ctx).qdadaDao.save(qdada);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public static boolean updateIdGCM (Context ctx, Usuario usuario, String idGCM){
        if ((idGCM == null) || (idGCM.isEmpty()))
            return false;
        try{
            if (usuario != null){
                com.cusl.ull.qdemos.server.Utilities.crearUsuario(ctx, usuario.getNombre(), usuario.getIdfacebook(), idGCM, false);
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }

    public static boolean hayQueActualizar (){
        if (BBDD.ultimaSincronizacionConServidor == null)
            return true;
        Calendar ahora = Calendar.getInstance();
        Calendar actualizacion = Calendar.getInstance();
        actualizacion.setTime(BBDD.ultimaSincronizacionConServidor);
        // TODO: Ponerlo mas alto, quizas no, la frecuencia de actualizacion, esta bajo para depuracion en desarrollo. Incluso meterlo en una property o algo de eso
        actualizacion.add(Calendar.MINUTE, 5);
        if (actualizacion.before(ahora)){
            return true;
        } else {
            return false;
        }
    }

}
