package com.cusl.ull.qdemos.bbdd.models;

import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.TableField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 13/01/14.
 */
// Clase que representa la elección de un usuario en una quedada, o sea, que ha respondido a que participará y las fechas que ha dicho que puede asistir.
public class UsuarioEleccion extends Entity {

    @TableField(name = "Usuario", datatype = DATATYPE_ENTITY_LINK)
    public Usuario usuario;

    @TableField(name = "Fechas", datatype = DATATYPE_ENTITY_LINK)
    public List<Date> fechas = new ArrayList<Date>();

    public UsuarioEleccion(){
        super();
    }

    public UsuarioEleccion(com.cusl.ull.qdemos.bbdd.models.Usuario usuario, List<Date> fechas) {
        super();
        setUsuario(usuario);
        setFechas(fechas);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        usuario = usuario;
    }

    public List<Date> getFechas() {
        return fechas;
    }

    public void setFechas(List<Date> fechas) {
        fechas = fechas;
    }
}
