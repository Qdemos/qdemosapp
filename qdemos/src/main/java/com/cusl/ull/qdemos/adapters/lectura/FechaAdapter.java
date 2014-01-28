package com.cusl.ull.qdemos.adapters.lectura;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.utilities.EleccionFecha;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 16/01/14.
 */
public class FechaAdapter extends BaseAdapter {

    protected Activity activity;
    protected List<Date> items;
    ImageButton si, no;

    public FechaAdapter(Activity activity, List<Date> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return (items == null) ? 0 : items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.item_fecha_eleccion_list, null);
        }

        Date item = items.get(position);

        CalendarView fecha = (CalendarView) vi.findViewById(R.id.dateView);
        fecha.setDate(item.getTime());
        fecha.setEnabled(false);

        TextView hora = (TextView) vi.findViewById(R.id.horaTV);
        hora.setText(new SimpleDateFormat("HH:mm").format(item));

        si = (ImageButton) vi.findViewById(R.id.accept_qdada);
        no = (ImageButton) vi.findViewById(R.id.deny_qdada);

        if ((EleccionFecha.elecciones.get(item) != null) && (EleccionFecha.elecciones.get(item))){
            si.setSelected(true);
            no.setSelected(false);
        } else if (EleccionFecha.elecciones.get(item) != null){
            no.setSelected(true);
            si.setSelected(false);
        } // Sino: No seteado aun, no ha respondido

        si.setTag(item);
        si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                si.setSelected(true);
                no.setSelected(false);
                EleccionFecha.setSelectedFecha(((Date) view.getTag()), true);
                notifyDataSetChanged();
            }
        });

        no.setTag(item);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                no.setSelected(true);
                si.setSelected(false);
                EleccionFecha.setSelectedFecha(((Date)view.getTag()), false);
                notifyDataSetChanged();
            }
        });

        return vi;
    }

}