package com.cusl.ull.qdemos.adapters.edicion;

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
import com.cusl.ull.qdemos.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 16/01/14.
 */
public class FechaAdapter extends BaseAdapter {
    protected Activity activity;
    protected List<Date> items;

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
            vi = inflater.inflate(R.layout.item_fecha_list, null);
        }

        Date item = items.get(position);

        CalendarView fecha = (CalendarView) vi.findViewById(R.id.dateView);
        fecha.setDate(item.getTime());
        fecha.setEnabled(false);
        TextView hora = (TextView) vi.findViewById(R.id.horaTV);
        hora.setText(new SimpleDateFormat("HH:mm").format(item));

        ImageButton eliminar = (ImageButton) vi.findViewById(R.id.eliminar);
        // Usamos el SETTAG y despues el GETTAG para saber que botón ha sido pulsado de todos los items que son visibles, y así poder saber que item borrar.
        eliminar.setTag(position);
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getTag() instanceof Integer){
                    deleteItem(((Integer)view.getTag()));
                    notifyDataSetChanged();
                }
            }
        });

        return vi;
    }

    public void deleteItem (int position){
        this.items.remove(position);
        // Para ordenar la lista por fecha y que aparezca en el layout (ListView) en orden
        Collections.sort(this.items, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });
    }

}