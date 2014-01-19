package com.cusl.ull.qdemos.bbdd.models;

/**
 * Created by Paco on 13/01/14.
 */
import android.content.Context;

import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.cusl.ull.qdemos.bbdd.utilities.Conversores;
import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name = "Qdada")
public class Qdada extends Entity{

    // Nombre que le asignara el creador al evento o quedada
    @TableField(name = "Titulo", datatype = DATATYPE_STRING)
    public String titulo;

    // Descripcion detallada de lo que consistira la quedada
    @TableField(name = "Descripcion", datatype = DATATYPE_STRING)
    public String descripcion;

    // Creador de la quedada
    @TableField(name = "Creador", datatype = DATATYPE_ENTITY_LINK)
    public Usuario creador;

    // Lista de los invitados a la quedada, que no son los que asistiran, son los que han sido invitados y no han respondido
    @TableField(name = "Invitados", datatype = DATATYPE_ENTITY_LINK)
    public List<Usuario> invitados = new ArrayList<Usuario>();

    // Participantes que asistiran a la quedada, una vez han respondido. Estos participantes vendran de invitados, por lo que si un participantes esta en esta lista, ya no estara en la de invitados
    @TableField(name = "Participantes", datatype = DATATYPE_ENTITY_LINK)
    public List<UsuarioEleccion> participantes = new ArrayList<UsuarioEleccion>();

    // Latitud del lugar de la quedada
    @TableField(name = "Latitud", datatype = DATATYPE_DOUBLE)
    public Double latitud;

    // Longitud del lugar de la quedada
    @TableField(name = "Longitud", datatype = DATATYPE_DOUBLE)
    public Double longitud;

    // Nombre de la calle y similar de donde se celebrará la Qdada
    @TableField(name = "Direccion", datatype = DATATYPE_STRING)
    public String direccion;

    // Fechas propuestas para la realización de las quedadas, por parte del creador
    @TableField(name = "Fechas", datatype = DATATYPE_ENTITY_LINK)
    public List<Fecha> fechas = new ArrayList<Fecha>();

    // Fecha limite de respuesta para decir si un invitado asistira o no la quedada y en que fechas podra
    @TableField(name = "Limite", datatype = DATATYPE_DATE_BINARY)
    public Date limite;

    // Atributo que sirve para saber si el creador da permiso al resto de invitados a que re-inviten a mas gente a la qdada
    @TableField(name = "Reinvitacion", datatype = DATATYPE_BOOLEAN)
    public Boolean reinvitacion;

    public Qdada(){
        super();
    }

    public Qdada(Context ctx, String titulo, String descripcion, Usuario creador, List<Usuario> invitados, List<UsuarioEleccion> participantes, Double latitud, Double longitud, String direccion, List<Date> fechas, Date limite, Boolean reinvitacion) {
        setTitulo(titulo);
        setDescripcion(descripcion);
        setCreador(creador);
        setInvitados(invitados);
        setParticipantes(participantes);
        setLatitud(latitud);
        setLongitud(longitud);
        setFechas(ctx, Conversores.fromListDateToListFecha(fechas));
        setLimite(limite);
        setReinvitacion(reinvitacion);
        setDireccion(direccion);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void setCreador(Usuario creador) {
        this.creador = creador;
    }

    public List<Usuario> getInvitados() {
        return invitados;
    }

    public void setInvitados(List<Usuario> invitados) {
        this.invitados = invitados;
    }

    public List<UsuarioEleccion> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<UsuarioEleccion> participantes) {
        this.participantes = participantes;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public List<Fecha> getFechas() {
        return fechas;
    }

    public void setFechas(Context ctx, List<Fecha> fechas) {
        try {
            for (Fecha fecha: fechas){
                BBDD.getApplicationDataContext(ctx).fechaDao.save(fecha);
            }
            this.fechas = fechas;
        } catch (Exception e){}
    }

    public Date getLimite() {
        return limite;
    }

    public void setLimite(Date limite) {
        this.limite = limite;
    }

    public Boolean getReinvitacion() {
        return reinvitacion;
    }

    public void setReinvitacion(Boolean reinvitacion) {
        this.reinvitacion = reinvitacion;
    }

    public void setParticipante(Context ctx, Usuario usuario, List<Date> fechas){
        invitados.remove(usuario);
        participantes.add(new UsuarioEleccion(ctx, usuario, Conversores.fromListDateToListFecha(fechas)));
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
