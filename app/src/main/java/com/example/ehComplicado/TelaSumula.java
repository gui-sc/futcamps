package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ehComplicado.FirebaseHelper.AmareloHelper;
import com.example.ehComplicado.FirebaseHelper.GolHelper;
import com.example.ehComplicado.FirebaseHelper.VermelhoHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import model.bean.CAmarelo;
import model.bean.CVermelho;
import model.bean.Campeonato;
import model.bean.Gols;
import model.bean.Partida;


public class TelaSumula extends Fragment {
    DatabaseReference partidaRef, campRef;
    ValueEventListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_sumula, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView lblEstadio = view.findViewById(R.id.lbl_estadio);
        final TextView lblData = view.findViewById(R.id.lbl_data);
        final TextView lblMandante = view.findViewById(R.id.lbl_mandante);
        final TextView lblVisitante = view.findViewById(R.id.lbl_visitante);
        final TextView lblPlacarMandante = view.findViewById(R.id.lbl_placar_mandante);
        final TextView lblPlacarVisitante = view.findViewById(R.id.lbl_placar_visitante);
        final TextView lblPenaltiMandante = view.findViewById(R.id.lbl_penaltis_mandante);
        final TextView lblPenaltiVisitante = view.findViewById(R.id.lbl_penaltis_visitante);
        final ListView lstGols = view.findViewById(R.id.ftc_lista_gols);
        final ListView lstCa = view.findViewById(R.id.ftc_lista_ca);
        final ListView lstCv = view.findViewById(R.id.ftc_lista_cv);
        final TextView lblObs = view.findViewById(R.id.lbl_obs);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String campKey = getArguments().getString("campKey");
        String partidaKey = getArguments().getString("partidaKey");
        Partida partida = getArguments().getParcelable("partida");
        campRef = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        partidaRef = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-partidas").child(campKey).child(partidaKey);
        DatabaseReference amareloReference = FirebaseDatabase.getInstance().getReference()
                .child("partida-amarelos").child(partidaKey);
        DatabaseReference vermelhoReference = FirebaseDatabase.getInstance().getReference()
                .child("partida-vermelhos").child(partidaKey);
        DatabaseReference golReference = FirebaseDatabase.getInstance().getReference()
                .child("partida-gols").child(partidaKey);
        final AmareloHelper amareloHelper = new AmareloHelper(amareloReference);
        final VermelhoHelper vermelhoHelper = new VermelhoHelper(vermelhoReference);
        final GolHelper golHelper = new GolHelper(golReference);


        lblEstadio.setText(partida.getLocal());
        lblData.setText(partida.getData());
        lblMandante.setText(partida.getNomeMandante());
        lblPlacarMandante.setText(String.valueOf(partida.getPlacarMandante()));
        lblPlacarVisitante.setText(String.valueOf(partida.getPlacarVisitante()));
        lblVisitante.setText(partida.getNomeVisitante());
        String string = lblObs.getText() + " " + partida.getObservacoes();
        lblObs.setText(string);
        if (partida.getPenaltisMandante() != 0 && partida.getPenaltisVisitante() != 0) {
            lblPenaltiMandante.setVisibility(View.VISIBLE);
            lblPenaltiVisitante.setVisibility(View.VISIBLE);
            lblPenaltiMandante.setText(String.valueOf(partida.getPenaltisMandante()));
            lblPenaltiVisitante.setText(String.valueOf(partida.getPenaltisVisitante()));
        }

        final Button btnJogos = view.findViewById(R.id.jogos_button);
        btnJogos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelaJogos t = new TelaJogos();
                Bundle data = new Bundle();
                data.putString("campKey", campKey);
                t.setArguments(data);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, t);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        final List<Gols> gols = golHelper.retrive();
        final List<CAmarelo> amarelos = amareloHelper.retrive();
        final List<CVermelho> vermelhos = vermelhoHelper.retrive();
        final ArrayAdapter<Gols> golsAdapter = new ArrayAdapter<>(getContext(), R.layout.personalizado_list_item, gols);
        final ArrayAdapter<CAmarelo> amareloAdapter = new ArrayAdapter<>(getContext(), R.layout.personalizado_list_item, amarelos);
        final ArrayAdapter<CVermelho> vermelhoAdapter = new ArrayAdapter<>(getContext(), R.layout.personalizado_list_item, vermelhos);

      ValueEventListener eventListener1 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          Campeonato camp = dataSnapshot.getValue(Campeonato.class);
          if (user.getUid().equals(camp.getUid())) {
            btnJogos.setVisibility(View.INVISIBLE);
            btnJogos.setClickable(false);
          }
          lstCa.setAdapter(amareloAdapter);
          lstCv.setAdapter(vermelhoAdapter);
          lstGols.setAdapter(golsAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
      };
      campRef.addListenerForSingleValueEvent(eventListener1);
      listener = eventListener1;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(listener != null){
          campRef.removeEventListener(listener);
        }
    }
}
