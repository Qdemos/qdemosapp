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

    // Fechas propuestas para la realización de las quedadas
    @TableField(name = "Fechas", datatype = DATATYPE_ENTITY_LINK)
    public List<Fecha> fechas = new ArrayList<Fecha>();

    // Fecha de momento ganadora
    @TableField(name = "Fechaganadora", datatype = DATATYPE_ENTITY_LINK)
    public Fecha fechaGanadora;

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
        setFechaGanadora(Conversores.fromDateToFecha(fechas.get(0)));
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

    public Fecha getFechaGanadora() {
        return fechaGanadora;
    }

    public void setFechaGanadora(Fecha fechaGanadora) {
        this.fechaGanadora = fechaGanadora;
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
        List<Fecha> fechasEleccion = Conversores.fromListDateToListFecha(fechas);
        participantes.add(new UsuarioEleccion(ctx, usuario, fechasEleccion, this.getID()));
        calcularFechaGanadora(fechasEleccion);
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    // Función que se encarga de saber cual es la fecha más repetida de los participantes para conocer la que mejor se adapta a la Qdada.
    public void calcularFechaGanadora (List<Fecha> nuevas){
        List<Fecha> totales = new ArrayList<Fecha>();
        List<Fecha> comparador = new ArrayList<Fecha>();
        totales.addAll(nuevas);
        for (UsuarioEleccion participante: this.getParticipantes()){
            totales.addAll(participante.getFechas());
        }
        List<Integer> repeticiones = new ArrayList<Integer>();
        int mayor=-1, indice=0;
        for (Fecha fecha: totales){
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
        this.setFechaGanadora(comparador.get(indice));
    }
}
