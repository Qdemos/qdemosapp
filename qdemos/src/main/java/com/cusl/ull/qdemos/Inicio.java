package com.cusl.ull.qdemos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.cusl.ull.qdemos.utilities.Utilities;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Activity Incial que se encarga de redirigir a la pantalla principal si estamos logueados o de mostrar la pantalla de Login si no lo estamos.
 * Tambien se encarga de mostrar la pantalla de logout en caso de que estemos logueados y queramos cerrar sesion con Facebook.
 */

public class Inicio extends FragmentActivity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    static final String TAG = "GCM";
    GoogleCloudMessaging gcm;
    String regid;
    Context context;
    ProgressDialog pd;

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

        context = getApplicationContext();

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

        // TODO: ¿Cómo se podría hacer para implementar MultiUsuario?... de esta manera sólo vale para un único usuario...
        com.cusl.ull.qdemos.server.Utilities.sincronizarBBDD(this);

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;
        // Check device for Play Services APK.
        checkPlayServices();
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

            if (!Utilities.haveInternet(context)){
                Toast.makeText(context, getString(R.string.conexion), Toast.LENGTH_LONG).show();
            } else {
                if (getRegistrationId(context).isEmpty()) {
                    pd = ProgressDialog.show(this, getResources().getText(R.string.esperar), getResources().getText(R.string.inicializando));
                    pd.setIndeterminate(false);
                    pd.setCancelable(true);
                }
            }
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
                                BBDD.crearUsuarioIfNotExist(getApplicationContext(), user.getName(), user.getId(), getRegistrationId(context));
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

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(context.getString(R.string.api_id_gcm));
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBBDD(regid);

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                    if (pd != null)
                        pd.dismiss();
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
            }
        }.execute(null, null, null);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(Inicio.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBBDD(String idGCM) {
        // Cuando cree el usuario no tenia todavia el IDGCM, asi que toca actualizarlo tanto en local como en el servidor
        Usuario usuario = BBDD.quienSoy(context);
        if (usuario == null)
            return;
        if (usuario.getIdgcm().equals("1")){
            BBDD.updateIdGCM(context, usuario, idGCM);
        }
    }

}
