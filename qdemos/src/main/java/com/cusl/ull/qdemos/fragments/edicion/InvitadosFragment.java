package com.cusl.ull.qdemos.fragments.edicion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.adapters.FechaAdapter;
import com.cusl.ull.qdemos.adapters.InvitadosAdapter;
import com.cusl.ull.qdemos.utilities.DatosQdada;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Paco on 7/01/14.
 * Fragment que se encarga de Loguear al usuario, es el splash screen inicial de login
 */
public class InvitadosFragment extends Fragment {

        private static final int PICK_FRIENDS_ACTIVITY = 1;
        private Button pickFriendsButton;
        private TextView resultsTextView;
        private UiLifecycleHelper lifecycleHelper;
        boolean pickFriendsWhenSessionOpened;
        InvitadosAdapter invitadosAdapter;

        public InvitadosFragment() {
            // Se ejecuta antes que el onCreateView

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_edit_invitados, container, false);
            // Empezar aqui a trabajar con la UI

            pickFriendsButton = (Button) rootView.findViewById(R.id.nuevosInvitados);
            pickFriendsButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    onClickPickFriends();
                }
            });

            lifecycleHelper = new UiLifecycleHelper(getActivity(), new Session.StatusCallback() {
                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    onSessionStateChanged(session, state, exception);
                }
            });
            lifecycleHelper.onCreate(savedInstanceState);

            ensureOpenSession();

            ListView listView = (ListView) rootView.findViewById(R.id.listaInvitados);

            invitadosAdapter = new InvitadosAdapter(getActivity(), DatosQdada.selectedUsers);
            listView.setAdapter(invitadosAdapter);
            listView.setClickable(false);

            return rootView;
        }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_FRIENDS_ACTIVITY:
                displaySelectedFriends(resultCode);
                break;
            default:
                Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
                break;
        }
    }

    private void displaySelectedFriends(int resultCode) {
        invitadosAdapter.addAllItem(DatosQdada.getSelectedUsers());
        invitadosAdapter.notifyDataSetChanged();
    }

    private boolean ensureOpenSession() {
        if (Session.getActiveSession() == null ||
                !Session.getActiveSession().isOpened()) {
            Session.openActiveSession(getActivity(), true, new Session.StatusCallback() {
                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    onSessionStateChanged(session, state, exception);
                }
            });
            return false;
        }
        return true;
    }

    private void onSessionStateChanged(Session session, SessionState state, Exception exception) {
        if (pickFriendsWhenSessionOpened && state.isOpened()) {
            pickFriendsWhenSessionOpened = false;

            startPickFriendsActivity();
        }
    }

    private void onClickPickFriends() {
        startPickFriendsActivity();
    }

    private void startPickFriendsActivity() {
        if (ensureOpenSession()) {
            Intent intent = new Intent(getActivity(), PickFriendsFragmentActivity.class);
            // Note: The following line is optional, as multi-select behavior is the default for
            // FriendPickerFragment. It is here to demonstrate how parameters could be passed to the
            // friend picker if single-select functionality was desired, or if a different user ID was
            // desired (for instance, to see friends of a friend).
            PickFriendsFragmentActivity.populateParameters(intent, null, true, true);
            startActivityForResult(intent, PICK_FRIENDS_ACTIVITY);
        } else {
            pickFriendsWhenSessionOpened = true;
        }
    }
}
