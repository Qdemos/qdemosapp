package com.cusl.ull.qdemos.fragments.edicion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.utilities.DatosQdada;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Paco on 7/01/14.
 * Fragment que se encarga de Loguear al usuario, es el splash screen inicial de login
 */
public class InfoFragment extends Fragment {

        EditText titulo, descripcion, direccion;
        View rootView;

        public InfoFragment() {
            // Se ejecuta antes que el onCreateView

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Empezar aqui a trabajar con la UI

            if (rootView != null) {
                ViewGroup parent = (ViewGroup) rootView.getParent();
                if (parent != null)
                    parent.removeView(rootView);
            }
            try {
                rootView = inflater.inflate(R.layout.fragment_edit_info, container, false);
            } catch (InflateException e) {}

            try {
                MapsInitializer.initialize(getActivity());
            } catch (GooglePlayServicesNotAvailableException e) {}

            GoogleMap mapa = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            mapa.setMyLocationEnabled(true);

            // TODO: Logica Mapa con autocompletado


//            mapa.addMarker(new MarkerOptions()
//                    .position(new LatLng(48.397141, 9.98787)).title(" Theatro Club Ulm"));
//
//            mapa.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(48.397141, 9.98787), 14.0f) );

            titulo = (EditText) rootView.findViewById(R.id.tituloET);
            descripcion = (EditText) rootView.findViewById(R.id.descripcionET);
            direccion = (EditText) rootView.findViewById(R.id.direccionET);


            titulo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    DatosQdada.setTitulo(titulo.getText().toString());
                }
            });

            descripcion.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

                @Override
                public void afterTextChanged(Editable s) {
                    DatosQdada.setDescripcion(descripcion.getText().toString());
                }
            });

            direccion.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

                @Override
                public void afterTextChanged(Editable s) {
                    DatosQdada.setDireccion(direccion.getText().toString());
                }
            });

            return rootView;
        }
}
