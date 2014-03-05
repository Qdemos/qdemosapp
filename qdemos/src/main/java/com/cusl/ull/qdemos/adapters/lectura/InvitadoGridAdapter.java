package com.cusl.ull.qdemos.adapters.lectura;

/**
 * Created by Paco on 22/01/14.
 */
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.bbdd.models.Qdada;
import com.cusl.ull.qdemos.bbdd.models.Usuario;
import com.cusl.ull.qdemos.bbdd.models.UsuarioEleccion;
import com.cusl.ull.qdemos.utilities.Utilities;
import com.facebook.widget.ProfilePictureView;

/**
 *
 * @author manish.s
 *
 */
public class InvitadoGridAdapter extends ArrayAdapter<Usuario> {

    Context context;
    int layoutResourceId;
    List<Usuario> data = new ArrayList<Usuario>();
    Qdada qdada;

    public InvitadoGridAdapter(Context context, int layoutResourceId, List<Usuario> data, Qdada qdada) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.qdada = qdada;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.nombrePerfil);
            holder.imageItem = (ProfilePictureView) row.findViewById(R.id.fotoPerfil);
            holder.fondo = (RelativeLayout) row.findViewById(R.id.fondoInvitado);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        Usuario item = data.get(position);
        holder.txtTitle.setText(item.getNombre().split(" ")[0]);
        holder.imageItem.setProfileId(item.getIdfacebook());
        if ((Utilities.isParticipante(getContext(), qdada, item.getIdfacebook()) == null) && (!qdada.sinresponder)){ // Si ha respondido a todas las fechas con un NO
            holder.fondo.setBackgroundResource(R.drawable.background_noparticipante_usuario_qdadas);
        } else if (Utilities.isParticipante(getContext(), qdada, item.getIdfacebook()) == null) { // Si aún no ha respondido a las fechas
            holder.fondo.setBackgroundResource(R.drawable.background_invitados_qdadas);
        } else if (Utilities.isParticipante(getContext(), qdada, item.getIdfacebook())){ // Si ha elegido una fecha que de momentoe s la ganadora
            holder.fondo.setBackgroundResource(R.drawable.background_participante_usuario_qdadas);
        } else { // Si ha elegido una fecha que de momento NO es ganadora
            holder.fondo.setBackgroundResource(R.drawable.background_noparticipante_usuario_qdadas);
        }
        return row;

    }

    static class RecordHolder {
        TextView txtTitle;
        ProfilePictureView imageItem;
        RelativeLayout fondo;
    }
}
