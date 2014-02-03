package com.cusl.ull.qdemos.fragments.lectura;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.utilities.DatosQdada;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Field;

/**
 * Created by Paco on 7/01/14.
 * Fragment que se encarga de Loguear al usuario, es el splash screen inicial de login
 */
public class InfoFragment extends Fragment {

        TextView titulo, descripcion;
        Qdada qdada;
        View rootView;

        public InfoFragment(Qdada qdada) {
            // Se ejecuta antes que el onCreateView
            this.qdada = qdada;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            // Para evitar que el Mapa del fragment pete, por estar inflando fragments (el mapa) dentro de fragments (Este layout). Si ya lo hemos inflado previamente
            // lo eliminamos antes de volverlo a inflar (en caso de que nos movamos de pestaña y volvamos)
            if (rootView != null) {
                ViewGroup parent = (ViewGroup) rootView.getParent();
                if (parent != null)
                    parent.removeView(rootView);
            }
            try {
                rootView = inflater.inflate(R.layout.fragment_lectura_info, container, false);
            } catch (InflateException e) {}


            // Empezar aqui a trabajar con la UI

            try {
                MapsInitializer.initialize(getActivity());
            } catch (GooglePlayServicesNotAvailableException e) {}

            GoogleMap mapa = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            mapa.setMyLocationEnabled(true);

            titulo = (TextView) rootView.findViewById(R.id.tituloTV);
            descripcion = (TextView) rootView.findViewById(R.id.descripcionTV);

            titulo.setText(this.qdada.getTitulo());

            if ((this.qdada.getDescripcion() == null) || (this.qdada.getDescripcion().trim().isEmpty())){
                descripcion.setTypeface(null, Typeface.ITALIC);
                descripcion.setText(getString(R.string.nodescripcion));
            } else {
                descripcion.setText(this.qdada.getDescripcion());
            }

            try{
                mapa.addMarker(new MarkerOptions().position(new LatLng(qdada.getLatitud(), qdada.getLongitud())).title(qdada.getDireccion()));
                mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(qdada.getLatitud(), qdada.getLongitud()), 14.0f));
            } catch (Exception e){}

            // TODO: Capturar el click del marcador para preguntar si quiere que te muestre como llegar

            return rootView;
        }

}
