package com.cusl.ull.qdemos.bbdd.dao;

/**
 * Created by Paco on 13/01/14.
 */
import android.content.Context;

import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.models.QdadaFechas;
import com.cusl.ull.qdemos.bbdd.models.UsuarioEleccion;
import com.mobandme.ada.ObjectContext;
import com.mobandme.ada.ObjectSet;

// DAO de los modelos que definimos en la BBDD, lo que nos permitira acceder a los datos desde cualquier parte.
public class QdemosDataContext extends ObjectContext {

    public ObjectSet<Qdada> qdadaDao;
    public ObjectSet<QdadaFechas> qdadaFechasDao;
    public ObjectSet<UsuarioEleccion> participanteDao;
    public UsuarioObjectSet usuarioDao;

    public QdemosDataContext(Context ctx) throws Exception {
        super(ctx, "Qdemos_db");
        this.qdadaDao = new ObjectSet<Qdada>(Qdada.class, this);
        this.usuarioDao = new UsuarioObjectSet(this);
        this.participanteDao = new ObjectSet<UsuarioEleccion>(UsuarioEleccion.class, this);
        this.qdadaFechasDao = new ObjectSet<QdadaFechas>(QdadaFechas.class, this);
    }

}