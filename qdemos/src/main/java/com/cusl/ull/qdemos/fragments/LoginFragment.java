package com.cusl.ull.qdemos.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cusl.ull.qdemos.R;
import com.facebook.*;
import com.facebook.model.*;

/**
 * Created by Paco on 7/01/14.
 * Fragment que se encarga de Loguear al usuario, es el splash screen inicial de login
 */
public class LoginFragment extends Fragment {

        public LoginFragment() {
            // Se ejecuta antes que el onCreateView

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            // Empezar aqui a trabajar con la UI
            return rootView;
        }
}
