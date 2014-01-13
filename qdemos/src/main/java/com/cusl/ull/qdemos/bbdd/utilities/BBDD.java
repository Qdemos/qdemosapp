package com.cusl.ull.qdemos.bbdd.utilities;

import android.content.Context;
import android.util.Log;

import com.cusl.ull.qdemos.bbdd.dao.QdemosDataContext;

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

}
