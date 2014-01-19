package com.cusl.ull.qdemos.bbdd.dao;

/**
 * Created by Paco on 13/01/14.
 */
import android.content.Context;

import com.cusl.ull.qdemos.bbdd.models.Fecha;
import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.bbdd.models.UsuarioEleccion;
import com.mobandme.ada.ObjectContext;
import com.mobandme.ada.ObjectSet;

// DAO de los modelos que definimos en la BBDD, lo que nos permitira acceder a los datos desde cualquier parte.
public class QdemosDataContext extends ObjectContext {

    public ObjectSet<Qdada> qdadaDao;
    public ObjectSet<UsuarioEleccion> participanteDao;
    public ObjectSet<Usuario> usuarioDao;
    public ObjectSet<Fecha> fechaDao;

    public QdemosDataContext(Context ctx) throws Exception {
        super(ctx, "Qdemos_db");
        this.qdadaDao = new ObjectSet<Qdada>(Qdada.class, this);
        this.usuarioDao = new ObjectSet<Usuario>(Usuario.class, this);
        this.participanteDao = new ObjectSet<UsuarioEleccion>(UsuarioEleccion.class, this);
        this.fechaDao = new ObjectSet<Fecha>(Fecha.class, this);
    }

}