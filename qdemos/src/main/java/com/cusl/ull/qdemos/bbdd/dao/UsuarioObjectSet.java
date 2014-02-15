package com.cusl.ull.qdemos.bbdd.dao;

import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.mobandme.ada.ObjectContext;
import com.mobandme.ada.ObjectSet;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import java.util.List;

/**
 * Created by Paco on 27/01/14.
 */
public class UsuarioObjectSet extends ObjectSet {

    public UsuarioObjectSet(ObjectContext pContext) throws AdaFrameworkException {
        super(Usuario.class, pContext);

    }

    public boolean exist(String id){
        try{
            if (id != null && !id.trim().equals("")){
                String wherePattern = String.format("%s = ?", this.getDataTableFieldName("idfacebook"));

                String[] whereValores = new String[] { id };

                List resultado = search(true, wherePattern, whereValores, null, null, null, 0, 1);
                if (resultado != null && resultado.size() > 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Usuario getPorIdFacebook(String id){
        return getPorIdFacebook(id, null, null, false);
    }

    public Usuario getPorIdFacebook(String id, String nombre, String idgcm, boolean anadirSiNoExiste){

        Usuario user = null;

        try{
            if (id != null && !id.trim().equals("")){
                String wherePattern = String.format("%s = ?", this.getDataTableFieldName("idfacebook"));

                String[] whereValores = new String[] { id };

                List resultado = search(true, wherePattern, whereValores, null, null, null, 0, 1);
                if (resultado != null && resultado.size() > 0) {
                    user = (Usuario) resultado.get(0);
                } else if (anadirSiNoExiste) {
                    user = new Usuario(nombre, id, idgcm);

                    save(user);

                    add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public Usuario getMyUser(){

        Usuario user = null;

        try{
            String wherePattern = String.format("%s != ?", this.getDataTableFieldName("idgcm"));

            String[] whereValores = new String[] { "null" };

            List resultado = search(true, wherePattern, whereValores, null, null, null, 0, 1);
            if (resultado != null && resultado.size() > 0) {
                user = (Usuario) resultado.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }


}
