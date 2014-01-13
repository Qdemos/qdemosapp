package com.cusl.ull.qdemos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

/**
 * Activity Incial que se encarga de redirigir a la pantalla principal si estamos logueados o de mostrar la pantalla de Login si no lo estamos.
 * Tambien se encarga de mostrar la pantalla de logout en caso de que estemos logueados y queramos cerrar sesion con Facebook.
 */

public class Inicio extends FragmentActivity {

    // Variables usadas para representar los fragments de Login, Pantalla principal (una vez logueado) y el LogOut.
    private static final int LOGIN = 0;
    private static final int PRINCIPAL = 1;
    private static final int LOGOUT = 2;
    private static final int FRAGMENT_COUNT = LOGOUT+1;

    // Item del menu que permitira cerrar sesion en Facebook
    private MenuItem logout;

    // Variables para controlar los fragments, cual esta activo, etc.
    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    private boolean isResumed = false;

    // Variables para controlar el ciclo de vida de la sesion de Facebook
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ciclo de vida de la sesion de autenticacion de Facebook
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        // Inicializamos cosas propias de la app, como la BBDD
        initConfigApp();

        setContentView(R.layout.activity_inicio);

        FragmentManager fm = getSupportFragmentManager();
        fragments[LOGIN] = fm.findFragmentById(R.id.splashFragment);
        fragments[PRINCIPAL] = fm.findFragmentById(R.id.selectionFragment);
        fragments[LOGOUT] = fm.findFragmentById(R.id.logOutFragment);

        // Ocultamos todos los fragments que maneja nuestra activity para despues ir mostrando y ocultando segun se vaya pidiendo por parte del usuario
        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();
    }

    public void initConfigApp (){
        BBDD.initBBDD(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Session session = Session.getActiveSession();

        // Cada vez que la app se ponga en primer plano, comprobaremos si nuestra sesión de FB aun sigue vigente, para entrar directamente. Si ha caducado o hemos hecho logout, mostramos el fragment de Login.
        if (session != null && session.isOpened()) {
            // if the session is already open, try to show the selection fragment
            if (!getActionBar().isShowing())
                getActionBar().show();
            showFragment(PRINCIPAL, false);
        }  else {
            // otherwise present the splash screen and ask the user to login, unless the user explicitly skipped.
            if (getActionBar().isShowing())
                getActionBar().hide();
            showFragment(LOGIN, false);
        }
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        // Si hacemos login o logout, el estado de la sesion cambiará, por lo que en esta función se controla y se muestra el fragment correspondiente en funcion de si la sesión esta abierta o cerrada.
        if (isResumed) {
            FragmentManager manager = getSupportFragmentManager();
            int backStackSize = manager.getBackStackEntryCount();
            for (int i = 0; i < backStackSize; i++) {
                manager.popBackStack();
            }
            // check for the OPENED state instead of session.isOpened() since for the
            // OPENED_TOKEN_UPDATED state, the selection fragment should already be showing.
            if (state.equals(SessionState.OPENED)) {
                if (!getActionBar().isShowing())
                    getActionBar().show();
                showFragment(PRINCIPAL, false);
            } else if (state.isClosed()) {
                if (getActionBar().isShowing())
                    getActionBar().hide();
                showFragment(LOGIN, false);
            }
        }
    }

    // Función que a partir del ID (0: Login, 1: Principal, 2: Logout), muestra el fragment correspondiente, ocultando el resto.
    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    // Funcion para manejar el Menu de la app, que en este caso es el que nos permitira hacer Logout
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // only add the menu when the selection fragment is showing
        if (fragments[PRINCIPAL].isVisible()) {
            if (menu.size() == 0) {
                logout = menu.add(R.string.logout);
            }
            return true;
        } else {
            menu.clear();
            logout = null;
        }
        return false;
    }

    // Si se ha clickado en la opcion de cerrar sesión del menu, mostraremos el fragment que nos permitirá cerrar la sesión.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.equals(logout)) {
            if (!getActionBar().isShowing())
                getActionBar().show();
            showFragment(LOGOUT, true);
            return true;
        }
        return false;
    }


}
