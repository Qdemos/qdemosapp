package com.cusl.ull.qdemos.bbdd.models;

import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

import java.util.Calendar;

/**
 * Created by Paco on 13/01/14.
 */
// Clase que representa a un usuario.
@Table(name = "Usuario")
public class Usuario extends Entity{

    // Nombre del usuario, se almacena para que en caso de que no haya conexion a internet, y no poder acceder a su info de Facebook, al menos mostrar el nombre de los usuarios
    @TableField(name = "Nombre", datatype = DATATYPE_STRING)
    public String nombre;

    // Identificador del usuario en facebook que nos servira para buscar su nombre, descargar y mostrar su foto de perfil, etc.
    @TableField(name = "Idfacebook", datatype = DATATYPE_STRING)
    public String idfacebook;

    // Identificador del usuario (cuenta google) en GCM para las notificaciones push
    // Solo lo tendra seteado el usuario principal, el dueño del smartphone, ya que el resto de usuarios estaran almacenados en el Servidor, para mas seguridad
    @TableField(name = "Idgcm", datatype = DATATYPE_STRING)
    public String idgcm;

    public Usuario(){
        super();
    }

    public Usuario(String nombre, String idfacebook, String idgcm){
        super();
        setNombre(nombre);
        setIdfacebook(idfacebook);
        setIdgcm(idgcm);
    }

    public String getNombre() {
        return nombre;
    }

    public String getIdfacebook() {
        return idfacebook;
    }

    public void setNombre(String nomb) {
        nombre = nomb;
    }

    public void setIdfacebook(String idface) {
        idfacebook = idface;
    }

    public String getIdgcm() {
        return idgcm;
    }

    public void setIdgcm(String idgcm) {
        this.idgcm = idgcm;
    }

    @Override
    public boolean equals(Object object){
        if (object != null && object instanceof Usuario) {
            Usuario comp = (Usuario) object;
            if (comp.getIdfacebook().equals(this.getIdfacebook())){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}