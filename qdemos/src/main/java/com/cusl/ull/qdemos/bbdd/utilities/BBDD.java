package com.cusl.ull.qdemos.bbdd.utilities;

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
import com.google.gson.Gson;
import com.mobandme.ada.Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 13/01/14.
 */

// Funciones tipicas de BBDD
public class BBDD {

    public static QdemosDataContext appDataContext;

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

    public static void crearUsuarioIfNotExist (Context ctx, String nombre, String idFB){
        try{
            // TODO: Rellenar el usuario correctamente con los datos de IDGCM y demás
            if (existo(ctx)){
                //TODO: Corroborar que el IDGCM es el correcto tanto en local como en el servidor
            } else {
                Usuario user = new Usuario(nombre, idFB, "1111");
                user.setStatus(Entity.STATUS_NEW);
                BBDD.getApplicationDataContext(ctx).usuarioDao.add(user);
                BBDD.getApplicationDataContext(ctx).usuarioDao.save();
            }
        } catch (Exception e){}
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

    public static List<Date> miEleccion (Context ctx, Long idQdada, String idFacebook){
        List<Date> fechas = new ArrayList<Date>();
        try {
            List<UsuarioEleccion> elecciones = BBDD.getApplicationDataContext(ctx).participanteDao.search(false, "Idqdada = ? and Idfacebook = ?", new String[]{idQdada.toString(), idFacebook}, null, null, null, null, null);
            for (UsuarioEleccion eleccion: elecciones){
                fechas.add(eleccion.getFecha());
            }
        } catch (Exception e){}
        return fechas;
    }

    public static void updateMiEleccion (Context ctx, Long idQdada, String idFacebook, List<Date> fechas){
        try{
            List<UsuarioEleccion> elecciones = BBDD.getApplicationDataContext(ctx).participanteDao.search(false, "Idqdada = ? and Idfacebook = ?", new String[]{idQdada.toString(), idFacebook}, null, null, null, null, null);
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
            Qdada qdada = BBDD.getApplicationDataContext(ctx).qdadaDao.getElementByID(datos.getID());
            return qdada;
        } catch (Exception e){
            Toast.makeText(ctx, R.string.error_bbdd_r, Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public static void setFechas(Context ctx, List<Date> fechas, Long idqdada) {
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
    public static void calcularFechaGanadora (Context ctx, Long idqdada){
        List<Date> totales = new ArrayList<Date>();
        List<Date> comparador = new ArrayList<Date>();
        try {
            List<UsuarioEleccion> elecciones = BBDD.getApplicationDataContext(ctx).participanteDao.search(false, "Idqdada = ?", new String[]{idqdada.toString()}, null, null, null, null, null);
            for (UsuarioEleccion ele: elecciones){
                totales.add(ele.getFecha());
            }
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
            Qdada qdada = BBDD.getApplicationDataContext(ctx).qdadaDao.getElementByID(idqdada);
            qdada.setFechaGanadora(comparador.get(indice));
            qdada.setStatus(Entity.STATUS_UPDATED);
            BBDD.getApplicationDataContext(ctx).qdadaDao.save(qdada);

        } catch (Exception e){}
    }

    public static int numeroParticipantes (Context ctx, Long idqdada){
        int participantes = 0;
        try {
            Qdada qdada = BBDD.getApplicationDataContext(ctx).qdadaDao.getElementByID(idqdada);
            List<UsuarioEleccion> elecciones = BBDD.getApplicationDataContext(ctx).participanteDao.search(false, "Idqdada = ?", new String[]{idqdada.toString()}, null, null, null, null, null);
            for (UsuarioEleccion ele: elecciones){
                if (ele.getFecha().equals(qdada.getFechaGanadora())){
                    participantes++;
                }
            }
        } catch (Exception e){}
        return participantes;
    }

    public static List<Usuario> getParticipantes (Context ctx, Long idqdada){
        List<Usuario> usuarios = new ArrayList<Usuario>();
        try {
            Qdada qdada = BBDD.getApplicationDataContext(ctx).qdadaDao.getElementByID(idqdada);
            List<UsuarioEleccion> elecciones = BBDD.getApplicationDataContext(ctx).participanteDao.search(false, "Idqdada = ?", new String[]{idqdada.toString()}, null, null, null, null, null);
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

    public static List<Date> getFechas (Context ctx, Long idqdada){
        List<Date> fechas = new ArrayList<Date>();
        try {
            List<QdadaFechas> qf = BBDD.getApplicationDataContext(ctx).qdadaFechasDao.search(false, "Idqdada = ?", new String[]{idqdada.toString()}, null, null, null, null, null);
            for (QdadaFechas item: qf){
                fechas.add(item.getFecha());
            }
        } catch (Exception e){}
        return fechas;
    }

    public static boolean soyParticipante (Context ctx, Long idQdada, String idFB, Date fechaGanadora){
        try {
            List<UsuarioEleccion> elecciones = BBDD.getApplicationDataContext(ctx).participanteDao.search(false, "Idqdada = ? and Idfacebook = ?", new String[]{idQdada.toString(), idFB}, null, null, null, null, null);
            for (UsuarioEleccion ue: elecciones){
                if (fechaGanadora.equals(ue.getFecha())){
                    return true;
                }
            }
        } catch (Exception e){}
        return false;
    }

    public static boolean tengoEleccion (Context ctx, Long idQdada, String idFB){
        try {
            List<UsuarioEleccion> elecciones = BBDD.getApplicationDataContext(ctx).participanteDao.search(false, "Idqdada = ? and Idfacebook = ?", new String[]{idQdada.toString(), idFB}, null, null, null, null, null);
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

}
