package com.cusl.ull.qdemos.fragments.edicion;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.interfaces.IStandardTaskListener;
import com.cusl.ull.qdemos.placeAutocomplete.DetailsPlaceOne;
import com.cusl.ull.qdemos.placeAutocomplete.FillPlace;
import com.cusl.ull.qdemos.utilities.DatosQdada;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

/**
 * Created by Paco on 7/01/14.
 * Fragment que se encarga de Loguear al usuario, es el splash screen inicial de login
 */
public class InfoFragment extends Fragment {

    EditText titulo, descripcion;
    AutoCompleteTextView direccion;
    View rootView;
    private DetailsPlaceOne geoLugar;
    private FillPlace buscarLugar;
    private Thread thread;
    private ProgressDialog pd;
    GoogleMap mapa;
    Location myLocation;
    RelativeLayout focus;

    public InfoFragment() {
        // Se ejecuta antes que el onCreateView

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Empezar aqui a trabajar con la UI

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_edit_info, container, false);
        } catch (InflateException e) {}

        try {
            MapsInitializer.initialize(getActivity());
            initMapa();
        } catch (GooglePlayServicesNotAvailableException e) {}

        titulo = (EditText) rootView.findViewById(R.id.tituloET);
        descripcion = (EditText) rootView.findViewById(R.id.descripcionET);
        direccion = (AutoCompleteTextView) rootView.findViewById(R.id.direccionET);
        focus = (RelativeLayout) rootView.findViewById(R.id.linearLayout_focus);

        initAutocompletadoDireccion();

        titulo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                DatosQdada.setTitulo(titulo.getText().toString());
            }
        });

        descripcion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void afterTextChanged(Editable s) {
                DatosQdada.setDescripcion(descripcion.getText().toString());
            }
        });

        direccion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void afterTextChanged(Editable s) {
                DatosQdada.setDireccion(direccion.getText().toString());
            }
        });

        return rootView;
    }

    public void initAutocompletadoDireccion(){
        final ArrayAdapter<String> adapterFrom = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        adapterFrom.setNotifyOnChange(true);
        direccion.setAdapter(adapterFrom);

        direccion.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                geoLugar = new DetailsPlaceOne();
                geoLugar.setContext(arg1.getContext());
                thread=  new Thread(){
                    @Override
                    public void run(){
                        try {
                            synchronized(this){
                                wait(700);
                            }
                        }
                        catch(InterruptedException ex){
                        }
                        pd.dismiss();

                        // Para que el foco no entorpezca en el Autocompletado, una vez el usuario ha seleccionado la direccion
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                focus.requestFocus();
                            }
                        });

                        if ((buscarLugar.referencesPlace.isEmpty()) || (buscarLugar.referencesPlace.get(direccion.getText().toString()) == null)){
                            showToastError();
                        } else {
                            geoLugar.setListener(new PlaceToPointMap_TaskListener(direccion.getText().toString()));
                            geoLugar.execute(buscarLugar.referencesPlace.get(direccion.getText().toString()));
                            DatosQdada.setDireccion(direccion.getText().toString());
                        }
                    }
                };
                pd = ProgressDialog.show(getActivity(), getResources().getText(R.string.procesando), getResources().getText(R.string.esperar));
                pd.setIndeterminate(false);
                pd.setCancelable(true);
                thread.start();
            }
        });

        // Monitorizamos el evento de cambio en el campo a autocompletar para buscar las propuestas de autocompletado
        direccion.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Calculamos el autocompletado
                if (count%3 == 1){
                    adapterFrom.clear();
                    // Ejecutamos en segundo plano la busqueda de propuestas de autocompletado
                    buscarLugar = new FillPlace(adapterFrom, direccion, getActivity());
                    buscarLugar.execute(direccion.getText().toString());
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });
    }

    public void showToastError(){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), R.string.error_problemautocompletado, Toast.LENGTH_LONG).show();
            }
        });
    }

    private class PlaceToPointMap_TaskListener implements IStandardTaskListener {

        private String markerStr;

        public PlaceToPointMap_TaskListener(String markerStr) {
            this.markerStr =  markerStr;
        }

        @Override
        public void taskComplete(Object result) {
            if ((Boolean)result){
                Double lat, lng;
                // Esconder teclado para que se vea la animación del mapa
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
                // Recogemos la posicion que hemos pedido al Web Service de Google Place
                lat = geoLugar.coordinates.get("lat");
                lng = geoLugar.coordinates.get("lng");
                if ((lat == null) || (lng == null))
                    return;
                else{
                    DatosQdada.setLatitud(lat);
                    DatosQdada.setLongitud(lng);
                }
                LatLng go = new LatLng(lat, lng);

                direccion.setText(markerStr);

                // Creamos la animación de movimiento hacia el lugar que hemos introducido
                CameraPosition camPos = new CameraPosition.Builder()
                        .target(go)
                        .zoom(16)         //Establecemos el zoom en 17
                        .tilt(45)         //Bajamos el punto de vista de la cámara 70 grados
                        .build();

                // Lanzamos el movimiento
                CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(camPos);
                mapa.animateCamera(camUpd);

                mapa.clear();

                mapa.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title(markerStr)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

            }
        }
    }

    private void initMapa(){

        mapa = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mapa==null)
            return;
        mapa.setMyLocationEnabled(true);
        mapa.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                myLocation=arg0;
            }
        });

        mapa.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (myLocation != null){
                    try {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
                        Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
                        Double lat = myLocation.getLatitude();
                        Double lng = myLocation.getLongitude();
                        List<Address> addresses = geoCoder.getFromLocation(lat, lng, 1);
                        String add = "";
                        if (addresses.size() > 0){
                            for (int i=0; i<addresses.get(0).getMaxAddressLineIndex();i++)
                                add += addresses.get(0).getAddressLine(i) + " ";
                        }
                        LatLng go = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        CameraPosition camPos = new CameraPosition.Builder()
                                .target(go)   //Centramos el mapa
                                .zoom(16)         //Establecemos el zoom
                                .tilt(45)         //Bajamos el punto de vista de la cÃ¡mara
                                .build();

                        CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(camPos);
                        mapa.animateCamera(camUpd);
                        mapa.clear();

                        direccion.setText(add);
                        DatosQdada.setDireccion(add);
                        DatosQdada.setLongitud(lng);
                        DatosQdada.setLatitud(lat);

                        mapa.clear();

                        mapa.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .title(add)
                                .draggable(true)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                    } catch (Exception e) {}
                }
                return false;
            }
        });

        mapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng point) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
                Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    List<Address> addresses = geoCoder.getFromLocation(point.latitude, point.longitude, 1);
                    String add = "";
                    if (addresses.size() > 0){
                        for (int i=0; i<addresses.get(0).getMaxAddressLineIndex();i++)
                            add += addresses.get(0).getAddressLine(i) + " ";
                    }
                    direccion.setText(add);
                    DatosQdada.setDireccion(add);
                    DatosQdada.setLongitud(point.longitude);
                    DatosQdada.setLatitud(point.latitude);

                    mapa.clear();

                    // Insertamos el Marcador
                    mapa.addMarker(new MarkerOptions()
                            .position(new LatLng(point.latitude, point.longitude))
                            .title(add)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                } catch (Exception e) {
                }

            }
        });

        mapa.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.hideInfoWindow();
                marker.setTitle("");
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng newPosition = marker.getPosition();
                Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    List<Address> addresses = geoCoder.getFromLocation(newPosition.latitude, newPosition.longitude, 1);
                    String add = "";
                    if (addresses.size() > 0){
                        for (int i=0; i<addresses.get(0).getMaxAddressLineIndex();i++)
                            add += addresses.get(0).getAddressLine(i) + " ";
                    }
                    mapa.clear();

                    mapa.addMarker(new MarkerOptions()
                            .position(new LatLng(newPosition.latitude, newPosition.longitude))
                            .title(add)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                    direccion.setText(add);
                    DatosQdada.setDireccion(add);
                    DatosQdada.setLongitud(newPosition.longitude);
                    DatosQdada.setLatitud(newPosition.latitude);

                } catch (Exception e) {
                }
            }

            @Override
            public void onMarkerDrag(Marker marker) {}
        });
    }
}
