package com.example.ehComplicado;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import model.bean.Campeonato;

public class CampeonatosAdapter extends BaseAdapter {

    private final Context ctx;
    private final List<Campeonato> campeonatos;

    CampeonatosAdapter(Context ctx, List<Campeonato> campeonatos) {
        this.ctx = ctx;
        this.campeonatos = campeonatos;
    }

    @Override
    public int getCount() {
        return campeonatos.size();
    }

    @Override
    public Object getItem(int position) {
        return campeonatos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Campeonato campeonato = campeonatos.get(position);
        if (convertView == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(ctx);
            convertView = layoutInflater.inflate(R.layout.linearlayout_camp,null);
        }
        final TextView nomeCamp = convertView.findViewById(R.id.nomeCamp);
        final TextView anoFormatoCamp = convertView.findViewById(R.id.anoFormatoCamp);
        final TextView situacao = convertView.findViewById(R.id.txtSituacao);
        if (campeonato.isIniciado()){
            situacao.setText(R.string.ftc_em_and);
            situacao.setTextColor(Color.GREEN);
        }else if (campeonato.isFinalizado()){
            situacao.setText(R.string.ftc_finalizado);
            situacao.setTextColor(Color.RED);
        }else if(campeonato.isFaseDeGrupos() || campeonato.isOitavas() || campeonato.isQuartas() || campeonato.isSemi() || campeonato.isFinal()){
            situacao.setText(R.string.ftc_td_ok);
            situacao.setTextColor(Color.BLUE);
        }else{
            situacao.setText(R.string.ftc_nao_ini);
        }
        nomeCamp.setText(campeonato.getNome());
        String builder = campeonato.getAno() +
                " - " +
                campeonato.getFormato();
        anoFormatoCamp.setText(builder);
        return convertView;
    }
}
