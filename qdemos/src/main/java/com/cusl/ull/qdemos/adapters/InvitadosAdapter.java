package com.cusl.ull.qdemos.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cusl.ull.qdemos.R;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 16/01/14.
 */
public class InvitadosAdapter extends BaseAdapter {
    protected Activity activity;
    protected List<GraphUser> items;

    public InvitadosAdapter(Activity activity, List<GraphUser> items) {
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
            vi = inflater.inflate(R.layout.item_invitados_list, null);
        }

        GraphUser item = items.get(position);

        ProfilePictureView foto = (ProfilePictureView) vi.findViewById(R.id.fotoPerfil);
        foto.setProfileId(item.getId());

        TextView nombre = (TextView) vi.findViewById(R.id.nombrePerfil);
        nombre.setText(item.getName());

        ImageButton eliminar = (ImageButton) vi.findViewById(R.id.eliminarInvitado);
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

    public void addAllItem (List<GraphUser> users){
        this.items = users;
        // Para ordenar la lista por nombre de usuario en FB y que aparezca en el layout (ListView) en orden

    }

    public void deleteItem (int position){
        this.items.remove(position);
        // Para ordenar la lista por por nombre de usuario en FB y que aparezca en el layout (ListView) en orden
        Collections.sort(this.items, new Comparator<GraphUser>() {
            public int compare(GraphUser o1, GraphUser o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

}