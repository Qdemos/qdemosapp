package com.cusl.ull.qdemos.bbdd.models;

import android.content.Context;

import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 13/01/14.
 */
// Clase que representa la elección de un usuario en una quedada, o sea, que ha respondido a que participará y las fechas que ha dicho que puede asistir.
@Table(name = "Usuarioeleccion")
public class UsuarioEleccion extends Entity {

    // Este campo lo coloco ya que la el ORM que utilizo le encontré un fallo que salta cuando no tiene atributos propios,
    // sólo enlaces a otras entidades como sería este caso, por ello creo un atributo propio con un valor que ademas me sirve
    // para buscar mas rapido sin hacer JOINS entra las tablas de QDADAS y USUARIOELECCION
    @TableField(name = "Idqdada", datatype = DATATYPE_STRING)
    public Long Idqdada;

    @TableField(name = "Usuario", datatype = DATATYPE_ENTITY_LINK)
    public Usuario Usuario;

    @TableField(name = "Fechas", datatype = DATATYPE_ENTITY_LINK)
    public List<Fecha> Fechas = new ArrayList<Fecha>();

    public UsuarioEleccion(){
        super();
    }

    public UsuarioEleccion(Context ctx, Usuario user, List<Fecha> fechas, Long idqdada) {
        super();
        setIdqdada(idqdada);
        setUsuario(user);
        setFechas(ctx, fechas);
    }

    public Usuario getUsuario() {
        return this.Usuario;
    }

    public void setUsuario(Usuario user) {
        this.Usuario = user;
    }

    public List<Fecha> getFechas() {
        return this.Fechas;
    }

    public Long getIdqdada() {
        return Idqdada;
    }

    public void setIdqdada(Long idqdada) {
        this.Idqdada = idqdada;
    }

    public void setFechas(Context ctx, List<Fecha> fechasParam) {
        try {
            for (Fecha fecha: fechasParam){
                BBDD.getApplicationDataContext(ctx).fechaDao.save(fecha);
            }
            this.Fechas = fechasParam;
        } catch (Exception e){}
    }
}
