package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Parcelable;
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

import model.bean.CAmarelo;
import model.bean.Campeonato;
import model.bean.Jogador;
import model.bean.Partida;
import model.bean.Time;
import model.dao.CAmareloDAO;
import model.dao.JogadorDAO;
import model.dao.PartidaDAO;

public class TelaCartoes extends Fragment {
    private FirebaseUser user;
    private String campKey;
    private DatabaseReference partidaReference;
    private DatabaseReference campReference;
    private JogadorDAO jogadorDAO;
    private CAmareloDAO amareloDAO;
    private ListView lstCa;
    private List<Time> times;
    private ValueEventListener partidaListener, campListener;
    public Partida partida;
    private List<Jogador> time1, time2;
    private ArrayList<Jogador> gols, amarelos = new ArrayList<>();
    private ArrayList<String> amarelados = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_cartoes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lstCa = view.findViewById(R.id.ftc_lista_ca);
        final ListView lstJogadoresCaMandante = view.findViewById(R.id.ftc_lista__jogadores_ca_mandante);
        final ListView lstJogadoresCaVisitante = view.findViewById(R.id.ftc_lista__jogadores_ca_visitante);
        jogadorDAO = new JogadorDAO();
        amareloDAO = new CAmareloDAO();
        gols = getArguments().getParcelableArrayList("gols");
        final PartidaDAO partidaDAO = new PartidaDAO();
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
        times = timeHelper.retrive();
        final TextView lblMandante = view.findViewById(R.id.lbl_mandante);
        final TextView lblVisitante = view.findViewById(R.id.lbl_visitante);
        DatabaseReference jogadorReference = FirebaseDatabase.getInstance().getReference()
                .child("time-jogadores").child(partida.getIdMandante());
        JogadorHelper jogadorHelperTime1 = new JogadorHelper(jogadorReference);
        jogadorReference = FirebaseDatabase.getInstance().getReference()
                .child("time-jogadores").child(partida.getIdVisitante());
        JogadorHelper jogadorHelperTime2 = new JogadorHelper(jogadorReference);
        final ArrayAdapter<Jogador> ca = new ArrayAdapter<>(getContext(), R.layout.personalizado_list_item);
        lstCa.setAdapter(ca);
        time1 = jogadorHelperTime1.retrive();
        time2 = jogadorHelperTime2.retrive();
        final ArrayAdapter<Jogador> jogadoresGeral = new ArrayAdapter<>(getContext(), R.layout.personalizado_list_item, time1);
        final ArrayAdapter<Jogador> jogadorArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.personalizado_list_item, time2);
        ValueEventListener mPartidaListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lstJogadoresCaMandante.setAdapter(jogadoresGeral);
                lstJogadoresCaVisitante.setAdapter(jogadorArrayAdapter);
                lblMandante.setText(partida.getNomeMandante());
                lblVisitante.setText(partida.getNomeVisitante());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        partidaReference.addListenerForSingleValueEvent(mPartidaListener);
        partidaListener = mPartidaListener;
        lstJogadoresCaMandante.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int i = 0;
                for (String j :
                        amarelados) {
                    if (j.equals(jogadoresGeral.getItem(position).getId())) {
                        i++;
                    }
                }
                if (i < 2) {
                    ca.add(jogadoresGeral.getItem(position));
                    lstCa.setAdapter(ca);
                    amarelados.add(jogadoresGeral.getItem(position).getId());
                } else {
                    Toast.makeText(getContext(), "Não é mais possível atribuir cartões amarelos para esse jogador", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lstJogadoresCaVisitante.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int i = 0;
                for (String j :
                        amarelados) {
                    if (j.equals(jogadorArrayAdapter.getItem(position).getId())) {
                        i++;
                    }
                }
                if (i < 2) {
                    ca.add(jogadorArrayAdapter.getItem(position));
                    lstCa.setAdapter(ca);
                    amarelados.add(jogadorArrayAdapter.getItem(position).getId());
                } else {
                    Toast.makeText(getContext(), "Não é mais possível atribuir cartões amarelos para esse jogador", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lstCa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                amarelados.remove(ca.getItem(position).getId());
                ca.remove(ca.getItem(position));
                lstCa.setAdapter(ca);
            }
        });
        Button btnProx = view.findViewById(R.id.proximo_button);
        btnProx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueEventListener mCampListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                        for (int i = 0; i < lstCa.getCount(); i++) {
                            Jogador jogador = (Jogador) lstCa.getItemAtPosition(i);
                            jogador = jogadorDAO.configurar(times, jogador);
                            if (jogador.isPendurado()) {
                                jogador.setSuspenso(true);
                                jogador.setPendurado(false);
                            } else if ((jogador.getCa() + 1) == camp.getCartoesPendurado()) {
                                jogador.setPendurado(true);
                            } else {
                                jogador.setCa(jogador.getCa() + 1);
                            }
                            amarelos.add(jogador);
                        }
                        TelaVermelhos t = new TelaVermelhos();
                        Bundle data = new Bundle();
                        data.putString("campKey", campKey);
                        data.putParcelable("partida", partida);
                        data.putParcelableArrayList("gols", gols);
                        data.putParcelableArrayList("amarelos", amarelos);
                        t.setArguments(data);
                        openFragment(t);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                campReference.addListenerForSingleValueEvent(mCampListener);
                campListener = mCampListener;
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        if (partidaListener != null) {
            partidaReference.removeEventListener(partidaListener);
        }
        if (campListener != null) {
            campReference.removeEventListener(campListener);
        }
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "vermelhos");
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
