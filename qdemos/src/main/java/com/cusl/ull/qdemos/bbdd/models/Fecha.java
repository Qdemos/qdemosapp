package com.cusl.ull.qdemos.bbdd.models;

import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

import java.util.Date;

/**
 * Created by Paco on 19/01/14.
 */
@Table(name = "Fecha")
public class Fecha extends Entity {

    @TableField(name = "Fecha", datatype = DATATYPE_DATE_BINARY)
    public Date Fecha;

    public Fecha(){
        super();
    }

    public Fecha(Date date) {
        super();
        setFecha(date);
    }

    public Date getFecha() {
        return this.Fecha;
    }

    public void setFecha(Date date) {
        this.Fecha = date;
    }
}
