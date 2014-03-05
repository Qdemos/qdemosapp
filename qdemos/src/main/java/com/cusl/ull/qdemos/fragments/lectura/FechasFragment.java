package com.cusl.ull.qdemos.fragments.lectura;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.adapters.lectura.FechaAdapter;
import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.cusl.ull.qdemos.utilities.EleccionFecha;

import java.util.Date;
import java.util.List;


/**
 * Created by Paco on 7/01/14.
 * Fragment que se encarga de Loguear al usuario, es el splash screen inicial de login
 */
public class FechasFragment extends Fragment  {

        ListView listView;
        FechaAdapter fechaAdapter;
        Qdada qdada;
        boolean onlyRead;

        public FechasFragment(Qdada qdada, boolean onlyRead) {
            this.qdada = qdada;
            this.onlyRead = onlyRead;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_lectura_fechas, container, false);
            // Empezar aqui a trabajar con la UI

            listView = (ListView) rootView.findViewById(R.id.listaFechas);
            List<Date> miEleccion = BBDD.miEleccion(getActivity(), this.qdada.getIdQdada(), BBDD.quienSoy(getActivity()).getIdfacebook());
            if ((miEleccion == null) || (miEleccion.isEmpty())){
                miEleccion=BBDD.getFechas(getActivity(), qdada.getIdQdada());
                EleccionFecha.reset(miEleccion);
            } else {
                EleccionFecha.reset(BBDD.getFechas(getActivity(), qdada.getIdQdada()), miEleccion);
            }
            fechaAdapter = new FechaAdapter(getActivity(), BBDD.getFechas(getActivity(), qdada.getIdQdada()), onlyRead, qdada);
            listView.setAdapter(fechaAdapter);
            listView.setClickable(false);

            return rootView;
        }

}
