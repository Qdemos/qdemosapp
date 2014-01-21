package com.cusl.ull.qdemos.fragments.edicion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cusl.ull.qdemos.adapters.edicion.FechaAdapter;
import com.cusl.ull.qdemos.utilities.DatosQdada;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import com.cusl.ull.qdemos.R;

import java.util.Calendar;

/**
 * Created by Paco on 7/01/14.
 * Fragment que se encarga de Loguear al usuario, es el splash screen inicial de login
 */
public class FechasFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

        public static final String DATEPICKER_TAG = "datepicker";
        public static final String TIMEPICKER_TAG = "timepicker";

        TimePickerDialog timePickerDialog = null;
        DatePickerDialog datePickerDialog = null;
        Calendar calendar = null;
        int year, month, day;
        ListView listView;
        FechaAdapter fechaAdapter;

        public FechasFragment() {
            // Se ejecuta antes que el onCreateView
            DatosQdada.reset(getActivity());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_edit_fechas, container, false);
            // Empezar aqui a trabajar con la UI

            calendar = Calendar.getInstance();

            datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
            timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);

            rootView.findViewById(R.id.nuevaFecha).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerDialog.setYearRange(calendar.get(Calendar.YEAR), 2023);
                    datePickerDialog.show(getFragmentManager(), DATEPICKER_TAG);
                }
            });

            listView = (ListView) rootView.findViewById(R.id.listaFechas);

            fechaAdapter = new FechaAdapter(getActivity(), DatosQdada.getFechas());
            listView.setAdapter(fechaAdapter);
            listView.setClickable(false);

            return rootView;
        }

        @Override
        public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
            this.year=year;
            this.month=month;
            this.day=day;
            timePickerDialog.show(getFragmentManager(), TIMEPICKER_TAG);
        }

        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
            DatosQdada.setNuevaFecha(year, month, day, hourOfDay, minute);
            fechaAdapter.notifyDataSetChanged();
        }
}
