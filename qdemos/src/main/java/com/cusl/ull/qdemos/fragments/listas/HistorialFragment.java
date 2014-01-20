package com.cusl.ull.qdemos.fragments.listas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cusl.ull.qdemos.R;

/**
 * Created by Paco on 7/01/14.
 * Fragment que se encarga de Loguear al usuario, es el splash screen inicial de login
 */
public class HistorialFragment extends Fragment {

        public HistorialFragment() {
            // Se ejecuta antes que el onCreateView

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_qdadas, container, false);
            // Empezar aqui a trabajar con la UI
            return rootView;
        }
}
