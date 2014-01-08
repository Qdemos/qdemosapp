package com.cusl.ull.qdemos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Settings;
import com.facebook.model.GraphUser;

/**
 * Created by Paco on 7/01/14.
 * Fragment que se encarga de mostrar la primera pantalla que ve el usuario si esta logueado.
 */
public class PrincipalFragment extends Fragment {

        public PrincipalFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_principal, container, false);
            // Empezar aqui a trabajar con la UI
            return rootView;
        }
}
