package com.cusl.ull.qdemos.fragments.edicion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cusl.ull.qdemos.R;
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

        public InfoFragment() {
            // Se ejecuta antes que el onCreateView

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_edit_info, container, false);
            // Empezar aqui a trabajar con la UI

            try {
                MapsInitializer.initialize(getActivity());
            } catch (GooglePlayServicesNotAvailableException e) {}

            GoogleMap mapa = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            mapa.setMyLocationEnabled(true);


//            mapa.addMarker(new MarkerOptions()
//                    .position(new LatLng(48.397141, 9.98787)).title(" Theatro Club Ulm"));
//
//            mapa.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(48.397141, 9.98787), 14.0f) );
            return rootView;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            try {
                Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.remove(fragment);
                ft.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
