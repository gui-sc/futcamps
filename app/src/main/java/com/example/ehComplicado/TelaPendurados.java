package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.ehComplicado.FirebaseHelper.JogadorHelper;
import com.example.ehComplicado.FirebaseHelper.TimeHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import model.bean.Jogador;
import model.bean.Time;
import model.dao.JogadorDAO;

public class TelaPendurados extends Fragment {
    DatabaseReference jogadoresReference, campReference, timeReference;
    TimeHelper timeHelper;
    JogadorHelper jogadorHelper;
    ValueEventListener campListener,jogadorListener;
    String campKey;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_pendurados,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TableLayout tbPendurados = view.findViewById(R.id.tabela_pendurados);

        user = FirebaseAuth.getInstance().getCurrentUser();
        campKey = getArguments().getString("campKey");
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);
        jogadoresReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-jogadores").child(campKey);

        timeHelper = new TimeHelper(timeReference);
        final List<Time> times = timeHelper.retrive();
        jogadorHelper = new JogadorHelper(jogadoresReference);
        final List<Jogador> jogadors = jogadorHelper.retrive();
        final JogadorDAO jogadorDAO = new JogadorDAO();
        ValueEventListener mJogadorListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Jogador>pendurados=jogadorDAO.listarPendurados(jogadors,times);
                for (Jogador jogador : pendurados){
                    View tr = getLayoutInflater().inflate(R.layout.tabela_cartoes_linha,null,false);
                    TextView txtNome = tr.findViewById(R.id.nome_jogador);
                    TextView txtTime = tr.findViewById(R.id.time);
                    txtNome.setText(jogador.getApelido());
                    txtTime.setText(jogador.getTime().getNome());
                    tbPendurados.addView(tr,new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        jogadoresReference.addListenerForSingleValueEvent(mJogadorListener);
        jogadorListener = mJogadorListener;

    }

    @Override
    public void onStop(){
        super.onStop();
        if (campListener != null){
            campReference.removeEventListener(campListener);
        }
        if (jogadorListener != null){
            jogadoresReference.removeEventListener(jogadorListener);
        }
    }

}