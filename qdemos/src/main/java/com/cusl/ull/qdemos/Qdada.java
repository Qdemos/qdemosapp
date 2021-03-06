package com.cusl.ull.qdemos;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cusl.ull.qdemos.bbdd.models.UsuarioEleccion;
import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
import com.cusl.ull.qdemos.dialogs.DatosSinGuardarDialog;
import com.cusl.ull.qdemos.fragments.edicion.FechasFragment;
import com.cusl.ull.qdemos.fragments.edicion.InfoFragment;
import com.cusl.ull.qdemos.fragments.edicion.InvitadosFragment;
import com.cusl.ull.qdemos.utilities.DatosQdada;
import com.cusl.ull.qdemos.utilities.EleccionFecha;
import com.cusl.ull.qdemos.utilities.Utilities;
import com.mobandme.ada.Entity;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class Qdada extends FragmentActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qdada);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }

        boolean isEdition = getIntent().getExtras().getBoolean("edicion");
        if (!isEdition)
            getActionBar().setTitle(R.string.title_qdada_lectura);
        else
            DatosQdada.reset(this);
    }


    // Sobreescribimos el metodo que controla el evento que se lanza cuando se pulsa sobre el botón atrás del móvil, para ver si hay datos que no se han persistido, avisar al usuario.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Utilities.hayDatosSinGuardar(this)){
                FragmentManager fragmentManager = getSupportFragmentManager();
                DatosSinGuardarDialog dialogo = new DatosSinGuardarDialog();
                dialogo.show(fragmentManager, "tagAlerta");
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.qdada, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_guardar) {
            boolean isEdition = getIntent().getExtras().getBoolean("edicion");
            if ((isEdition) && (validacionDatos())){
                ProgressDialog pd = ProgressDialog.show(this, getResources().getText(R.string.esperar), getResources().getText(R.string.procesando));
                pd.setIndeterminate(false);
                pd.setCancelable(false);
                com.cusl.ull.qdemos.server.Utilities.crearQdada((Activity) this, pd);
            } else if (!isEdition){
                List<Date> fechas = EleccionFecha.getFechas();
                String qdadajson = getIntent().getExtras().getString("qdadajson");
                com.cusl.ull.qdemos.bbdd.models.Qdada qdada = BBDD.getQdadaFromJSON(this, qdadajson);
                if (fechas == null){
                    Crouton.makeText(this, R.string.validar_fechas_invitado, Style.ALERT).show();
                } else if ((fechas.isEmpty()) && (qdada.getCreador().getIdfacebook().equals(BBDD.quienSoy(this).getIdfacebook()))){
                    Crouton.makeText(this, R.string.validar_fechas_creador, Style.ALERT).show();
                } else {
                    try {
                        BBDD.updateMiEleccion(this, qdada.getIdQdada(), BBDD.quienSoy(this).getIdfacebook(), fechas);
                    } catch (Exception e){
                        Toast.makeText(this, R.string.error_bbdd_r, Toast.LENGTH_LONG).show();
                    }
                }
            }
            return true;
        } else if (id == android.R.id.home){
            Intent intent = new Intent(this, Home.class);
            // Para eliminar el historial de activities visitadas ya que volvemos al HOME y asi el boton ATRAS no tenga ningun comportamiento, se resetee.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean validacionDatos(){
        if (DatosQdada.validarInfo(this) && DatosQdada.validarFechas(this) && DatosQdada.validarInvitados(this))
            return true;
        return false;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            boolean isEdition = getIntent().getExtras().getBoolean("edicion");
            switch (position) {
                case 0:
                    if (isEdition)
                        return new InfoFragment();
                    else {
                        String qdadajson = getIntent().getExtras().getString("qdadajson");
                        com.cusl.ull.qdemos.bbdd.models.Qdada datos = BBDD.getQdadaFromJSON(getApplicationContext(), qdadajson);
                        return new com.cusl.ull.qdemos.fragments.lectura.InfoFragment(datos);
                    }
                case 1:
                    if (isEdition)
                        return new FechasFragment();
                    else {
                        String qdadajson = getIntent().getExtras().getString("qdadajson");
                        boolean isHistorica = getIntent().getExtras().getBoolean("historica");
                        com.cusl.ull.qdemos.bbdd.models.Qdada datos = BBDD.getQdadaFromJSON(getApplicationContext(), qdadajson);
                        return new com.cusl.ull.qdemos.fragments.lectura.FechasFragment(datos, isHistorica);
                    }
                case 2:
                    if (isEdition)
                        return new InvitadosFragment();
                    else {
                        String qdadajson = getIntent().getExtras().getString("qdadajson");
                        com.cusl.ull.qdemos.bbdd.models.Qdada datos = BBDD.getQdadaFromJSON(getApplicationContext(), qdadajson);
                        return new com.cusl.ull.qdemos.fragments.lectura.InvitadosFragment(datos);
                    }
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section_info).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section_fechas).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section_invitados).toUpperCase(l);
            }
            return null;
        }
    }

}
