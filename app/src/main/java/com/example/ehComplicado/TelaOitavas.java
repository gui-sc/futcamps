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
import android.widget.Button;
import android.widget.TextView;

import com.example.ehComplicado.FirebaseHelper.PartidaHelper;
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

import model.bean.Campeonato;
import model.bean.Partida;
import model.bean.Time;
import model.dao.PartidaDAO;

public class TelaOitavas extends Fragment {
    private DatabaseReference campReference;
    private ValueEventListener campListener, campListener2, campListener3,campListener4,campListener5;
    private String campKey;
    List<Partida> partidas1 = new ArrayList<>();
    private String fase;
    private List<Partida> partidas = new ArrayList<>();
    private List<Partida> partidasGeral;
    PartidaDAO partidaDAO;
    private List<Time> times;
    TextView lblAnt, lblAtual, lblDepois, lblMandantePartida1, lblMandantePartida2,
            lblMandantePartida3, lblMandantePartida4, lblMandantePlacar1, lblMandantePlacar2, lblMandantePlacar3, lblMandantePlacar4, lblVisitantePlacar1, lblVisitantePlacar2, lblVisitantePlacar3, lblVisitantePlacar4, lblMandantePenaltis1, lblMandantePenaltis2, lblMandantePenaltis3, lblMandantePenaltis4, lblVisitantePartida1, lblVisitantePartida2, lblVisitantePartida3,
            lblVisitantePartida4, lblVisitantePenaltis1, lblVisitantePenaltis2, lblVisitantePenaltis3, lblVisitantePenaltis4, lblX3, lblX4, lblX1, lblX2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_oitavas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lblMandantePartida1 = view.findViewById(R.id.lbl_mandante_partida1);
        lblMandantePartida2 = view.findViewById(R.id.lbl_mandante_partida2);
        lblMandantePartida3 = view.findViewById(R.id.lbl_mandante_partida3);
        lblMandantePartida4 = view.findViewById(R.id.lbl_mandante_partida4);
        lblMandantePlacar1 = view.findViewById(R.id.lbl_placar_mandante_partida1);
        lblMandantePlacar2 = view.findViewById(R.id.lbl_placar_mandante_partida2);
        lblMandantePlacar3 = view.findViewById(R.id.lbl_placar_mandante_partida3);
        lblMandantePlacar4 = view.findViewById(R.id.lbl_placar_mandante_partida4);
        lblVisitantePlacar1 = view.findViewById(R.id.lbl_placar_visitante_partida1);
        lblVisitantePlacar2 = view.findViewById(R.id.lbl_placar_visitante_partida2);
        lblVisitantePlacar3 = view.findViewById(R.id.lbl_placar_visitante_partida3);
        lblVisitantePlacar4 = view.findViewById(R.id.lbl_placar_visitante_partida4);
        lblMandantePenaltis1 = view.findViewById(R.id.lbl_penalti_mandante_partida1);
        lblMandantePenaltis2 = view.findViewById(R.id.lbl_penalti_mandante_partida2);
        lblMandantePenaltis3 = view.findViewById(R.id.lbl_penalti_mandante_partida3);
        lblMandantePenaltis4 = view.findViewById(R.id.lbl_penalti_mandante_partida4);
        lblVisitantePartida1 = view.findViewById(R.id.lbl_visitante_partida1);
        lblVisitantePartida2 = view.findViewById(R.id.lbl_visitante_partida2);
        lblVisitantePartida3 = view.findViewById(R.id.lbl_visitante_partida3);
        lblVisitantePartida4 = view.findViewById(R.id.lbl_visitante_partida4);
        lblVisitantePenaltis1 = view.findViewById(R.id.lbl_penalti_visitante_partida1);
        lblVisitantePenaltis2 = view.findViewById(R.id.lbl_penalti_visitante_partida2);
        lblVisitantePenaltis3 = view.findViewById(R.id.lbl_penalti_visitante_partida3);
        lblVisitantePenaltis4 = view.findViewById(R.id.lbl_penalti_visitante_partida4);
        lblX3 = view.findViewById(R.id.lblX3);
        lblX4 = view.findViewById(R.id.lblX4);
        lblX1 = view.findViewById(R.id.lblX1);
        lblX2 = view.findViewById(R.id.lblX2);
        lblAnt = view.findViewById(R.id.lbl_antes);
        lblAtual = view.findViewById(R.id.lbl_atual);
        lblDepois = view.findViewById(R.id.lbl_depois);

        partidaDAO = new PartidaDAO();


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        campKey = getArguments().getString("campKey");
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        DatabaseReference partidaReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-partidas").child(campKey);
        DatabaseReference timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);
        TimeHelper timeHelper = new TimeHelper(timeReference);
        PartidaHelper partidaHelper = new PartidaHelper(partidaReference);
        partidasGeral = partidaHelper.retrive();
        times = timeHelper.retrive();
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                if (camp.isOitavas()) {
                    trocarDeFase("oitavas1");
                } else if (camp.isQuartas()) {
                    trocarDeFase("quartas");
                } else if (camp.isSemi()) {
                    trocarDeFase("semi");
                } else {
                    trocarDeFase("final");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(mCampListener);
        campListener = mCampListener;
        lblAnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                        if (fase.equals("oitavas1")) {
                            if (camp.getFormato().equals("Fase de Grupos + Mata-Mata")
                                    || camp.getFormato().equals("Group Stage + Knockout Round")) {
                                trocarDeFase("faseDeGrupos");
                            }
                        } else if (fase.equals("oitavas2")) {
                            trocarDeFase("oitavas1");
                        } else if (fase.equals("quartas")) {
                            if ((camp.getNumGrupos() * camp.getClassificados() > 13) ||
                                    ((camp.getFormato().equals("Mata-Mata") || camp.getFormato().equals("Knockout Round")) && camp.getNumTimes() > 13)) {
                                trocarDeFase("oitavas2");
                            } else if ((camp.getNumGrupos() * camp.getClassificados() > 9) ||
                                    ((camp.getFormato().equals("Mata-Mata") || camp.getFormato().equals("Knockout Round")) && camp.getNumTimes() > 9)) {
                                trocarDeFase("oitavas1");
                            } else {
                                if (camp.getFormato().equals("Fase de Grupos + Mata-Mata")
                                        || camp.getFormato().equals("Group Stage + Knockout Round")) {
                                    trocarDeFase("faseDeGrupos");
                                }
                            }
                        } else if (fase.equals("semi")) {
                            if (camp.getNumGrupos() * camp.getClassificados() == 4) {
                                trocarDeFase("faseDeGrupos");
                            } else {
                                trocarDeFase("quartas");
                            }
                        } else if (fase.equals("final")) {
                            if (camp.getNumGrupos() * camp.getClassificados() == 2) {
                                trocarDeFase("faseDeGrupos");
                            } else {
                                trocarDeFase("semi");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                campReference.addListenerForSingleValueEvent(eventListener);
                campListener2 = eventListener;
            }
        });

        final Button btnJogos = view.findViewById(R.id.jogos_button);
        btnJogos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                        Intent it = new Intent(getContext(),TelaPrincipalJogos.class);
                        it.putExtra("camp",camp);
                        startActivity(it);
                        getActivity().finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                campReference.addListenerForSingleValueEvent(eventListener);
                campListener4 = eventListener;

            }
        });
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Campeonato campeonato = dataSnapshot.getValue(Campeonato.class);
                if(user.getUid().equals(campeonato.getUid())){
                    btnJogos.setVisibility(View.INVISIBLE);
                    btnJogos.setClickable(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(eventListener);
        campListener5 = eventListener;
        lblDepois.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                        if (fase.equals("oitavas1")) {
                            if ((camp.getNumGrupos() * camp.getClassificados() > 13) ||
                                    ((camp.getFormato().equals("Mata-Mata") || camp.getFormato().equals("Knockout Round")) && camp.getNumTimes() > 13)) {
                                trocarDeFase("oitavas2");
                            } else if ((camp.getNumGrupos() * camp.getClassificados() > 9) ||
                                    ((camp.getFormato().equals("Mata-Mata") || camp.getFormato().equals("Knockout Round")) && camp.getNumTimes() > 9)) {
                                if (!camp.isOitavas()) {
                                    trocarDeFase("quartas");
                                }
                            }
                        } else if (fase.equals("oitavas2")) {
                            if (!camp.isOitavas()) {
                                trocarDeFase("quartas");
                            }
                        } else if (fase.equals("quartas")) {
                            if (!camp.isQuartas()) {
                                trocarDeFase("semi");
                            }
                        } else if (fase.equals("semi")) {
                            if (!camp.isSemi()) {
                                trocarDeFase("final");
                            }
                        } else if (fase.equals("final")) {
                            if (!camp.isFinal()) {
                                trocarDeFase("premios");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                campReference.addListenerForSingleValueEvent(eventListener);
                campListener3 = eventListener;
            }
        });
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void trocarDeFase(String novaFase) {
        switch (novaFase) {
            case "faseDeGrupos":
                TelaQuatroGrupos t = new TelaQuatroGrupos();
                Bundle data = new Bundle();
                data.putString("campKey", campKey);
                t.setArguments(data);
                openFragment(t);
                break;
            case "oitavas1":
                partidas1 = partidaDAO.listarFase(partidasGeral, "oitavas");
                lblAtual.setText(R.string.oitavas);
                fase = "oitavas1";
                break;
            case "oitavas2":
                partidas1 = partidaDAO.listarFase(partidasGeral, "oitavas");
                lblAtual.setText(R.string.oitavas);
                fase = "oitavas2";
                break;
            case "quartas":
                partidas1 = partidaDAO.listarFase(partidasGeral, "quartas");
                lblAtual.setText(R.string.Quartas);
                fase = "quartas";
                break;
            case "semi":
                partidas1 = partidaDAO.listarFase(partidasGeral, "semi");
                lblAtual.setText(R.string.Semi);
                fase = "semi";
                break;
            case "final":
                partidas1 = partidaDAO.listarFase(partidasGeral, "final");
                lblAtual.setText(R.string.Final);
                fase = "final";
                break;
            case "premios":
                TelaPremios premios = new TelaPremios();
                Bundle dataPremios = new Bundle();
                dataPremios.putString("campKey", campKey);
                premios.setArguments(dataPremios);
                openFragment(premios);
        }
        partidas.clear();
        for (Partida partida : partidas1) {
            partida = partidaDAO.configurar(times, partida);
            partidas.add(partida);
        }
        if (partidas.size() == 4) {
            lblMandantePartida1.setVisibility(View.VISIBLE);
            lblVisitantePartida1.setVisibility(View.VISIBLE);
            lblMandantePlacar1.setVisibility(View.VISIBLE);
            lblVisitantePlacar1.setVisibility(View.VISIBLE);
            lblMandantePenaltis1.setVisibility(View.VISIBLE);
            lblVisitantePenaltis1.setVisibility(View.VISIBLE);
            lblX1.setVisibility(View.VISIBLE);
            lblMandantePartida4.setVisibility(View.VISIBLE);
            lblVisitantePartida4.setVisibility(View.VISIBLE);
            lblMandantePlacar4.setVisibility(View.VISIBLE);
            lblVisitantePlacar4.setVisibility(View.VISIBLE);
            lblMandantePenaltis4.setVisibility(View.VISIBLE);
            lblVisitantePenaltis4.setVisibility(View.VISIBLE);
            lblX4.setVisibility(View.VISIBLE);
            lblMandantePartida2.setVisibility(View.VISIBLE);
            lblVisitantePartida2.setVisibility(View.VISIBLE);
            lblMandantePartida3.setVisibility(View.VISIBLE);
            lblVisitantePartida3.setVisibility(View.VISIBLE);
            lblMandantePlacar2.setVisibility(View.VISIBLE);
            lblVisitantePlacar2.setVisibility(View.VISIBLE);
            lblMandantePlacar3.setVisibility(View.VISIBLE);
            lblVisitantePlacar3.setVisibility(View.VISIBLE);
            lblMandantePenaltis2.setVisibility(View.VISIBLE);
            lblVisitantePenaltis2.setVisibility(View.VISIBLE);
            lblMandantePenaltis3.setVisibility(View.VISIBLE);
            lblVisitantePenaltis3.setVisibility(View.VISIBLE);
            lblX2.setVisibility(View.VISIBLE);
            lblX3.setVisibility(View.VISIBLE);
            if (partidas.get(0).isCadastrada()) {
                lblMandantePartida1.setText(partidas.get(0).getNomeMandante());
                lblVisitantePartida1.setText(partidas.get(0).getNomeVisitante());
                lblMandantePlacar1.setText(String.valueOf(partidas.get(0).getPlacarMandante()));
                lblVisitantePlacar1.setText(String.valueOf(partidas.get(0).getPlacarVisitante()));
                if (partidas.get(0).getPenaltisMandante() != 0 && partidas.get(0).getPenaltisVisitante() != 0) {
                    lblMandantePenaltis1.setText(String.valueOf(partidas.get(0).getPenaltisMandante()));
                    lblVisitantePenaltis1.setText(String.valueOf(partidas.get(0).getPenaltisVisitante()));
                } else {
                    lblMandantePenaltis1.setText("");
                    lblVisitantePenaltis1.setText("");
                }
            } else {
                lblMandantePartida1.setText(partidas.get(0).getNomeMandante());
                lblVisitantePartida1.setText(partidas.get(0).getNomeVisitante());
                lblMandantePlacar1.setText("");
                lblVisitantePlacar1.setText("");
                lblMandantePenaltis1.setText("");
                lblVisitantePenaltis1.setText("");
            }
            if (partidas.get(1).isCadastrada()) {
                lblMandantePartida2.setText(partidas.get(1).getNomeMandante());
                lblVisitantePartida2.setText(partidas.get(1).getNomeVisitante());
                lblMandantePlacar2.setText(String.valueOf(partidas.get(1).getPlacarMandante()));
                lblVisitantePlacar2.setText(String.valueOf(partidas.get(1).getPlacarVisitante()));
                if (partidas.get(1).getPenaltisMandante() != 0 && partidas.get(1).getPenaltisVisitante() != 0) {
                    lblMandantePenaltis2.setText(String.valueOf(partidas.get(1).getPenaltisMandante()));
                    lblVisitantePenaltis2.setText(String.valueOf(partidas.get(1).getPenaltisVisitante()));
                } else {
                    lblMandantePenaltis2.setText("");
                    lblVisitantePenaltis2.setText("");
                }
            } else {
                lblMandantePartida2.setText(partidas.get(1).getNomeMandante());
                lblVisitantePartida2.setText(partidas.get(1).getNomeVisitante());
                lblMandantePlacar2.setText("");
                lblVisitantePlacar2.setText("");
                lblMandantePenaltis2.setText("");
                lblVisitantePenaltis2.setText("");
            }
            if (partidas.get(2).isCadastrada()) {
                lblMandantePartida3.setText(partidas.get(2).getNomeMandante());
                lblVisitantePartida3.setText(partidas.get(2).getNomeVisitante());
                lblMandantePlacar3.setText(String.valueOf(partidas.get(2).getPlacarMandante()));
                lblVisitantePlacar3.setText(String.valueOf(partidas.get(2).getPlacarVisitante()));
                if (partidas.get(2).getPenaltisMandante() != 0 && partidas.get(2).getPenaltisVisitante() != 0) {
                    lblMandantePenaltis3.setText(String.valueOf(partidas.get(2).getPenaltisMandante()));
                    lblVisitantePenaltis3.setText(String.valueOf(partidas.get(2).getPenaltisVisitante()));
                } else {
                    lblMandantePenaltis3.setText("");
                    lblVisitantePenaltis3.setText("");
                }
            } else {
                lblMandantePartida3.setText(partidas.get(2).getNomeMandante());
                lblVisitantePartida3.setText(partidas.get(2).getNomeVisitante());
                lblMandantePlacar3.setText("");
                lblVisitantePlacar3.setText("");
                lblMandantePenaltis3.setText("");
                lblVisitantePenaltis3.setText("");
            }
            if (partidas.get(3).isCadastrada()) {
                lblMandantePartida4.setText(partidas.get(3).getNomeMandante());
                lblVisitantePartida4.setText(partidas.get(3).getNomeVisitante());
                lblMandantePlacar4.setText(String.valueOf(partidas.get(3).getPlacarMandante()));
                lblVisitantePlacar4.setText(String.valueOf(partidas.get(3).getPlacarVisitante()));
                if (partidas.get(3).getPenaltisMandante() != 0 && partidas.get(3).getPenaltisVisitante() != 0) {
                    lblMandantePenaltis4.setText(String.valueOf(partidas.get(3).getPenaltisMandante()));
                    lblVisitantePenaltis4.setText(String.valueOf(partidas.get(3).getPenaltisVisitante()));
                } else {
                    lblMandantePenaltis4.setText("");
                    lblVisitantePenaltis4.setText("");
                }
            } else {
                lblMandantePartida4.setText(partidas.get(3).getNomeMandante());
                lblVisitantePartida4.setText(partidas.get(3).getNomeVisitante());
                lblMandantePlacar4.setText("");
                lblVisitantePlacar4.setText("");
                lblMandantePenaltis4.setText("");
                lblVisitantePenaltis4.setText("");
            }
        } else if (partidas.size() == 2) {
            if (partidas.get(0).isCadastrada()) {
                lblMandantePartida2.setText(partidas.get(0).getNomeMandante());
                lblVisitantePartida2.setText(partidas.get(0).getNomeVisitante());
                lblMandantePlacar2.setText(String.valueOf(partidas.get(0).getPlacarMandante()));
                lblVisitantePlacar2.setText(String.valueOf(partidas.get(0).getPlacarVisitante()));
                if (partidas.get(0).getPenaltisMandante() != 0 && partidas.get(0).getPenaltisVisitante() != 0) {
                    lblMandantePenaltis2.setText(String.valueOf(partidas.get(0).getPenaltisMandante()));
                    lblVisitantePenaltis2.setText(String.valueOf(partidas.get(0).getPenaltisVisitante()));
                } else {
                    lblMandantePenaltis2.setText("");
                    lblVisitantePenaltis2.setText("");
                }
            } else {
                lblMandantePartida2.setText(partidas.get(0).getNomeMandante());
                lblVisitantePartida2.setText(partidas.get(0).getNomeVisitante());
                lblMandantePlacar2.setText("");
                lblVisitantePlacar2.setText("");
                lblMandantePenaltis2.setText("");
                lblVisitantePenaltis2.setText("");
            }
            if (partidas.get(1).isCadastrada()) {
                lblMandantePartida3.setText(partidas.get(1).getNomeMandante());
                lblVisitantePartida3.setText(partidas.get(1).getNomeVisitante());
                lblMandantePlacar3.setText(String.valueOf(partidas.get(1).getPlacarMandante()));
                lblVisitantePlacar3.setText(String.valueOf(partidas.get(1).getPlacarVisitante()));
                if (partidas.get(1).getPenaltisMandante() != 0 && partidas.get(1).getPenaltisVisitante() != 0) {
                    lblMandantePenaltis3.setText(String.valueOf(partidas.get(1).getPenaltisMandante()));
                    lblVisitantePenaltis3.setText(String.valueOf(partidas.get(1).getPenaltisVisitante()));
                } else {
                    lblMandantePenaltis3.setText("");
                    lblVisitantePenaltis3.setText("");
                }
            } else {
                lblMandantePartida3.setText(partidas.get(1).getNomeMandante());
                lblVisitantePartida3.setText(partidas.get(1).getNomeVisitante());
                lblMandantePlacar3.setText("");
                lblVisitantePlacar3.setText("");
                lblMandantePenaltis3.setText("");
                lblVisitantePenaltis3.setText("");
            }
            lblMandantePartida1.setVisibility(View.INVISIBLE);
            lblVisitantePartida1.setVisibility(View.INVISIBLE);
            lblMandantePartida4.setVisibility(View.INVISIBLE);
            lblVisitantePartida4.setVisibility(View.INVISIBLE);
            lblMandantePlacar1.setVisibility(View.INVISIBLE);
            lblVisitantePlacar1.setVisibility(View.INVISIBLE);
            lblMandantePlacar4.setVisibility(View.INVISIBLE);
            lblVisitantePlacar4.setVisibility(View.INVISIBLE);
            lblMandantePenaltis1.setVisibility(View.INVISIBLE);
            lblVisitantePenaltis1.setVisibility(View.INVISIBLE);
            lblMandantePenaltis4.setVisibility(View.INVISIBLE);
            lblVisitantePenaltis4.setVisibility(View.INVISIBLE);
            lblX1.setVisibility(View.INVISIBLE);
            lblX4.setVisibility(View.INVISIBLE);

            lblMandantePartida3.setVisibility(View.VISIBLE);
            lblVisitantePartida3.setVisibility(View.VISIBLE);
            lblMandantePlacar3.setVisibility(View.VISIBLE);
            lblVisitantePlacar3.setVisibility(View.VISIBLE);
            lblMandantePenaltis3.setVisibility(View.VISIBLE);
            lblVisitantePenaltis3.setVisibility(View.VISIBLE);
            lblX3.setVisibility(View.VISIBLE);
            lblMandantePartida2.setVisibility(View.VISIBLE);
            lblVisitantePartida2.setVisibility(View.VISIBLE);
            lblMandantePlacar2.setVisibility(View.VISIBLE);
            lblVisitantePlacar2.setVisibility(View.VISIBLE);
            lblMandantePenaltis2.setVisibility(View.VISIBLE);
            lblVisitantePenaltis2.setVisibility(View.VISIBLE);
            lblX2.setVisibility(View.VISIBLE);
        } else if (partidas.size() == 6) {
            if (fase.equals("oitavas1")) {
                if (partidas.get(0).isCadastrada()) {
                    lblMandantePartida1.setText(partidas.get(0).getNomeMandante());
                    lblVisitantePartida1.setText(partidas.get(0).getNomeVisitante());
                    lblMandantePlacar1.setText(String.valueOf(partidas.get(0).getPlacarMandante()));
                    lblVisitantePlacar1.setText(String.valueOf(partidas.get(0).getPlacarVisitante()));
                    if (partidas.get(0).getPenaltisMandante() != 0 && partidas.get(0).getPenaltisVisitante() != 0) {
                        lblMandantePenaltis1.setText(String.valueOf(partidas.get(0).getPenaltisMandante()));
                        lblVisitantePenaltis1.setText(String.valueOf(partidas.get(0).getPenaltisVisitante()));
                    } else {
                        lblMandantePenaltis1.setText("");
                        lblVisitantePenaltis1.setText("");
                    }
                } else {
                    lblMandantePartida1.setText(partidas.get(0).getNomeMandante());
                    lblVisitantePartida1.setText(partidas.get(0).getNomeVisitante());
                    lblMandantePlacar1.setText("");
                    lblVisitantePlacar1.setText("");
                    lblMandantePenaltis1.setText("");
                    lblVisitantePenaltis1.setText("");
                }
                if (partidas.get(1).isCadastrada()) {
                    lblMandantePartida2.setText(partidas.get(1).getNomeMandante());
                    lblVisitantePartida2.setText(partidas.get(1).getNomeVisitante());
                    lblMandantePlacar2.setText(String.valueOf(partidas.get(1).getPlacarMandante()));
                    lblVisitantePlacar2.setText(String.valueOf(partidas.get(1).getPlacarVisitante()));
                    if (partidas.get(1).getPenaltisMandante() != 0 && partidas.get(1).getPenaltisVisitante() != 0) {
                        lblMandantePenaltis2.setText(String.valueOf(partidas.get(1).getPenaltisMandante()));
                        lblVisitantePenaltis2.setText(String.valueOf(partidas.get(1).getPenaltisVisitante()));
                    } else {
                        lblMandantePenaltis2.setText("");
                        lblVisitantePenaltis2.setText("");
                    }
                } else {
                    lblMandantePartida2.setText(partidas.get(1).getNomeMandante());
                    lblVisitantePartida2.setText(partidas.get(1).getNomeVisitante());
                    lblMandantePlacar2.setText("");
                    lblVisitantePlacar2.setText("");
                    lblMandantePenaltis2.setText("");
                    lblVisitantePenaltis2.setText("");
                }
                if (partidas.get(2).isCadastrada()) {
                    lblMandantePartida3.setText(partidas.get(2).getNomeMandante());
                    lblVisitantePartida3.setText(partidas.get(2).getNomeVisitante());
                    lblMandantePlacar3.setText(String.valueOf(partidas.get(2).getPlacarMandante()));
                    lblVisitantePlacar3.setText(String.valueOf(partidas.get(2).getPlacarVisitante()));
                    if (partidas.get(2).getPenaltisMandante() != 0 && partidas.get(2).getPenaltisVisitante() != 0) {
                        lblMandantePenaltis3.setText(String.valueOf(partidas.get(2).getPenaltisMandante()));
                        lblVisitantePenaltis3.setText(String.valueOf(partidas.get(2).getPenaltisVisitante()));
                    } else {
                        lblMandantePenaltis3.setText("");
                        lblVisitantePenaltis3.setText("");
                    }
                } else {
                    lblMandantePartida3.setText(partidas.get(2).getNomeMandante());
                    lblVisitantePartida3.setText(partidas.get(2).getNomeVisitante());
                    lblMandantePlacar3.setText("");
                    lblVisitantePlacar3.setText("");
                    lblMandantePenaltis3.setText("");
                    lblVisitantePenaltis3.setText("");
                }
            } else if (fase.equals("oitavas2")) {
                if (partidas.get(3).isCadastrada()) {
                    lblMandantePartida1.setText(partidas.get(3).getNomeMandante());
                    lblVisitantePartida1.setText(partidas.get(3).getNomeVisitante());
                    lblMandantePlacar1.setText(String.valueOf(partidas.get(3).getPlacarMandante()));
                    lblVisitantePlacar1.setText(String.valueOf(partidas.get(3).getPlacarVisitante()));
                    if (partidas.get(3).getPenaltisMandante() != 0 && partidas.get(3).getPenaltisVisitante() != 0) {
                        lblMandantePenaltis1.setText(String.valueOf(partidas.get(3).getPenaltisMandante()));
                        lblVisitantePenaltis1.setText(String.valueOf(partidas.get(3).getPenaltisVisitante()));
                    } else {
                        lblMandantePenaltis1.setText("");
                        lblVisitantePenaltis1.setText("");
                    }
                } else {
                    lblMandantePartida1.setText(partidas.get(3).getNomeMandante());
                    lblVisitantePartida1.setText(partidas.get(3).getNomeVisitante());
                    lblMandantePlacar1.setText("");
                    lblVisitantePlacar1.setText("");
                    lblMandantePenaltis1.setText("");
                    lblVisitantePenaltis1.setText("");
                }
                if (partidas.get(4).isCadastrada()) {
                    lblMandantePartida2.setText(partidas.get(4).getNomeMandante());
                    lblVisitantePartida2.setText(partidas.get(4).getNomeVisitante());
                    lblMandantePlacar2.setText(String.valueOf(partidas.get(4).getPlacarMandante()));
                    lblVisitantePlacar2.setText(String.valueOf(partidas.get(4).getPlacarVisitante()));
                    if (partidas.get(4).getPenaltisMandante() != 0 && partidas.get(4).getPenaltisVisitante() != 0) {
                        lblMandantePenaltis2.setText(String.valueOf(partidas.get(4).getPenaltisMandante()));
                        lblVisitantePenaltis2.setText(String.valueOf(partidas.get(4).getPenaltisVisitante()));
                    } else {
                        lblMandantePenaltis2.setText("");
                        lblVisitantePenaltis2.setText("");
                    }
                } else {
                    lblMandantePartida2.setText(partidas.get(4).getNomeMandante());
                    lblVisitantePartida2.setText(partidas.get(4).getNomeVisitante());
                    lblMandantePlacar2.setText("");
                    lblVisitantePlacar2.setText("");
                    lblMandantePenaltis2.setText("");
                    lblVisitantePenaltis2.setText("");
                }
                if (partidas.get(5).isCadastrada()) {
                    lblMandantePartida3.setText(partidas.get(5).getNomeMandante());
                    lblVisitantePartida3.setText(partidas.get(5).getNomeVisitante());
                    lblMandantePlacar3.setText(String.valueOf(partidas.get(5).getPlacarMandante()));
                    lblVisitantePlacar3.setText(String.valueOf(partidas.get(5).getPlacarVisitante()));
                    if (partidas.get(5).getPenaltisMandante() != 0 && partidas.get(5).getPenaltisVisitante() != 0) {
                        lblMandantePenaltis3.setText(String.valueOf(partidas.get(5).getPenaltisMandante()));
                        lblVisitantePenaltis3.setText(String.valueOf(partidas.get(5).getPenaltisVisitante()));
                    } else {
                        lblMandantePenaltis3.setText("");
                        lblVisitantePenaltis3.setText("");
                    }
                } else {
                    lblMandantePartida3.setText(partidas.get(5).getNomeMandante());
                    lblVisitantePartida3.setText(partidas.get(5).getNomeVisitante());
                    lblMandantePlacar3.setText("");
                    lblVisitantePlacar3.setText("");
                    lblMandantePenaltis3.setText("");
                    lblVisitantePenaltis3.setText("");
                }
            }
            lblMandantePartida4.setVisibility(View.INVISIBLE);
            lblVisitantePartida4.setVisibility(View.INVISIBLE);
            lblMandantePlacar4.setVisibility(View.INVISIBLE);
            lblVisitantePlacar4.setVisibility(View.INVISIBLE);
            lblMandantePenaltis4.setVisibility(View.INVISIBLE);
            lblVisitantePenaltis4.setVisibility(View.INVISIBLE);
            lblX4.setVisibility(View.INVISIBLE);
            lblMandantePartida1.setVisibility(View.VISIBLE);
            lblVisitantePartida1.setVisibility(View.VISIBLE);
            lblMandantePlacar1.setVisibility(View.VISIBLE);
            lblVisitantePlacar1.setVisibility(View.VISIBLE);
            lblMandantePenaltis1.setVisibility(View.VISIBLE);
            lblVisitantePenaltis1.setVisibility(View.VISIBLE);
            lblX1.setVisibility(View.VISIBLE);
            lblMandantePartida2.setVisibility(View.VISIBLE);
            lblVisitantePartida2.setVisibility(View.VISIBLE);
            lblMandantePlacar2.setVisibility(View.VISIBLE);
            lblVisitantePlacar2.setVisibility(View.VISIBLE);
            lblMandantePenaltis2.setVisibility(View.VISIBLE);
            lblVisitantePenaltis2.setVisibility(View.VISIBLE);
            lblX2.setVisibility(View.VISIBLE);
            lblMandantePartida3.setVisibility(View.VISIBLE);
            lblVisitantePartida3.setVisibility(View.VISIBLE);
            lblMandantePlacar3.setVisibility(View.VISIBLE);
            lblVisitantePlacar3.setVisibility(View.VISIBLE);
            lblMandantePenaltis3.setVisibility(View.VISIBLE);
            lblVisitantePenaltis3.setVisibility(View.VISIBLE);
            lblX3.setVisibility(View.VISIBLE);
        } else if (partidas.size() == 8) {
            lblMandantePartida1.setVisibility(View.VISIBLE);
            lblVisitantePartida1.setVisibility(View.VISIBLE);
            lblMandantePlacar1.setVisibility(View.VISIBLE);
            lblVisitantePlacar1.setVisibility(View.VISIBLE);
            lblMandantePenaltis1.setVisibility(View.VISIBLE);
            lblVisitantePenaltis1.setVisibility(View.VISIBLE);
            lblX1.setVisibility(View.VISIBLE);
            lblMandantePartida2.setVisibility(View.VISIBLE);
            lblVisitantePartida2.setVisibility(View.VISIBLE);
            lblMandantePlacar2.setVisibility(View.VISIBLE);
            lblVisitantePlacar2.setVisibility(View.VISIBLE);
            lblMandantePenaltis2.setVisibility(View.VISIBLE);
            lblVisitantePenaltis2.setVisibility(View.VISIBLE);
            lblX2.setVisibility(View.VISIBLE);
            lblMandantePartida3.setVisibility(View.VISIBLE);
            lblVisitantePartida3.setVisibility(View.VISIBLE);
            lblMandantePlacar3.setVisibility(View.VISIBLE);
            lblVisitantePlacar3.setVisibility(View.VISIBLE);
            lblMandantePenaltis3.setVisibility(View.VISIBLE);
            lblVisitantePenaltis3.setVisibility(View.VISIBLE);
            lblX3.setVisibility(View.VISIBLE);
            lblMandantePartida4.setVisibility(View.VISIBLE);
            lblVisitantePartida4.setVisibility(View.VISIBLE);
            lblMandantePlacar4.setVisibility(View.VISIBLE);
            lblVisitantePlacar4.setVisibility(View.VISIBLE);
            lblMandantePenaltis4.setVisibility(View.VISIBLE);
            lblVisitantePenaltis4.setVisibility(View.VISIBLE);
            lblX4.setVisibility(View.VISIBLE);
            if (novaFase.equals("oitavas1")) {
                if (partidas.get(0).isCadastrada()) {
                    lblMandantePartida1.setText(partidas.get(0).getNomeMandante());
                    lblVisitantePartida1.setText(partidas.get(0).getNomeVisitante());
                    lblMandantePlacar1.setText(String.valueOf(partidas.get(0).getPlacarMandante()));
                    lblVisitantePlacar1.setText(String.valueOf(partidas.get(0).getPlacarVisitante()));
                    if (partidas.get(0).getPenaltisMandante() != 0 && partidas.get(0).getPenaltisVisitante() != 0) {
                        lblMandantePenaltis1.setText(String.valueOf(partidas.get(0).getPenaltisMandante()));
                        lblVisitantePenaltis1.setText(String.valueOf(partidas.get(0).getPenaltisVisitante()));
                    } else {
                        lblMandantePenaltis1.setText("");
                        lblVisitantePenaltis1.setText("");
                    }
                } else {
                    lblMandantePartida1.setText(partidas.get(0).getNomeMandante());
                    lblVisitantePartida1.setText(partidas.get(0).getNomeVisitante());
                    lblMandantePlacar1.setText("");
                    lblVisitantePlacar1.setText("");
                    lblMandantePenaltis1.setText("");
                    lblVisitantePenaltis1.setText("");
                }
                if (partidas.get(1).isCadastrada()) {
                    lblMandantePartida2.setText(partidas.get(1).getNomeMandante());
                    lblVisitantePartida2.setText(partidas.get(1).getNomeVisitante());
                    lblMandantePlacar2.setText(String.valueOf(partidas.get(1).getPlacarMandante()));
                    lblVisitantePlacar2.setText(String.valueOf(partidas.get(1).getPlacarVisitante()));
                    if (partidas.get(1).getPenaltisMandante() != 0 && partidas.get(1).getPenaltisVisitante() != 0) {
                        lblMandantePenaltis2.setText(String.valueOf(partidas.get(1).getPenaltisMandante()));
                        lblVisitantePenaltis2.setText(String.valueOf(partidas.get(1).getPenaltisVisitante()));
                    } else {
                        lblMandantePenaltis2.setText("");
                        lblVisitantePenaltis2.setText("");
                    }
                } else {
                    lblMandantePartida2.setText(partidas.get(1).getNomeMandante());
                    lblVisitantePartida2.setText(partidas.get(1).getNomeVisitante());
                    lblMandantePlacar2.setText("");
                    lblVisitantePlacar2.setText("");
                    lblMandantePenaltis2.setText("");
                    lblVisitantePenaltis2.setText("");
                }
                if (partidas.get(2).isCadastrada()) {
                    lblMandantePartida3.setText(partidas.get(2).getNomeMandante());
                    lblVisitantePartida3.setText(partidas.get(2).getNomeVisitante());
                    lblMandantePlacar3.setText(String.valueOf(partidas.get(2).getPlacarMandante()));
                    lblVisitantePlacar3.setText(String.valueOf(partidas.get(2).getPlacarVisitante()));
                    if (partidas.get(2).getPenaltisMandante() != 0 && partidas.get(2).getPenaltisVisitante() != 0) {
                        lblMandantePenaltis3.setText(String.valueOf(partidas.get(2).getPenaltisMandante()));
                        lblVisitantePenaltis3.setText(String.valueOf(partidas.get(2).getPenaltisVisitante()));
                    } else {
                        lblMandantePenaltis3.setText("");
                        lblVisitantePenaltis3.setText("");
                    }
                } else {
                    lblMandantePartida3.setText(partidas.get(2).getNomeMandante());
                    lblVisitantePartida3.setText(partidas.get(2).getNomeVisitante());
                    lblMandantePlacar3.setText("");
                    lblVisitantePlacar3.setText("");
                    lblMandantePenaltis3.setText("");
                    lblVisitantePenaltis3.setText("");
                }
                if (partidas.get(3).isCadastrada()) {
                    lblMandantePartida4.setText(partidas.get(3).getNomeMandante());
                    lblVisitantePartida4.setText(partidas.get(3).getNomeVisitante());
                    lblMandantePlacar4.setText(String.valueOf(partidas.get(3).getPlacarMandante()));
                    lblVisitantePlacar4.setText(String.valueOf(partidas.get(3).getPlacarVisitante()));
                    if (partidas.get(3).getPenaltisMandante() != 0 && partidas.get(3).getPenaltisVisitante() != 0) {
                        lblMandantePenaltis4.setText(String.valueOf(partidas.get(3).getPenaltisMandante()));
                        lblVisitantePenaltis4.setText(String.valueOf(partidas.get(3).getPenaltisVisitante()));
                    } else {
                        lblMandantePenaltis4.setText("");
                        lblVisitantePenaltis4.setText("");
                    }
                } else {
                    lblMandantePartida4.setText(partidas.get(3).getNomeMandante());
                    lblVisitantePartida4.setText(partidas.get(3).getNomeVisitante());
                    lblMandantePlacar4.setText("");
                    lblVisitantePlacar4.setText("");
                    lblMandantePenaltis4.setText("");
                    lblVisitantePenaltis4.setText("");
                }
            } else if (novaFase.equals("oitavas2")) {

                if (partidas.get(4).isCadastrada()) {
                    lblMandantePartida1.setText(partidas.get(4).getNomeMandante());
                    lblVisitantePartida1.setText(partidas.get(4).getNomeVisitante());
                    lblMandantePlacar1.setText(String.valueOf(partidas.get(4).getPlacarMandante()));
                    lblVisitantePlacar1.setText(String.valueOf(partidas.get(4).getPlacarVisitante()));
                    if (partidas.get(4).getPenaltisMandante() != 0 && partidas.get(4).getPenaltisVisitante() != 0) {
                        lblMandantePenaltis1.setText(String.valueOf(partidas.get(4).getPenaltisMandante()));
                        lblVisitantePenaltis1.setText(String.valueOf(partidas.get(4).getPenaltisVisitante()));
                    } else {
                        lblMandantePenaltis1.setText("");
                        lblVisitantePenaltis1.setText("");
                    }
                } else {
                    lblMandantePartida1.setText(partidas.get(4).getNomeMandante());
                    lblVisitantePartida1.setText(partidas.get(4).getNomeVisitante());
                    lblMandantePlacar1.setText("");
                    lblVisitantePlacar1.setText("");
                    lblMandantePenaltis1.setText("");
                    lblVisitantePenaltis1.setText("");
                }
                if (partidas.get(5).isCadastrada()) {
                    lblMandantePartida2.setText(partidas.get(5).getNomeMandante());
                    lblVisitantePartida2.setText(partidas.get(5).getNomeVisitante());
                    lblMandantePlacar2.setText(String.valueOf(partidas.get(5).getPlacarMandante()));
                    lblVisitantePlacar2.setText(String.valueOf(partidas.get(5).getPlacarVisitante()));
                    if (partidas.get(5).getPenaltisMandante() != 0 && partidas.get(5).getPenaltisVisitante() != 0) {
                        lblMandantePenaltis2.setText(String.valueOf(partidas.get(5).getPenaltisMandante()));
                        lblVisitantePenaltis2.setText(String.valueOf(partidas.get(5).getPenaltisVisitante()));
                    } else {
                        lblMandantePenaltis2.setText("");
                        lblVisitantePenaltis2.setText("");
                    }
                } else {
                    lblMandantePartida2.setText(partidas.get(5).getNomeMandante());
                    lblVisitantePartida2.setText(partidas.get(5).getNomeVisitante());
                    lblMandantePlacar2.setText("");
                    lblVisitantePlacar2.setText("");
                    lblMandantePenaltis2.setText("");
                    lblVisitantePenaltis2.setText("");
                }
                if (partidas.get(6).isCadastrada()) {
                    lblMandantePartida3.setText(partidas.get(6).getNomeMandante());
                    lblVisitantePartida3.setText(partidas.get(6).getNomeVisitante());
                    lblMandantePlacar3.setText(String.valueOf(partidas.get(6).getPlacarMandante()));
                    lblVisitantePlacar3.setText(String.valueOf(partidas.get(6).getPlacarVisitante()));
                    if (partidas.get(6).getPenaltisMandante() != 0 && partidas.get(6).getPenaltisVisitante() != 0) {
                        lblMandantePenaltis3.setText(String.valueOf(partidas.get(6).getPenaltisMandante()));
                        lblVisitantePenaltis3.setText(String.valueOf(partidas.get(6).getPenaltisVisitante()));
                    } else {
                        lblMandantePenaltis3.setText("");
                        lblVisitantePenaltis3.setText("");
                    }
                } else {
                    lblMandantePartida3.setText(partidas.get(6).getNomeMandante());
                    lblVisitantePartida3.setText(partidas.get(6).getNomeVisitante());
                    lblMandantePlacar3.setText("");
                    lblVisitantePlacar3.setText("");
                    lblMandantePenaltis3.setText("");
                    lblVisitantePenaltis3.setText("");
                }
                if (partidas.get(7).isCadastrada()) {
                    lblMandantePartida4.setText(partidas.get(7).getNomeMandante());
                    lblVisitantePartida4.setText(partidas.get(7).getNomeVisitante());
                    lblMandantePlacar4.setText(String.valueOf(partidas.get(7).getPlacarMandante()));
                    lblVisitantePlacar4.setText(String.valueOf(partidas.get(7).getPlacarVisitante()));
                    if (partidas.get(7).getPenaltisMandante() != 0 && partidas.get(7).getPenaltisVisitante() != 0) {
                        lblMandantePenaltis4.setText(String.valueOf(partidas.get(7).getPenaltisMandante()));
                        lblVisitantePenaltis4.setText(String.valueOf(partidas.get(7).getPenaltisVisitante()));
                    } else {
                        lblMandantePenaltis4.setText("");
                        lblVisitantePenaltis4.setText("");
                    }
                } else {
                    lblMandantePartida4.setText(partidas.get(7).getNomeMandante());
                    lblVisitantePartida4.setText(partidas.get(7).getNomeVisitante());
                    lblMandantePlacar4.setText("");
                    lblVisitantePlacar4.setText("");
                    lblMandantePenaltis4.setText("");
                    lblVisitantePenaltis4.setText("");
                }

            }
        } else if (partidas.size() == 1) {
            lblMandantePartida4.setVisibility(View.INVISIBLE);
            lblVisitantePartida4.setVisibility(View.INVISIBLE);
            lblMandantePlacar4.setVisibility(View.INVISIBLE);
            lblVisitantePlacar4.setVisibility(View.INVISIBLE);
            lblMandantePenaltis4.setVisibility(View.INVISIBLE);
            lblVisitantePenaltis4.setVisibility(View.INVISIBLE);
            lblX4.setVisibility(View.INVISIBLE);
            lblMandantePartida3.setVisibility(View.INVISIBLE);
            lblVisitantePartida3.setVisibility(View.INVISIBLE);
            lblMandantePlacar3.setVisibility(View.INVISIBLE);
            lblVisitantePlacar3.setVisibility(View.INVISIBLE);
            lblMandantePenaltis3.setVisibility(View.INVISIBLE);
            lblVisitantePenaltis3.setVisibility(View.INVISIBLE);
            lblX3.setVisibility(View.INVISIBLE);
            lblMandantePartida1.setVisibility(View.INVISIBLE);
            lblVisitantePartida1.setVisibility(View.INVISIBLE);
            lblMandantePlacar1.setVisibility(View.INVISIBLE);
            lblVisitantePlacar1.setVisibility(View.INVISIBLE);
            lblMandantePenaltis1.setVisibility(View.INVISIBLE);
            lblVisitantePenaltis1.setVisibility(View.INVISIBLE);
            lblX1.setVisibility(View.INVISIBLE);
            if (partidas.get(0).isCadastrada()) {
                lblMandantePartida2.setText(partidas.get(0).getNomeMandante());
                lblVisitantePartida2.setText(partidas.get(0).getNomeVisitante());
                lblMandantePlacar2.setText(String.valueOf(partidas.get(0).getPlacarMandante()));
                lblVisitantePlacar2.setText(String.valueOf(partidas.get(0).getPlacarVisitante()));
                if (partidas.get(0).getPenaltisMandante() != 0 && partidas.get(0).getPenaltisVisitante() != 0) {
                    lblMandantePenaltis2.setText(String.valueOf(partidas.get(0).getPenaltisMandante()));
                    lblVisitantePenaltis2.setText(String.valueOf(partidas.get(0).getPenaltisVisitante()));
                } else {
                    lblMandantePenaltis2.setText("");
                    lblVisitantePenaltis2.setText("");
                }
            } else {
                lblMandantePartida2.setText(partidas.get(0).getNomeMandante());
                lblVisitantePartida2.setText(partidas.get(0).getNomeVisitante());
                lblMandantePlacar2.setText("");
                lblVisitantePlacar2.setText("");
                lblMandantePenaltis2.setText("");
                lblVisitantePenaltis2.setText("");
            }
        }
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