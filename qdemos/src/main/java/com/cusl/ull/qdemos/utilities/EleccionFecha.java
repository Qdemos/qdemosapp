package com.cusl.ull.qdemos.utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Paco on 21/01/14.
 */
public class EleccionFecha {

    public static Map<Date, Boolean> elecciones;
    public static boolean hayCambios = false;

    public static boolean isEmpty(){
        return !hayCambios;
    }

    public static void reset(){
        elecciones = new HashMap<Date, Boolean>();
        hayCambios = false;
    }

    // Inicializar por primera vez como todas las Fechas sin seleccion (si no ha respondido aun)
    public static void reset (List<Date> todas){
        elecciones = new HashMap<Date, Boolean>();
        for (Date fecha: todas){
            elecciones.put(fecha, null);
        }
        hayCambios=false;
    }

    // Inicializar con las fechas que seleccionó en su momento
    public static void reset (List<Date> todas, List<Date> miEleccion){
        elecciones = new HashMap<Date, Boolean>();
        for (Date fecha: todas){
            if (Utilities.containsDate(miEleccion,fecha))
                elecciones.put(fecha, true);
            else
                elecciones.put(fecha, false);
        }
        hayCambios=false;
    }

    public static void setSelectedFecha(Date fecha, Boolean selected){
        elecciones.put(fecha, selected);
        if (!hayCambios)
            hayCambios=true;
    }

    public static List<Date> getFechas (){
        if (elecciones == null)
            return null;
        List<Date> ret = new ArrayList<Date>();
        for (Map.Entry<Date, Boolean> entry: elecciones.entrySet()){
            if (entry.getValue() == null){
                return null;
            } else if (entry.getValue()) {
                ret.add(entry.getKey());
            }
        }
        return ret;
    }
}
