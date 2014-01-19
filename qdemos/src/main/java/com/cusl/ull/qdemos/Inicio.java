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
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

/**
 * Activity Incial que se encarga de redirigir a la pantalla principal si estamos logueados o de mostrar la pantalla de Login si no lo estamos.
 * Tambien se encarga de mostrar la pantalla de logout en caso de que estemos logueados y queramos cerrar sesion con Facebook.
 */

public class Inicio extends FragmentActivity {

    // Variables usadas para representar los fragments de Login, Pantalla principal (una vez logueado) y el LogOut.
    private static final int LOGIN = 0;
    private static final int FRAGMENT_COUNT = LOGIN+1;

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

        // Ocultamos todos los fragments que maneja nuestra activity para despues mostrarlo si procede o saltar a la siguiente activity.
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(fragments[LOGIN]);
        transaction.commit();
        getActionBar().hide();
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
            goToHome(session);
        }  else {
            // otherwise present the splash screen and ask the user to login, unless the user explicitly skipped.
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
                goToHome(session);
            } else if (state.isClosed()) {
                showFragment(LOGIN, false);
            }
        }
    }

    // Función que a partir del ID (0: Login), muestra el fragment correspondiente, ocultando el resto.
    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.show(fragments[LOGIN]);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    // Funcion encargada de redireccionar a la pantalla principal de usuario una vez logueado correctamente en Facebook
    public void goToHome(final Session session){
        // Obtenemos nuestros datos para crear el usuario que nos represente
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                BBDD.crearUsuarioIfNotExist(getApplicationContext(), user.getName(), user.getId());
                            }
                        }
                        if (response.getError() != null) {
                            // Handle errors, will do so later.
                        }
                    }
                });
        request.executeAsync();
        // Empezar aqui a trabajar con la UI
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

}
