package com.cusl.ull.qdemos.fragments.listas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.adapters.QdadasAdapter;
import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 7/01/14.
 * Fragment que se encarga de Loguear al usuario, es el splash screen inicial de login
 */
public class AceptadasFragment extends Fragment {

        ListView listView;

        public AceptadasFragment() {
            // Se ejecuta antes que el onCreateView

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_qdadas, container, false);
            // Empezar aqui a trabajar con la UI

            listView = (ListView) rootView.findViewById(R.id.listaQdadas);


            List<Qdada> qdadas = new ArrayList<Qdada>();
            List<Qdada> qdadasQuieroIr = new ArrayList<Qdada>();
            try {
                String myIdFB = BBDD.quienSoy(getActivity()).getIdfacebook();
                Date ahora = new Date();
                qdadas = BBDD.getApplicationDataContext(getActivity()).qdadaDao.search(false, "Sinresponder = ? and Fechaganadora > ?", new String[]{"0", String.valueOf(ahora.getTime())}, "Fechaganadora ASC", null, null, null, null);
                for (Qdada qdada: qdadas){
                    if (BBDD.tengoEleccion(getActivity(), qdada.getIdQdada(), myIdFB)){
                        qdadasQuieroIr.add(qdada);
                    }
                }
            } catch (Exception e){}

            QdadasAdapter qdadasAceptadasAdapter = new QdadasAdapter(getActivity(), qdadasQuieroIr, 0);
            listView.setAdapter(qdadasAceptadasAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intentQ = new Intent(getActivity(), com.cusl.ull.qdemos.Qdada.class);
                    Qdada item = (Qdada) listView.getItemAtPosition(i);
                    intentQ.putExtra("qdadajson", new Gson().toJson(item));
                    intentQ.putExtra("edicion", false);
                    startActivity(intentQ);
                }
            });

            return rootView;
        }

}
