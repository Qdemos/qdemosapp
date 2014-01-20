package com.cusl.ull.qdemos.bbdd.models;

import com.cusl.ull.qdemos.utilities.Utilities;
import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    @Override
    public boolean equals(Object object){
        if (object != null && object instanceof Fecha) {
            Fecha comp = (Fecha) object;
            Calendar c1 = Calendar.getInstance();
            c1.setTime(this.getFecha());
            Calendar c2  = Calendar.getInstance();
            c2.setTime(comp.getFecha());
            if (   (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
                && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
                && (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH))
                && (c1.get(Calendar.HOUR) == c2.get(Calendar.HOUR))
                && (c1.get(Calendar.MINUTE) == c2.get(Calendar.MINUTE))
                ){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
