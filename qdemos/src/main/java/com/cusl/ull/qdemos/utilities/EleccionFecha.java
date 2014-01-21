package com.cusl.ull.qdemos.utilities;

import com.cusl.ull.qdemos.bbdd.models.Fecha;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Paco on 21/01/14.
 */
public class EleccionFecha {

    public static Map<Fecha, Boolean> elecciones;

    // Inicializar por primera vez como todas las Fechas sin seleccion (si no ha respondido aun)
    public static void reset (List<Fecha> todas){
        elecciones = new HashMap<Fecha, Boolean>();
        for (Fecha fecha: todas){
            elecciones.put(fecha, null);
        }
    }

    // Inicializar con las fechas que seleccionó en su momento
    public static void reset (List<Fecha> todas, List<Fecha> miEleccion){
        elecciones = new HashMap<Fecha, Boolean>();
        for (Fecha fecha: todas){
            if (miEleccion.contains(fecha))
                elecciones.put(fecha, true);
            else
                elecciones.put(fecha, false);
        }
    }

    public static void setSelectedFecha(Fecha fecha, Boolean selected){
        elecciones.put(fecha, selected);
    }
}
