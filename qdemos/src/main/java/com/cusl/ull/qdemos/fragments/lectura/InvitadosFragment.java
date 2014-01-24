package com.cusl.ull.qdemos.fragments.lectura;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.adapters.edicion.InvitadosAdapter;
import com.cusl.ull.qdemos.adapters.lectura.InvitadoGridAdapter;
import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.fragments.edicion.PickFriendsFragmentActivity;
import com.cusl.ull.qdemos.utilities.DatosQdada;
import com.cusl.ull.qdemos.utilities.Utilities;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Paco on 7/01/14.
 * Fragment que se encarga de Loguear al usuario, es el splash screen inicial de login
 */
public class InvitadosFragment extends Fragment {

        Qdada qdada;
        GridView gridView;

    public InvitadosFragment(Qdada qdada) {
            // Se ejecuta antes que el onCreateView
            this.qdada = qdada;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_lectura_invitados, container, false);
            // Empezar aqui a trabajar con la UI

            gridView = (GridView) rootView.findViewById(R.id.listaInvitados);
            List<Usuario> invitados = new ArrayList<Usuario>();
            invitados.addAll(qdada.getInvitados());
            invitados.add(this.qdada.getCreador());
            Collections.sort(invitados, new Comparator<Usuario>() {
                public int compare(Usuario o1, Usuario o2) {
                    return o1.getNombre().compareTo(o2.getNombre());
                }
            });
            invitados=Utilities.ordenarInvitados(invitados, qdada.getParticipantes());

            InvitadoGridAdapter customGridAdapter = new InvitadoGridAdapter(getActivity(), R.layout.item_invitados_lectura_list, invitados, this.qdada);
            gridView.setAdapter(customGridAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Usuario user =  (Usuario) gridView.getItemAtPosition(i);
                    Crouton.cancelAllCroutons();
                    Crouton.makeText(getActivity(), user.getNombre(), Style.INFO).show();
                }
            });

            return rootView;
        }

}
