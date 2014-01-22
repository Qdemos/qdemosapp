package com.cusl.ull.qdemos.adapters;

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
import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.utilities.Utilities;
import com.facebook.widget.ProfilePictureView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 16/01/14.
 */
public class QdadasAceptadasAdapter extends BaseAdapter {
    protected Activity activity;
    protected List<Qdada> items;

    public QdadasAceptadasAdapter(Activity activity, List<Qdada> items) {
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
            vi = inflater.inflate(R.layout.item_qdada_list, null);
        }

        Qdada qdada = items.get(position);

        TextView titulo = (TextView) vi.findViewById(R.id.tituloQdada);
        titulo.setText(qdada.getTitulo());

        ProfilePictureView foto = (ProfilePictureView) vi.findViewById(R.id.fotoPerfil);
        foto.setProfileId(qdada.getCreador().getIdfacebook());

        TextView nombre = (TextView) vi.findViewById(R.id.nombrePerfil);
        nombre.setText(qdada.getCreador().getNombre());

        TextView confirmados = (TextView) vi.findViewById(R.id.confirmadasTV);
        confirmados.setText(String.valueOf(qdada.getParticipantes().size()));

        // El más uno es para sumar tambien la 'auto'-invitación del creador
        TextView invitados = (TextView) vi.findViewById(R.id.invitadasTV);
        invitados.setText(String.valueOf(qdada.getInvitados().size()+1));

        CalendarView fecha = (CalendarView) vi.findViewById(R.id.dateView);
        fecha.setDate(qdada.getFechaGanadora().getFecha().getTime());
        fecha.setEnabled(false);

        TextView hora = (TextView) vi.findViewById(R.id.horaTV);
        hora.setText(new SimpleDateFormat("HH:mm").format(qdada.getFechaGanadora().getFecha()));

        return vi;
    }


}