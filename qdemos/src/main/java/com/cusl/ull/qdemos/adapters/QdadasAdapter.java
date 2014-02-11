package com.cusl.ull.qdemos.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.utilities.BBDD;
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
public class QdadasAdapter extends BaseAdapter {
    protected Activity activity;
    protected List<Qdada> items;

    // Para saber que lista de quedadas usa el adaptardor
    // 0: Qdadas aceptadas
    // 1: Qdadas Pendientes (no respondidas)
    // 2: Qdadas pasadas (historico)
    protected int tipo;

    public QdadasAdapter(Activity activity, List<Qdada> items, int tipo) {
        this.activity = activity;
        this.items = items;
        this.tipo = tipo;
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
        confirmados.setText(String.valueOf(BBDD.numeroParticipantes(vi.getContext(), qdada.getIdQdada())));

        // El más uno es para sumar tambien la 'auto'-invitación del creador
        TextView invitados = (TextView) vi.findViewById(R.id.invitadasTV);
        invitados.setText(String.valueOf(qdada.getInvitados().size()+1));

        if (qdada.getFechaGanadora() != null){
            View miEstado = (View) vi.findViewById(R.id.miEstadoQdada);
            if (tipo == 0){
                // Miramos el SDK para ver la version de Android en la que esta corriendo la app y asi saber que funcion usar para setear el color de si la fecha de la qdada es una de las que elegimos o no
                // ya que a partir de las version 16 de Android (4.1) la funcion ha cambiado y para versiones anteriores se debe seguir usando la antigua, que ahora es deprecated.
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (BBDD.soyParticipante(vi.getContext(), qdada.getIdQdada(), BBDD.quienSoy(vi.getContext()).getIdfacebook(), qdada.getFechaGanadora())){
                    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        miEstado.setBackgroundDrawable(vi.getResources().getDrawable(R.drawable.circle_green));
                    } else {
                        miEstado.setBackground(vi.getResources().getDrawable(R.drawable.circle_green));
                    }
                } else{
                    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        miEstado.setBackgroundDrawable(vi.getResources().getDrawable(R.drawable.circle_red));
                    } else {
                        miEstado.setBackground(vi.getResources().getDrawable(R.drawable.circle_red));
                    }
                }
            } else {
                miEstado.setVisibility(View.GONE);
            }
            CalendarView fecha = (CalendarView) vi.findViewById(R.id.dateView);
            fecha.setDate(qdada.getFechaGanadora().getTime());
            fecha.setEnabled(false);
            TextView hora = (TextView) vi.findViewById(R.id.horaTV);
            hora.setText(new SimpleDateFormat("HH:mm").format(qdada.getFechaGanadora()));
        }

        return vi;
    }


}