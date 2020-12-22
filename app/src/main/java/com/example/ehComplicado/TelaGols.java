package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ehComplicado.FirebaseHelper.JogadorHelper;
import com.example.ehComplicado.FirebaseHelper.TimeHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import model.bean.Gols;
import model.bean.Jogador;
import model.bean.Partida;
import model.bean.Time;
import model.dao.GolsDAO;
import model.dao.JogadorDAO;

public class TelaGols extends Fragment {
    private FirebaseUser user;
    private Partida partida;
    private String campKey;
    private ListView lstGols;
    private List<Time> times;
    private ValueEventListener partidaListener;
    private DatabaseReference partidaReference;
    private DatabaseReference campReference;
    private GolsDAO golDAO;
    private JogadorDAO jogadorDAO;
    private Jogador golContraMandante, golContraVisitante;
    private int golsMandante, golsVisitante;
    private List<Jogador> time1, time2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_gols, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lstGols = view.findViewById(R.id.ftc_lista_gols);
        final ListView lstJogadoresGolsMandante = view.findViewById(R.id.ftc_lista__jogadores_gols_mandante);
        final ListView lstJogadoresGolsVisitante = view.findViewById(R.id.ftc_lista_jogadores_gols_visitante);
        golDAO = new GolsDAO();
        jogadorDAO = new JogadorDAO();
        golsMandante = 0;
        golsVisitante = 0;
        user = FirebaseAuth.getInstance().getCurrentUser();
        campKey = getArguments().getString("campKey");
        partida = getArguments().getParcelable("partida");
        partidaReference = FirebaseDatabase.getInstance().getReference()
                .child("partidas").child(partida.getId());
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        DatabaseReference timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);
        TimeHelper timeHelper = new TimeHelper(timeReference);
        final TextView lblMandante = view.findViewById(R.id.lbl_mandante);
        final TextView lblVisitante = view.findViewById(R.id.lbl_visitante);
        DatabaseReference jogadorReference = FirebaseDatabase.getInstance().getReference()
                .child("time-jogadores").child(partida.getIdMandante());
        JogadorHelper jogadorHelperTime1 = new JogadorHelper(jogadorReference);
        jogadorReference = FirebaseDatabase.getInstance().getReference()
                .child("time-jogadores").child(partida.getIdVisitante());
        JogadorHelper jogadorHelperTime2 = new JogadorHelper(jogadorReference);
        times = timeHelper.retrive();
        time1 = jogadorHelperTime1.retrive();
        time2 = jogadorHelperTime2.retrive();
        golContraMandante = new Jogador();
        golContraMandante.setApelido("Gol contra");
        golContraVisitante = new Jogador();
        golContraVisitante.setApelido("Gol contra");
        golContraMandante.setIdTime(partida.getIdMandante());
        golContraVisitante.setIdTime(partida.getIdVisitante());
        time1.add(golContraMandante);
        time2.add(golContraVisitante);
        final ArrayAdapter<Jogador> jogadoresGeral = new ArrayAdapter<>(getContext(), R.layout.personalizado_list_item, time1);
        final ArrayAdapter<Jogador> jogadorArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.personalizado_list_item, time2);
        final ArrayAdapter<Jogador> gols = new ArrayAdapter<>(getContext(), R.layout.personalizado_list_item);
        ValueEventListener mPartidaListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lstGols.setAdapter(gols);
                lstJogadoresGolsMandante.setAdapter(jogadoresGeral);
                lstJogadoresGolsVisitante.setAdapter(jogadorArrayAdapter);
                lblMandante.setText(partida.getNomeMandante());
                lblVisitante.setText(partida.getNomeVisitante());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        partidaReference.addListenerForSingleValueEvent(mPartidaListener);
        partidaListener = mPartidaListener;
        lstJogadoresGolsMandante.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (partida.getPlacarMandante() > golsMandante) {
                    gols.add(jogadoresGeral.getItem(position));
                    lstGols.setAdapter(gols);
                    golsMandante++;
                } else {
                    Toast.makeText(getContext(), "Limite de gols do " + partida.getNomeMandante() + " atingido...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lstJogadoresGolsVisitante.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (partida.getPlacarVisitante() > golsVisitante) {
                    gols.add(jogadorArrayAdapter.getItem(position));
                    lstGols.setAdapter(gols);
                    golsVisitante++;
                } else {
                    Toast.makeText(getContext(), "Limite de gols do " + partida.getNomeVisitante() + " atingido...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lstGols.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Jogador jogador = gols.getItem(position);
                if (jogador.getIdTime().equals(partida.getIdMandante())) {
                    golsMandante--;
                } else if (jogador.getIdTime().equals(partida.getIdVisitante())) {
                    golsVisitante--;
                }
                gols.remove(gols.getItem(position));
                lstGols.setAdapter(gols);
            }
        });
        Button btnProx = view.findViewById(R.id.proximo_button);
        btnProx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (golsMandante == partida.getPlacarMandante() && golsVisitante == partida.getPlacarVisitante()) {

                    ArrayList<Jogador> gols = new ArrayList<>();
                    for (int i = 0; i < lstGols.getCount(); i++) {
                        Jogador jogador = (Jogador) lstGols.getItemAtPosition(i);
                        jogador = jogadorDAO.configurar(times, jogador);
                        jogador.setGols(jogador.getGols() + 1);
                        gols.add(jogador);
                    }
                    TelaCartoes t = new TelaCartoes();
                    Bundle data = new Bundle();
                    data.putParcelable("partida", partida);
                    data.putString("campKey", campKey);
                    data.putParcelableArrayList("gols",gols);
                    t.setArguments(data);
                    openFragment(t,"amarelos");
                } else {
                    Toast.makeText(getContext(), "faltam gols...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void openFragment(Fragment fragment,String tag) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment,tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (partidaListener != null) {
            partidaReference.removeEventListener(partidaListener);
        }
    }
}
