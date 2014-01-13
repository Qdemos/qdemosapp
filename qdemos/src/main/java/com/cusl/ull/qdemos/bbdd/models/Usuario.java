package com.cusl.ull.qdemos.bbdd.models;

import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.TableField;

/**
 * Created by Paco on 13/01/14.
 */
// Clase que representa a un usuario.
public class Usuario extends Entity{

    // Nombre del usuario, se almacena para que en caso de que no haya conexion a internet, y no poder acceder a su info de Facebook, al menos mostrar el nombre de los usuarios
    @TableField(name = "Nombre", datatype = DATATYPE_STRING)
    public String nombre;

    // Identificador del usuario en facebook que nos servira para buscar su nombre, descargar y mostrar su foto de perfil, etc.
    @TableField(name = "Idfacebook", datatype = DATATYPE_STRING)
    public String idfacebook;

    public Usuario(){
        super();
    }

    public Usuario(String nombre, String idfacebook){
        super();
        setNombre(nombre);
        setIdfacebook(idfacebook);
    }

    public String getNombre() {
        return nombre;
    }

    public String getIdfacebook() {
        return idfacebook;
    }

    public void setNombre(String nombre) {
        nombre = nombre;
    }

    public void setIdfacebook(String idfacebook) {
        idfacebook = idfacebook;
    }
}