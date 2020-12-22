package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.ehComplicado.FirebaseHelper.PartidaHelper;
import com.example.ehComplicado.FirebaseHelper.TimeHelper;
import com.example.ehComplicado.FirebaseHelper.UsuarioHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.bean.Campeonato;
import model.bean.Jogador;
import model.bean.Partida;
import model.bean.Time;
import model.bean.Usuario;
import model.dao.CampeonatoDAO;
import model.dao.JogadorDAO;
import model.dao.PartidaDAO;
import model.dao.TimeDAO;

public class TelaJogos extends Fragment {
    private DatabaseReference campReference;
    private FirebaseUser user;
    private List<Partida> partidas1;
    private String campKey;
    private CampeonatoDAO campDAO;
    private TimeDAO timeDAO;
    private List<Usuario> usuarios;
    private Partida partida;
    private boolean todasCadastradas;
    private Campeonato camp;
    private ValueEventListener campListener,campListener2, campListener3, campListener4, campListener5;
    private ArrayAdapter<Partida> adapter;
    Button btnChave;
    private List<Partida> partidasGrupos, oitavas, quartas, semi, Final;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_jogos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ListView lstJogos = view.findViewById(R.id.ftc_lista_jogos);

        timeDAO = new TimeDAO();
        campDAO = new CampeonatoDAO();
        final PartidaDAO partidaDAO = new PartidaDAO();

        user = FirebaseAuth.getInstance().getCurrentUser();

        campKey = getArguments().getString("campKey");

        campReference = FirebaseDatabase.getInstance().getReference().child("campeonatos").child(campKey);
        DatabaseReference timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);
        DatabaseReference partidaReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-partidas").child(campKey);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-seguidores").child(campKey);
        DatabaseReference jogadorRef = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-jogadores").child(campKey);
        JogadorHelper jogadorHelper = new JogadorHelper(jogadorRef);
        oitavas = new ArrayList<>();
        quartas = new ArrayList<>();
        semi = new ArrayList<>();
        Final = new ArrayList<>();
        UsuarioHelper usuarioHelper = new UsuarioHelper(userRef);
        usuarios = usuarioHelper.retrive();
        TimeHelper timeHelper = new TimeHelper(timeReference);
        PartidaHelper partidaHelper = new PartidaHelper(partidaReference);
        final List<Time> times = timeHelper.retrive();
        camp = new Campeonato();
        partidas1 = partidaHelper.retrive();
        final List<Jogador> jogadores = jogadorHelper.retrive();
        final JogadorDAO jogadorDAO = new JogadorDAO();
        final TextView lblAntes = view.findViewById(R.id.lbl_antes);
        final TextView lblDepois = view.findViewById(R.id.lbl_depois);
        final TextView lblAtual = view.findViewById(R.id.lbl_atual);
        btnChave = view.findViewById(R.id.chaveamento_button);
        btnChave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelaOitavas t = new TelaOitavas();
                Bundle data = new Bundle();
                data.putString("campKey",campKey);
                t.setArguments(data);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, t);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Campeonato campeonato = dataSnapshot.getValue(Campeonato.class);
                if(user.getUid().equals(campeonato.getUid())){
                    btnChave.setClickable(false);
                    btnChave.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(listener);
        campListener2 = listener;

        lblAntes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueEventListener mCampListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        camp = dataSnapshot.getValue(Campeonato.class);
                        if (lblAtual.getText().equals(getString(R.string.oitavas))) {
                            if (camp.getNumGrupos() > 0) {
                                lblAtual.setText(R.string.faseDeGrupos);
                                partidasGrupos = partidaDAO.listarFase(partidas1, "grupos");
                                adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, partidasGrupos);
                                lstJogos.setAdapter(adapter);
                            }
                        } else if (lblAtual.getText().equals(getString(R.string.Quartas))) {
                            if ((camp.getNumGrupos() == 4 && camp.getClassificados() == 2) ||
                                    (camp.getNumGrupos() == 2 && (camp.getClassificados() == 3 || camp.getClassificados() == 4))) {
                                lblAtual.setText(getString(R.string.faseDeGrupos));
                                partidasGrupos = partidaDAO.listarFase(partidas1, "grupos");
                                adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, partidasGrupos);
                            } else {
                                if (camp.getNumGrupos() == 0 && camp.getNumTimes() > 8) {
                                    lblAtual.setText(getString(R.string.oitavas));
                                    oitavas = partidaDAO.listarFase(partidas1, "oitavas");
                                    adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, oitavas);
                                    lstJogos.setAdapter(adapter);
                                }
                            }
                        } else if (lblAtual.getText().equals(getString(R.string.Semi))) {
                            if (camp.getNumGrupos() == 2) {
                                if (camp.getClassificados() == 2) {
                                    lblAtual.setText(getString(R.string.faseDeGrupos));
                                    partidasGrupos = partidaDAO.listarFase(partidas1, "grupos");
                                    adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, partidasGrupos);

                                } else {
                                    lblAtual.setText(getString(R.string.Quartas));
                                    quartas = partidaDAO.listarFase(partidas1, "quartas");
                                    adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, quartas);
                                    lstJogos.setAdapter(adapter);
                                }
                            } else if (camp.getNumGrupos() == 4) {
                                if (camp.getClassificados() == 1) {
                                    lblAtual.setText(R.string.faseDeGrupos);
                                    adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, partidasGrupos);
                                } else {
                                    lblAtual.setText(getString(R.string.Quartas));
                                    quartas = partidaDAO.listarFase(partidas1, "quartas");
                                    adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, quartas);
                                    lstJogos.setAdapter(adapter);
                                }
                            } else {
                                lblAtual.setText(getString(R.string.Quartas));
                                quartas = partidaDAO.listarFase(partidas1, "quartas");
                                adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, quartas);
                                lstJogos.setAdapter(adapter);
                            }
                        } else if (lblAtual.getText().equals(getString(R.string.Final))) {
                            if (camp.getNumGrupos() == 2 && camp.getClassificados() == 1) {
                                lblAtual.setText(getString(R.string.faseDeGrupos));
                                partidasGrupos = partidaDAO.listarFase(partidas1, "grupos");
                                adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, partidasGrupos);

                            } else {
                                lblAtual.setText(getString(R.string.Semi));
                                semi = partidaDAO.listarFase(partidas1, "semi");
                                adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, semi);
                                lstJogos.setAdapter(adapter);
                                lblDepois.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                campReference.addListenerForSingleValueEvent(mCampListener);
                campListener5 = mCampListener;
            }
        });
        lblDepois.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueEventListener mCampListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        camp = dataSnapshot.getValue(Campeonato.class);
                        if (lblAtual.getText().equals(getString(R.string.faseDeGrupos))) {
                            if (camp.getNumGrupos() == 4) {
                                if (!camp.isFaseDeGrupos()) {
                                    if (camp.getClassificados() == 3 || camp.getClassificados() == 4) {
                                        lblAtual.setText(getString(R.string.oitavas));
                                        oitavas = partidaDAO.listarFase(partidas1, "oitavas");
                                        adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, oitavas);
                                        lstJogos.setAdapter(adapter);
                                    } else if (camp.getClassificados() == 2) {
                                        lblAtual.setText(getString(R.string.Quartas));
                                        quartas = partidaDAO.listarFase(partidas1, "quartas");
                                        adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, quartas);
                                        lstJogos.setAdapter(adapter);
                                    } else {
                                        lblAtual.setText(R.string.Semi);
                                        semi = partidaDAO.listarFase(partidas1, "semi");
                                        adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, semi);
                                        lstJogos.setAdapter(adapter);
                                    }
                                }
                            } else {

                                if (!camp.isFaseDeGrupos()) {
                                    if (camp.getClassificados() == 3 || camp.getClassificados() == 4) {
                                        lblAtual.setText(R.string.Quartas);
                                        quartas = partidaDAO.listarFase(partidas1, "quartas");
                                        adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, quartas);
                                        lstJogos.setAdapter(adapter);
                                    } else if (camp.getClassificados() == 2) {
                                        lblAtual.setText(getString(R.string.Semi));
                                        semi = partidaDAO.listarFase(partidas1, "semi");
                                        adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, semi);
                                        lstJogos.setAdapter(adapter);
                                    } else {
                                        lblAtual.setText(R.string.ftc_final);
                                        Final = partidaDAO.listarFase(partidas1, "final");
                                        adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, Final);
                                        lstJogos.setAdapter(adapter);
                                    }
                                }

                            }
                        } else if (lblAtual.getText().equals(getString(R.string.oitavas))) {
                            if (!camp.isOitavas()) {
                                lblAtual.setText(getString(R.string.Quartas));
                                quartas = partidaDAO.listarFase(partidas1, "quartas");
                                adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, quartas);
                                lstJogos.setAdapter(adapter);
                            }
                        } else if (lblAtual.getText().equals(getString(R.string.Quartas))) {
                            if (!camp.isQuartas()) {
                                lblAtual.setText(getString(R.string.Semi));
                                semi = partidaDAO.listarFase(partidas1, "semi");
                                adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, semi);
                                lstJogos.setAdapter(adapter);
                            }
                        } else if (lblAtual.getText().equals(getString(R.string.Semi))) {
                            if (!camp.isSemi()) {
                                lblAtual.setText(getString(R.string.Final));
                                Final = partidaDAO.listarFase(partidas1, "final");
                                adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, Final);
                                lstJogos.setAdapter(adapter);
                                lblDepois.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                campReference.addListenerForSingleValueEvent(mCampListener);
                campListener4 = mCampListener;
            }
        });

        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                camp = dataSnapshot.getValue(Campeonato.class);
                partida = new Partida();
                if (camp.isFaseDeGrupos()) {
                    lblAtual.setText(R.string.faseDeGrupos);
                    partidasGrupos = partidaDAO.listarFase(partidas1, "grupos");
                    adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, partidasGrupos);
                    lstJogos.setAdapter(adapter);
                } else if (camp.isOitavas()) {
                    lblAtual.setText(getString(R.string.oitavas));
                    oitavas = partidaDAO.listarFase(partidas1, "oitavas");
                    adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, oitavas);
                    lstJogos.setAdapter(adapter);
                } else if (camp.isQuartas()) {
                    lblAtual.setText(getString(R.string.Quartas));
                    quartas = partidaDAO.listarFase(partidas1, "quartas");
                    adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, quartas);
                    lstJogos.setAdapter(adapter);
                } else if (camp.isSemi()) {
                    lblAtual.setText(getString(R.string.Semi));
                    semi = partidaDAO.listarFase(partidas1, "semi");
                    adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, semi);
                    lstJogos.setAdapter(adapter);
                } else {
                    lblAtual.setText(getString(R.string.Final));
                    Final = partidaDAO.listarFase(partidas1, "final");
                    adapter = new ArrayAdapter<>(getContext(), R.layout.simples_list_item, Final);
                    lstJogos.setAdapter(adapter);
                    lblDepois.setVisibility(View.INVISIBLE);
                }
                todasCadastradas = false;
                for (Partida partida : partidas1) {
                    todasCadastradas = partida.isCadastrada();
                    if (!todasCadastradas) {
                        break;
                    }
                }
                if (todasCadastradas && !camp.isFinalizado()) {
                    if (camp.isFaseDeGrupos()) {
                        if (camp.getNumGrupos() == 4 && camp.getNumTimes() == 16) {
                            List<Time> times1 = timeDAO.listarGrupoComCC(1, times);
                            List<Time> times2 = timeDAO.listarGrupoComCC(2, times);
                            List<Time> times3 = timeDAO.listarGrupoComCC(3, times);
                            List<Time> times4 = timeDAO.listarGrupoComCC(4, times);
                            timeDAO.primeiro(times1.get(0), campKey);
                            timeDAO.primeiro(times2.get(0), campKey);
                            timeDAO.primeiro(times3.get(0), campKey);
                            timeDAO.primeiro(times4.get(0), campKey);
                            times1.get(0).setPrimeiro(true);
                            times2.get(0).setPrimeiro(true);
                            times3.get(0).setPrimeiro(true);
                            times4.get(0).setPrimeiro(true);
                            List<Time> timeList = new ArrayList<>();
                            for (int i = 0; i < camp.getClassificados(); i++) {
                                timeList.add(times1.get(i));
                                timeList.add(times2.get(i));
                                timeList.add(times3.get(i));
                                timeList.add(times4.get(i));
                            }
                            for (Time time : times1) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            for (Time time : times2) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            for (Time time : times3) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            for (Time time : times4) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            Collections.shuffle(timeList);
                            if (timeList.size() == 12) {
                                partida.setFase("oitavas");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(4));
                                partida.setVisitante(timeList.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(6));
                                partida.setVisitante(timeList.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesOitavas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setOitavas(true);
                            } else if (timeList.size() == 16) {
                                partida.setFase("oitavas");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(4));
                                partida.setVisitante(timeList.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(6));
                                partida.setVisitante(timeList.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(8));
                                partida.setVisitante(timeList.get(9));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(10));
                                partida.setVisitante(timeList.get(11));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(12));
                                partida.setVisitante(timeList.get(13));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(14));
                                partida.setVisitante(timeList.get(15));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesOitavas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setOitavas(true);
                            } else if (timeList.size() == 8) {
                                partida.setFase("quartas");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(4));
                                partida.setVisitante(timeList.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(6));
                                partida.setVisitante(timeList.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesQuartas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setQuartas(true);
                            } else if (timeList.size() == 4) {
                                partida.setFase("semi");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesSemi()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setSemi(false);
                            }
                        } else if (camp.getNumGrupos() == 4 && camp.getNumTimes() == 12) {
                            List<Time> times1 = timeDAO.listarGrupoComCC(1, times);
                            List<Time> times2 = timeDAO.listarGrupoComCC(2, times);
                            List<Time> times3 = timeDAO.listarGrupoComCC(3, times);
                            List<Time> times4 = timeDAO.listarGrupoComCC(4, times);
                            timeDAO.primeiro(times1.get(0), campKey);
                            timeDAO.primeiro(times2.get(0), campKey);
                            timeDAO.primeiro(times3.get(0), campKey);
                            timeDAO.primeiro(times4.get(0), campKey);
                            times1.get(0).setPrimeiro(true);
                            times2.get(0).setPrimeiro(true);
                            times3.get(0).setPrimeiro(true);
                            times4.get(0).setPrimeiro(true);
                            List<Time> timeList = new ArrayList<>();
                            for (int i = 0; i < camp.getClassificados(); i++) {
                                timeList.add(times1.get(i));
                                timeList.add(times2.get(i));
                                timeList.add(times3.get(i));
                                timeList.add(times4.get(i));
                            }
                            for (Time time : times1) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            for (Time time : times2) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            for (Time time : times3) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            for (Time time : times4) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            Collections.shuffle(timeList);
                            if (timeList.size() == 8) {
                                partida.setFase("quartas");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(4));
                                partida.setVisitante(timeList.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(6));
                                partida.setVisitante(timeList.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesQuartas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setQuartas(true);
                            } else if (timeList.size() == 12) {
                                partida.setFase("oitavas");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(4));
                                partida.setVisitante(timeList.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(6));
                                partida.setVisitante(timeList.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesOitavas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setOitavas(true);
                            } else if (timeList.size() == 4) {
                                partida.setFase("semi");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesSemi()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setSemi(false);
                            }
                        } else if (camp.getNumGrupos() == 2 && camp.getNumTimes() == 8) {
                            List<Time> times1 = timeDAO.listarGrupoComCC(1, times);
                            List<Time> times2 = timeDAO.listarGrupoComCC(2, times);
                            timeDAO.primeiro(times1.get(0), campKey);
                            timeDAO.primeiro(times2.get(0), campKey);
                            times1.get(0).setPrimeiro(true);
                            times2.get(0).setPrimeiro(true);
                            List<Time> timeList = new ArrayList<>();
                            for (int i = 0; i < camp.getClassificados(); i++) {
                                timeList.add(times1.get(i));
                                timeList.add(times2.get(i));
                            }
                            for (Time time : times1) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            for (Time time : times2) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            Collections.shuffle(timeList);
                            if (camp.getClassificados() == 2) {
                                List<Time> primeiros = new ArrayList<>();
                                primeiros.add(times1.get(0));
                                primeiros.add(times2.get(0));
                                List<Time> segundos = new ArrayList<>();
                                segundos.add(times1.get(1));
                                segundos.add(times2.get(1));
                                Partida partida1 = new Partida();
                                Partida partida2 = new Partida();
                                partida1.setFase("semi");
                                partida2.setFase("semi");
                                if (primeiros.get(0).getGrupo() == 1) {
                                    partida1.setMandante(primeiros.get(0));
                                    partida2.setMandante(primeiros.get(1));
                                } else {
                                    partida1.setMandante(primeiros.get(1));
                                    partida2.setMandante(primeiros.get(0));
                                }
                                if (segundos.get(0).getGrupo() == 1) {
                                    partida2.setVisitante(segundos.get(0));
                                    partida1.setVisitante(segundos.get(1));
                                } else {
                                    partida2.setVisitante(segundos.get(1));
                                    partida1.setVisitante(segundos.get(0));
                                }
                                partidaDAO.cadastrarPrevia(partida1, campKey);
                                partidaDAO.cadastrarPrevia(partida2, campKey);
                                if (camp.isZerarCartoesSemi()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setSemi(true);
                            } else if (timeList.size() == 2) {
                                partida.setFase("final");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                camp.setFaseDeGrupos(false);
                                camp.setFinal(true);
                            } else if (timeList.size() == 6) {
                                for (Time time : timeList) {
                                    if (time.isPrimeiro()) {
                                        timeList.remove(time);
                                    }
                                }
                                partida.setFase("quartas");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesQuartas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setQuartas(true);
                            } else if (timeList.size() == 8) {
                                partida.setFase("quartas");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(4));
                                partida.setVisitante(timeList.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(6));
                                partida.setVisitante(timeList.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesQuartas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setQuartas(true);
                            }
                        } else if (camp.getNumGrupos() == 2 && camp.getNumTimes() == 6) {
                            List<Time> times1 = timeDAO.listarGrupoComCC(1, times);
                            List<Time> times2 = timeDAO.listarGrupoComCC(2, times);
                            timeDAO.primeiro(times1.get(0), campKey);
                            timeDAO.primeiro(times2.get(0), campKey);
                            times1.get(0).setPrimeiro(true);
                            times2.get(0).setPrimeiro(true);
                            List<Time> timeList = new ArrayList<>();
                            for (int i = 0; i < camp.getClassificados(); i++) {
                                timeList.add(times1.get(i));
                                timeList.add(times2.get(i));
                            }
                            for (Time time : times1) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            for (Time time : times2) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            Collections.shuffle(timeList);
                            if (timeList.size() == 4) {
                                List<Time> primeiros = new ArrayList<>();
                                primeiros.add(times1.get(0));
                                primeiros.add(times2.get(0));
                                List<Time> segundos = new ArrayList<>();
                                segundos.add(times1.get(1));
                                segundos.add(times2.get(1));
                                Partida partida1 = new Partida();
                                Partida partida2 = new Partida();
                                partida1.setFase("semi");
                                partida2.setFase("semi");
                                if (primeiros.get(0).getGrupo() == 1) {
                                    partida1.setMandante(primeiros.get(0));
                                    partida2.setMandante(primeiros.get(1));
                                } else {
                                    partida1.setMandante(primeiros.get(1));
                                    partida2.setMandante(primeiros.get(0));
                                }
                                if (segundos.get(0).getGrupo() == 1) {
                                    partida2.setVisitante(segundos.get(0));
                                    partida1.setVisitante(segundos.get(1));
                                } else {
                                    partida2.setVisitante(segundos.get(1));
                                    partida1.setVisitante(segundos.get(0));
                                }
                                partidaDAO.cadastrarPrevia(partida1, campKey);
                                partidaDAO.cadastrarPrevia(partida2, campKey);
                                if (camp.isZerarCartoesSemi()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setSemi(true);
                            } else if (timeList.size() == 2) {
                                partida.setFase("final");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                camp.setFaseDeGrupos(false);
                                camp.setFinal(true);
                            } else if (timeList.size() == 6) {
                                for (Time time : timeList) {
                                    if (time.isPrimeiro()) {
                                        timeList.remove(time);
                                    }
                                }
                                partida.setFase("quartas");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesQuartas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setQuartas(true);
                            }
                        } else if (camp.getNumGrupos() == 2 && camp.getNumTimes() == 10) {
                            List<Time> times1 = timeDAO.listarGrupoComCC(1, times);
                            List<Time> times2 = timeDAO.listarGrupoComCC(2, times);
                            timeDAO.primeiro(times1.get(0), campKey);
                            timeDAO.primeiro(times2.get(0), campKey);
                            times1.get(0).setPrimeiro(true);
                            times2.get(0).setPrimeiro(true);
                            List<Time> timeList = new ArrayList<>();
                            for (int i = 0; i < camp.getClassificados(); i++) {
                                timeList.add(times1.get(i));
                                timeList.add(times2.get(i));
                            }
                            for (Time time : times1) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            for (Time time : times2) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            Collections.shuffle(timeList);
                            if (timeList.size() == 8) {
                                Partida partida1 = new Partida();
                                Partida partida2 = new Partida();
                                Partida partida3 = new Partida();
                                Partida partida4 = new Partida();
                                partida1.setFase("quartas");
                                partida2.setFase("quartas");
                                partida3.setFase("quartas");
                                partida4.setFase("quartas");
                                partida1.setMandante(times1.get(0));
                                partida1.setVisitante(times2.get(3));
                                partida2.setMandante(times2.get(0));
                                partida2.setVisitante(times1.get(3));
                                partida3.setMandante(times1.get(1));
                                partida3.setVisitante(times2.get(2));
                                partida4.setMandante(times2.get(1));
                                partida4.setVisitante(times1.get(2));
                                partidaDAO.cadastrarPrevia(partida1, campKey);
                                partidaDAO.cadastrarPrevia(partida2, campKey);
                                partidaDAO.cadastrarPrevia(partida3, campKey);
                                partidaDAO.cadastrarPrevia(partida4, campKey);
                                if (camp.isZerarCartoesQuartas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setQuartas(true);
                            } else if (timeList.size() == 10) {
                                partida.setFase("oitavas");
                                partida.setMandante(times1.get(3));
                                partida.setVisitante(times2.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(3));
                                partida.setVisitante(times1.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesOitavas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setOitavas(true);
                            } else if (timeList.size() == 2) {
                                partida.setFase("final");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                camp.setFaseDeGrupos(false);
                                camp.setFinal(true);
                            } else if (timeList.size() == 4) {
                                partida.setFase("semi");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesSemi()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setSemi(false);
                            } else if (timeList.size() == 6) {
                                for (Time time : timeList) {
                                    if (time.isPrimeiro()) {
                                        timeList.remove(time);
                                    }
                                }
                                partida.setFase("quartas");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesQuartas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setQuartas(true);
                            }
                        } else if (camp.getNumGrupos() == 2 && camp.getNumTimes() == 12) {
                            List<Time> times1 = timeDAO.listarGrupoComCC(1, times);
                            List<Time> times2 = timeDAO.listarGrupoComCC(2, times);
                            timeDAO.primeiro(times1.get(0), campKey);
                            timeDAO.primeiro(times2.get(0), campKey);
                            times1.get(0).setPrimeiro(true);
                            times2.get(0).setPrimeiro(true);
                            List<Time> timeList = new ArrayList<>();
                            for (int i = 0; i < camp.getClassificados(); i++) {
                                timeList.add(times1.get(i));
                                timeList.add(times2.get(i));
                            }
                            for (Time time : times1) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            for (Time time : times2) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            Collections.shuffle(timeList);
                            if (timeList.size() == 8) {
                                Partida partida1 = new Partida();
                                Partida partida2 = new Partida();
                                Partida partida3 = new Partida();
                                Partida partida4 = new Partida();
                                partida1.setFase("quartas");
                                partida2.setFase("quartas");
                                partida3.setFase("quartas");
                                partida4.setFase("quartas");
                                partida1.setMandante(times1.get(0));
                                partida1.setVisitante(times2.get(3));
                                partida2.setMandante(times2.get(0));
                                partida2.setVisitante(times1.get(3));
                                partida3.setMandante(times1.get(1));
                                partida3.setVisitante(times2.get(2));
                                partida4.setMandante(times2.get(1));
                                partida4.setVisitante(times1.get(2));
                                partidaDAO.cadastrarPrevia(partida1, campKey);
                                partidaDAO.cadastrarPrevia(partida2, campKey);
                                partidaDAO.cadastrarPrevia(partida3, campKey);
                                partidaDAO.cadastrarPrevia(partida4, campKey);
                                if (camp.isZerarCartoesQuartas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setQuartas(true);
                            } else if (timeList.size() == 10) {
                                partida.setFase("oitavas");
                                partida.setMandante(times1.get(3));
                                partida.setVisitante(times2.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(3));
                                partida.setVisitante(times1.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesOitavas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setOitavas(true);
                            } else if (timeList.size() == 12) {
                                partida.setFase("oitavas");
                                for (Time time : timeList) {
                                    if (time.isPrimeiro()) {
                                        timeList.remove(time);
                                    }
                                }
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(4));
                                partida.setVisitante(timeList.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(6));
                                partida.setVisitante(timeList.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesOitavas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setOitavas(true);
                            } else if (timeList.size() == 2) {
                                partida.setFase("final");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                camp.setFaseDeGrupos(false);
                                camp.setFinal(true);
                            } else if (timeList.size() == 4) {
                                partida.setFase("semi");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesSemi()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setSemi(false);
                            } else if (timeList.size() == 6) {
                                for (Time time : timeList) {
                                    if (time.isPrimeiro()) {
                                        timeList.remove(time);
                                    }
                                }
                                partida.setFase("quartas");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesQuartas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setQuartas(true);
                            }
                        } else if (camp.getNumGrupos() == 2 && camp.getNumTimes() == 16) {
                            List<Time> times1 = timeDAO.listarGrupoComCC(1, times);
                            List<Time> times2 = timeDAO.listarGrupoComCC(2, times);
                            timeDAO.primeiro(times1.get(0), campKey);
                            timeDAO.primeiro(times2.get(0), campKey);
                            times1.get(0).setPrimeiro(true);
                            times2.get(0).setPrimeiro(true);
                            List<Time> timeList = new ArrayList<>();
                            for (int i = 0; i < camp.getClassificados(); i++) {
                                timeList.add(times1.get(i));
                                timeList.add(times2.get(i));
                            }
                            for (Time time : times1) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            for (Time time : times2) {
                                if (!timeList.contains(time)) {
                                    timeDAO.eliminar(time, campKey);
                                }
                            }
                            Collections.shuffle(timeList);
                            if (timeList.size() == 8) {
                                List<Time> oitavas = new ArrayList<>();
                                oitavas.add(times1.get(2));
                                oitavas.add(times2.get(2));
                                oitavas.add(times1.get(3));
                                oitavas.add(times2.get(3));
                                oitavas.add(times1.get(4));
                                oitavas.add(times2.get(4));
                                oitavas.add(times1.get(5));
                                oitavas.add(times2.get(5));
                                Collections.shuffle(oitavas);
                                partida.setFase("quartas");
                                partida.setMandante(oitavas.get(0));
                                partida.setVisitante(oitavas.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(oitavas.get(2));
                                partida.setVisitante(oitavas.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(oitavas.get(4));
                                partida.setVisitante(oitavas.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(oitavas.get(6));
                                partida.setVisitante(oitavas.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesOitavas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setOitavas(true);
                            } else if (timeList.size() == 10) {
                                partida.setFase("oitavas");
                                for (Time time : timeList) {
                                    if (time.isPrimeiro()) {
                                        timeList.remove(time);
                                    }
                                }
                                partida.setMandante(times1.get(3));
                                partida.setVisitante(times2.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(3));
                                partida.setVisitante(times1.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesOitavas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setOitavas(true);
                            } else if (timeList.size() == 12) {
                                partida.setFase("oitavas");
                                for (Time time : timeList) {
                                    if (time.isPrimeiro()) {
                                        timeList.remove(time);
                                    }
                                }
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(4));
                                partida.setVisitante(timeList.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(6));
                                partida.setVisitante(timeList.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesOitavas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setOitavas(true);
                            } else if (timeList.size() == 2) {
                                partida.setFase("final");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                camp.setFaseDeGrupos(false);
                                camp.setFinal(true);
                            } else if (timeList.size() == 4) {
                                partida.setFase("semi");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesSemi()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setSemi(false);
                            } else if (timeList.size() == 6) {
                                for (Time time : timeList) {
                                    if (time.isPrimeiro()) {
                                        timeList.remove(time);
                                    }
                                }
                                partida.setFase("quartas");
                                partida.setMandante(timeList.get(0));
                                partida.setVisitante(timeList.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(timeList.get(2));
                                partida.setVisitante(timeList.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                if (camp.isZerarCartoesQuartas()) {
                                    for (Jogador jogador : jogadores) {
                                        jogador.setCa(0);
                                        jogador.setPendurado(false);
                                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                                    }
                                }
                                camp.setFaseDeGrupos(false);
                                camp.setQuartas(true);
                            }
                        }
                    } else if (camp.isOitavas()) {
                        List<Time> timesList1 = timeDAO.listarClassificados(times);
                        Collections.shuffle(timesList1);
                        Partida partida = new Partida();
                        partida.setFase("quartas");
                        partida.setMandante(timesList1.get(0));
                        partida.setVisitante(timesList1.get(1));
                        partidaDAO.cadastrarPrevia(partida, campKey);
                        partida.setMandante(timesList1.get(2));
                        partida.setVisitante(timesList1.get(3));
                        partidaDAO.cadastrarPrevia(partida, campKey);
                        partida.setMandante(timesList1.get(4));
                        partida.setVisitante(timesList1.get(5));
                        partidaDAO.cadastrarPrevia(partida, campKey);
                        partida.setMandante(timesList1.get(6));
                        partida.setVisitante(timesList1.get(7));
                        partidaDAO.cadastrarPrevia(partida, campKey);
                        if (camp.isZerarCartoesQuartas()) {
                            for (Jogador jogador : jogadores) {
                                jogador.setCa(0);
                                jogador.setPendurado(false);
                                jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                            }
                        }
                        camp.setOitavas(false);
                        camp.setQuartas(true);
                    } else if (camp.isQuartas()) {
                        List<Time> timesList1 = timeDAO.listarClassificados(times);
                        Collections.shuffle(timesList1);
                        Partida partida = new Partida();
                        partida.setFase("semi");
                        partida.setMandante(timesList1.get(0));
                        partida.setVisitante(timesList1.get(1));
                        partidaDAO.cadastrarPrevia(partida, campKey);
                        partida.setMandante(timesList1.get(2));
                        partida.setVisitante(timesList1.get(3));
                        partidaDAO.cadastrarPrevia(partida, campKey);
                        if (camp.isZerarCartoesSemi()) {
                            for (Jogador jogador : jogadores) {
                                jogador.setCa(0);
                                jogador.setPendurado(false);
                                jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                            }
                        }
                        camp.setQuartas(false);
                        camp.setSemi(true);
                    } else if (camp.isSemi()) {
                        List<Time> timesList2 = timeDAO.listarClassificados(times);
                        Partida partida = new Partida();
                        partida.setFase("final");
                        partida.setMandante(timesList2.get(0));
                        partida.setVisitante(timesList2.get(1));
                        partidaDAO.cadastrarPrevia(partida, campKey);
                        camp.setSemi(false);
                        camp.setFinal(true);
                    } else if (camp.isFinal()) {
                        camp.setFinal(false);
                        camp.setIniciado(false);
                        camp.setFinalizado(true);
                    }
                    campDAO.passarDeFase(camp, usuarios);
                    Intent it = new Intent(getContext(), Tela.class);
                    it.putExtra("user", user);
                    it.putExtra("camp", camp);
                    it.putExtra("key", "jogos");
                    TelaPrincipalJogos t = (TelaPrincipalJogos)getActivity();
                    t.startActivity(it);
                    t.finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(mCampListener);
        campListener = mCampListener;
        partida = new Partida();
        lstJogos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position,
                                    long id) {
                ValueEventListener mCampListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        camp = dataSnapshot.getValue(Campeonato.class);
                        if (lblAtual.getText().equals(getString(R.string.faseDeGrupos))) {
                            partida = partidasGrupos.get(position);
                        } else if (lblAtual.getText().equals(getString(R.string.oitavas))) {
                            partida = oitavas.get(position);
                        } else if (lblAtual.getText().equals(getString(R.string.Quartas))) {
                            partida = quartas.get(position);
                        } else if (lblAtual.getText().equals(getString(R.string.Semi))) {
                            partida = semi.get(position);
                        } else if (lblAtual.getText().equals(getString(R.string.Final))) {
                            partida = Final.get(position);
                        }
                        if (partida.isCadastrada()) {
                            TelaSumula t = new TelaSumula();
                            Bundle data = new Bundle();
                            data.putString("campKey",campKey);
                            data.putString("partidaKey",partida.getId());
                            data.putParcelable("partida",partida);
                            t.setArguments(data);
                            openFragment(t);
                        } else {
                            if (camp.getUid().equals(user.getUid())) {
                                TelaNovaPartida t = new TelaNovaPartida();
                                Bundle data = new Bundle();
                                data.putString("campKey",campKey);
                                data.putString("partidaKey",partida.getId());
                                data.putParcelable("partida",partida);
                                t.setArguments(data);
                                openFragment(t);
                            } else {
                                Toast.makeText(getContext(), R.string.partidaNaoCadastrada, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                campReference.addListenerForSingleValueEvent(mCampListener);
                campListener3 = mCampListener;
            }
        });

    }

    public void openFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (campListener != null) {
            campReference.removeEventListener(campListener);
        }
        if (campListener2 != null) {
            campReference.removeEventListener(campListener2);
        }
        if (campListener3 != null) {
            campReference.removeEventListener(campListener3);
        }
        if (campListener4 != null) {
            campReference.removeEventListener(campListener4);
        }
        if (campListener5 != null) {
            campReference.removeEventListener(campListener5);
        }
    }

}
