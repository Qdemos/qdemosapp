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

    @TableField(name = "Idqdada", datatype = DATATYPE_STRING)
    public String Idqdada;

    @TableField(name = "Idfacebook", datatype = DATATYPE_STRING)
    public String Idfacebook;

    @TableField(name = "Fecha", datatype = DATATYPE_DATE_BINARY)
    public Date Fecha;

    public UsuarioEleccion(){
        super();
    }

    public UsuarioEleccion(Context ctx, String idFB, Date fecha, String idqdada) {
        super();
        setIdqdada(idqdada);
        setIdusuario(idFB);
        setFecha(ctx, fecha);
    }

    public Date getFecha() {
        return this.Fecha;
    }

    public String getIdqdada() {
        return Idqdada;
    }

    public void setIdqdada(String idqdada) {
        Idqdada = idqdada;
    }

    public void setFecha(Context ctx, Date fechap) {
        try {
            this.Fecha = fechap;
        } catch (Exception e){}
    }

    @Override
    public boolean equals(Object object){
        if (object != null && object instanceof UsuarioEleccion) {
            UsuarioEleccion comp = (UsuarioEleccion) object;
            if ((comp.getIdusuario().equals(this.getIdusuario())) && (comp.getIdqdada().equals(this.getIdqdada()))){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String getIdusuario() {
        return Idfacebook;
    }

    public void setIdusuario(String idusuario) {
        Idfacebook = idusuario;
    }
}
