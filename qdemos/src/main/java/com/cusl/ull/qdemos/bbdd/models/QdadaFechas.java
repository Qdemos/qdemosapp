package com.cusl.ull.qdemos.bbdd.models;

import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 27/01/14.
 */

// Clase que representa una de las fechas propuesta para una de las Qdadas. De esta manera buscando todas las filas de esta tabla que contengan un mismo ID de Qdada podemos obtener
// todas las fechas propuestas para hacer la Qdada
@Table(name = "Qdadafechas")
public class QdadaFechas extends Entity {

    @TableField(name = "Idqdada", datatype = DATATYPE_STRING)
    public String Idqdada;

    // Fechas propuestas para la realización de las quedadas
    @TableField(name = "Fecha", datatype = DATATYPE_DATE_BINARY)
    public Date fecha;

    public QdadaFechas(){
        super();
    }

    public QdadaFechas(String idqdada, Date fecha) {
        Idqdada = idqdada;
        this.fecha = fecha;
    }

    public String getIdqdada() {
        return Idqdada;
    }

    public void setIdqdada(String idqdada) {
        Idqdada = idqdada;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
