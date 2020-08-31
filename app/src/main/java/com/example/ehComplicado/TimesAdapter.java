package com.example.ehComplicado;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import model.bean.Time;

public class TimesAdapter extends BaseAdapter {

    private Context ctx;
    private List<Time>times;

    public TimesAdapter(Context ctx, List<Time> times){
        this.ctx = ctx;
        this.times = times;
    }

    @Override
    public int getCount() {
        return times.size();
    }

    @Override
    public Object getItem(int position) {
        return times.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Time time = times.get(position);
        if (convertView == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(ctx);
            convertView = layoutInflater.inflate(R.layout.linear_layout_time,null);
        }
        final TextView nomeCamp = convertView.findViewById(R.id.nomeCamp);
        final TextView anoFormatoCamp = convertView.findViewById(R.id.anoFormatoCamp);
        nomeCamp.setText(time.getNome());
        anoFormatoCamp.setText(time.getDirigente());
        return convertView;
    }
}
