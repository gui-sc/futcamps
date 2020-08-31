package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import model.bean.Partida;
import model.bean.Campeonato;
import model.bean.Time;
import model.dao.PartidaDAO;

public class TelaQuartas extends Fragment {
    private DatabaseReference partidaReference;
    private DatabaseReference campReference;
    private FirebaseUser user;
    private ValueEventListener campListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_quartas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView lblMandantePartida1 = view.findViewById(R.id.lbl_mandante_partida1);
        final TextView lblMandantePartida2 = view.findViewById(R.id.lbl_mandante_partida2);
        final TextView lblMandantePartida3 = view.findViewById(R.id.lbl_mandante_partida3);
        final TextView lblMandantePartida4 = view.findViewById(R.id.lbl_mandante_partida4);
        final TextView lblMandantePlacar1 = view.findViewById(R.id.lbl_placar_mandante_partida1);
        final TextView lblMandantePlacar2 = view.findViewById(R.id.lbl_placar_mandante_partida2);
        final TextView lblMandantePlacar3 = view.findViewById(R.id.lbl_placar_mandante_partida3);
        final TextView lblMandantePlacar4 = view.findViewById(R.id.lbl_placar_mandante_partida4);
        final TextView lblVisitantePlacar1 = view.findViewById(R.id.lbl_placar_visitante_partida1);
        final TextView lblVisitantePlacar2 = view.findViewById(R.id.lbl_placar_visitante_partida2);
        final TextView lblVisitantePlacar3 = view.findViewById(R.id.lbl_placar_visitante_partida3);
        final TextView lblVisitantePlacar4 = view.findViewById(R.id.lbl_placar_visitante_partida4);
        final TextView lblMandantePenaltis1 = view.findViewById(R.id.lbl_penalti_mandante_partida1);
        final TextView lblMandantePenaltis2 = view.findViewById(R.id.lbl_penalti_mandante_partida2);
        final TextView lblMandantePenaltis3 = view.findViewById(R.id.lbl_penalti_mandante_partida3);
        final TextView lblMandantePenaltis4 = view.findViewById(R.id.lbl_penalti_mandante_partida4);
        final TextView lblVisitantePartida1 = view.findViewById(R.id.lbl_visitante_partida1);
        final TextView lblVisitantePartida2 = view.findViewById(R.id.lbl_visitante_partida2);
        final TextView lblVisitantePartida3 = view.findViewById(R.id.lbl_visitante_partida3);
        final TextView lblVisitantePartida4 = view.findViewById(R.id.lbl_visitante_partida4);
        final TextView lblVisitantePenaltis1 = view.findViewById(R.id.lbl_penalti_visitante_partida1);
        final TextView lblVisitantePenaltis2 = view.findViewById(R.id.lbl_penalti_visitante_partida2);
        final TextView lblVisitantePenaltis3 = view.findViewById(R.id.lbl_penalti_visitante_partida3);
        final TextView lblVisitantePenaltis4 = view.findViewById(R.id.lbl_penalti_visitante_partida4);
        final TextView lblX3 = view.findViewById(R.id.lblX3);
        final TextView lblX4 = view.findViewById(R.id.lblX4);
        final PartidaDAO partidaDAO = new PartidaDAO();

        user = FirebaseAuth.getInstance().getCurrentUser();
        String campKey = getArguments().getString("campKey");
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        partidaReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-partidas").child(campKey);
        DatabaseReference timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);
        TimeHelper timeHelper = new TimeHelper(timeReference);
        PartidaHelper partidaHelper = new PartidaHelper(partidaReference);
        final List<Partida> partidasGeral = partidaHelper.retrive();

        final List<Time> times = timeHelper.retrive();


        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Campeonato camp = dataSnapshot.getValue(Campeonato.class);
               /* if (!camp.isFinalizado()) {
                    createDrawer();
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }*/
                final List<Partida> partidas1 = partidaDAO.listarFase(partidasGeral, "quartas");
                final List<Partida> partidas = new ArrayList<>();
                for (Partida partida : partidas1) {
                    partida = partidaDAO.configurar(times, partida);
                    partidas.add(partida);
                }
                if (partidas.size() == 4) {
                    if (partidas.get(0).isCadastrada()) {
                        lblMandantePartida1.setText(partidas.get(0).getNomeMandante());
                        lblVisitantePartida1.setText(partidas.get(0).getNomeVisitante());
                        lblMandantePlacar1.setText(String.valueOf(partidas.get(0).getPlacarMandante()));
                        lblVisitantePlacar1.setText(String.valueOf(partidas.get(0).getPlacarVisitante()));
                        if (partidas.get(0).getPenaltisMandante() != 0 && partidas.get(0).getPenaltisVisitante() != 0) {
                            lblMandantePenaltis1.setText(String.valueOf(partidas.get(0).getPenaltisMandante()));
                            lblVisitantePenaltis1.setText(String.valueOf(partidas.get(0).getPenaltisVisitante()));
                        }
                    } else {
                        lblMandantePartida1.setText(partidas.get(0).getNomeMandante());
                        lblVisitantePartida1.setText(partidas.get(0).getNomeVisitante());
                    }
                    if (partidas.get(1).isCadastrada()) {
                        lblMandantePartida2.setText(partidas.get(1).getNomeMandante());
                        lblVisitantePartida2.setText(partidas.get(1).getNomeVisitante());
                        lblMandantePlacar2.setText(String.valueOf(partidas.get(1).getPlacarMandante()));
                        lblVisitantePlacar2.setText(String.valueOf(partidas.get(1).getPlacarVisitante()));
                        if (partidas.get(1).getPenaltisMandante() != 0 && partidas.get(1).getPenaltisVisitante() != 0) {
                            lblMandantePenaltis2.setText(String.valueOf(partidas.get(1).getPenaltisMandante()));
                            lblVisitantePenaltis2.setText(String.valueOf(partidas.get(1).getPenaltisVisitante()));
                        }
                    } else {
                        lblMandantePartida2.setText(partidas.get(1).getNomeMandante());
                        lblVisitantePartida2.setText(partidas.get(1).getNomeVisitante());
                    }
                    if (partidas.get(2).isCadastrada()) {
                        lblMandantePartida3.setText(partidas.get(2).getNomeMandante());
                        lblVisitantePartida3.setText(partidas.get(2).getNomeVisitante());
                        lblMandantePlacar3.setText(String.valueOf(partidas.get(2).getPlacarMandante()));
                        lblVisitantePlacar3.setText(String.valueOf(partidas.get(2).getPlacarVisitante()));
                        if (partidas.get(2).getPenaltisMandante() != 0 && partidas.get(2).getPenaltisVisitante() != 0) {
                            lblMandantePenaltis3.setText(String.valueOf(partidas.get(2).getPenaltisMandante()));
                            lblVisitantePenaltis3.setText(String.valueOf(partidas.get(2).getPenaltisVisitante()));
                        }
                    } else {
                        lblMandantePartida3.setText(partidas.get(2).getNomeMandante());
                        lblVisitantePartida3.setText(partidas.get(2).getNomeVisitante());
                    }
                    if (partidas.get(3).isCadastrada()) {
                        lblMandantePartida4.setText(partidas.get(3).getNomeMandante());
                        lblVisitantePartida4.setText(partidas.get(3).getNomeVisitante());
                        lblMandantePlacar4.setText(String.valueOf(partidas.get(3).getPlacarMandante()));
                        lblVisitantePlacar4.setText(String.valueOf(partidas.get(3).getPlacarVisitante()));
                        if (partidas.get(3).getPenaltisMandante() != 0 && partidas.get(3).getPenaltisVisitante() != 0) {
                            lblMandantePenaltis4.setText(String.valueOf(partidas.get(3).getPenaltisMandante()));
                            lblVisitantePenaltis4.setText(String.valueOf(partidas.get(3).getPenaltisVisitante()));
                        }
                    } else {
                        lblMandantePartida4.setText(partidas.get(3).getNomeMandante());
                        lblVisitantePartida4.setText(partidas.get(3).getNomeVisitante());
                    }
                } else if (partidas.size() == 2) {
                    if (partidas.get(0).isCadastrada()) {
                        lblMandantePartida1.setText(partidas.get(0).getNomeMandante());
                        lblVisitantePartida1.setText(partidas.get(0).getNomeVisitante());
                        lblMandantePlacar1.setText(String.valueOf(partidas.get(0).getPlacarMandante()));
                        lblVisitantePlacar1.setText(String.valueOf(partidas.get(0).getPlacarVisitante()));
                        if (partidas.get(0).getPenaltisMandante() != 0 && partidas.get(0).getPenaltisVisitante() != 0) {
                            lblMandantePenaltis1.setText(String.valueOf(partidas.get(0).getPenaltisMandante()));
                            lblVisitantePenaltis1.setText(String.valueOf(partidas.get(0).getPenaltisVisitante()));
                        }
                    } else {
                        lblMandantePartida1.setText(partidas.get(0).getNomeMandante());
                        lblVisitantePartida1.setText(partidas.get(0).getNomeVisitante());
                    }
                    if (partidas.get(1).isCadastrada()) {
                        lblMandantePartida2.setText(partidas.get(1).getNomeMandante());
                        lblVisitantePartida2.setText(partidas.get(1).getNomeVisitante());
                        lblMandantePlacar2.setText(String.valueOf(partidas.get(1).getPlacarMandante()));
                        lblVisitantePlacar2.setText(String.valueOf(partidas.get(1).getPlacarVisitante()));
                        if (partidas.get(1).getPenaltisMandante() != 0 && partidas.get(1).getPenaltisVisitante() != 0) {
                            lblMandantePenaltis2.setText(String.valueOf(partidas.get(1).getPenaltisMandante()));
                            lblVisitantePenaltis2.setText(String.valueOf(partidas.get(1).getPenaltisVisitante()));
                        }
                    } else {
                        lblMandantePartida2.setText(partidas.get(1).getNomeMandante());
                        lblVisitantePartida2.setText(partidas.get(1).getNomeVisitante());
                    }
                    lblMandantePartida3.setVisibility(View.INVISIBLE);
                    lblVisitantePartida3.setVisibility(View.INVISIBLE);
                    lblMandantePenaltis3.setVisibility(View.INVISIBLE);
                    lblMandantePlacar3.setVisibility(View.INVISIBLE);
                    lblVisitantePenaltis3.setVisibility(View.INVISIBLE);
                    lblVisitantePlacar3.setVisibility(View.INVISIBLE);
                    lblX3.setVisibility(View.INVISIBLE);
                    lblMandantePartida4.setVisibility(View.INVISIBLE);
                    lblVisitantePartida4.setVisibility(View.INVISIBLE);
                    lblMandantePenaltis4.setVisibility(View.INVISIBLE);
                    lblMandantePlacar4.setVisibility(View.INVISIBLE);
                    lblVisitantePenaltis4.setVisibility(View.INVISIBLE);
                    lblVisitantePlacar4.setVisibility(View.INVISIBLE);
                    lblX4.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(mCampListener);
        campListener = mCampListener;


    }

    @Override
    public void onStop() {
        super.onStop();
        if (campListener != null) {
            campReference.removeEventListener(campListener);
        }
    }


   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ValueEventListener mCampListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                        if (camp.isFinalizado()) {
                            Intent it = new Intent(TelaQuartas.this, TelaPremios.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            startActivity(it);
                            finish();
                        } else {
                            criarActivity();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                campReference.addListenerForSingleValueEvent(mCampListener);
                campListener3 = mCampListener;
                break;
            case R.id.btn_prox:
                Intent it = new Intent(TelaQuartas.this, TelaSemi.class);
                it.putExtra("user", user);
                it.putExtra("campKey", campKey);
                startActivity(it);
                finish();
                break;
            case R.id.btn_ant:
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                        if ((camp.getNumGrupos() == 4 && camp.getClassificados() == 2) ||
                                (camp.getNumGrupos() == 2 && (camp.getClassificados() == 3 || camp.getClassificados() == 4))) {
                            Intent it = new Intent(TelaQuartas.this, TelaQuatroGrupos.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            startActivity(it);
                            finish();
                        } else {
                            Intent it = new Intent(TelaQuartas.this, TelaOitavas.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            startActivity(it);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                campReference.addListenerForSingleValueEvent(valueEventListener);
                campListener3 = valueEventListener;
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void createDrawer() {
        //Itens do Drawer
        headerNavigation = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimaryDark)
                .withProfileImagesVisible(false)
                .addProfiles(new ProfileDrawerItem().withName(user.getDisplayName()).withEmail(user.getEmail()))
                .build();
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.ftc_jogos).withIcon(R.drawable.soccer_ball_32px);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.ftc_artilheiros).withIcon(R.drawable.soccer_32px);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.ftc_suspensos).withIcon(R.drawable.soccer_card_32px);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.ftc_pendurados).withIcon(R.drawable.soccer_card_32px);
        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(5).withName(R.string.ftc_voltar).withIcon(R.drawable.back_32px);
        //Definição do Drawer
        Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withSliderBackgroundDrawableRes(R.drawable.gradient)
                .withToolbar(toolbar)
                .withAccountHeader(headerNavigation)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),//Divisor
                        item2,
                        new DividerDrawerItem(),
                        item3,
                        new DividerDrawerItem(),
                        item4,
                        new DividerDrawerItem(),
                        item5

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 1) {
                            Intent it = new Intent(TelaQuartas.this, TelaJogos.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            startActivity(it);
                            finish();
                        } else if (drawerItem.getIdentifier() == 2) {
                            Intent it = new Intent(TelaQuartas.this, TelaArtilheiros.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            startActivity(it);
                            finish();
                        } else if (drawerItem.getIdentifier() == 3) {
                            Intent it = new Intent(TelaQuartas.this, TelaSuspensos.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            startActivity(it);
                            finish();
                        } else if (drawerItem.getIdentifier() == 4) {
                            Intent it = new Intent(TelaQuartas.this, TelaPendurados.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            startActivity(it);
                            finish();
                        } else if (drawerItem.getIdentifier() == 5) {
                            criarActivity();
                        }
                        return false;
                    }
                })
                .withSelectedItemByPosition(-1)
                .build();
        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer.getActionBarDrawerToggle().setHomeAsUpIndicator(R.drawable.menu_32px);
    }*/
}
