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
import model.bean.Campeonato;
import model.bean.Jogador;
import model.bean.Time;
import model.dao.JogadorDAO;

public class TelaSuspensos extends Fragment {
    private DatabaseReference jogadoresReference;
    private DatabaseReference campReference;
    private ValueEventListener jogadorListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TableLayout tbSuspensos = view.findViewById(R.id.tabela_suspensos);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String campKey = getArguments().getString("campKey");
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        DatabaseReference timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);
        jogadoresReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-jogadores").child(campKey);

        TimeHelper timeHelper = new TimeHelper(timeReference);
        final List<Time> times = timeHelper.retrive();
        JogadorHelper jogadorHelper = new JogadorHelper(jogadoresReference);
        final List<Jogador> jogadors = jogadorHelper.retrive();
        final JogadorDAO jogadorDAO = new JogadorDAO();
        ValueEventListener mJogadorListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Jogador>suspensos = jogadorDAO.listarSuspensosCamp(jogadors,times);
                for (Jogador jogador:suspensos){
                    View tr = getLayoutInflater().inflate(R.layout.tabela_cartoes_linha,null,false);
                    TextView txtNome = tr.findViewById(R.id.nome_jogador);
                    TextView txtTime = tr.findViewById(R.id.time);
                    txtNome.setText(jogador.getApelido());
                    txtTime.setText(jogador.getTime().getNome());
                    tbSuspensos.addView(tr,new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        jogadoresReference.addListenerForSingleValueEvent(mJogadorListener);
        jogadorListener = mJogadorListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_suspensos,container,false);
    }

    @Override
    public void onStop(){
        super.onStop();

        if (jogadorListener != null){
            jogadoresReference.removeEventListener(jogadorListener);
        }
    }
}
