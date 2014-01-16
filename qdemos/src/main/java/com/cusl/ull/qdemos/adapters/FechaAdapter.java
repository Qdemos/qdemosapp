package com.cusl.ull.qdemos.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cusl.ull.qdemos.R;

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

        TextView fecha = (TextView) vi.findViewById(R.id.fecha);
        fecha.setText(item.toString());

        return vi;
    }

    public void addItem (Date fecha){
        this.items.add(fecha);
    }

    public void deleteItem (int position){
        this.items.remove(position);
    }

}