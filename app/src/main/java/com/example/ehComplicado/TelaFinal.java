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
import model.bean.Time;
import model.dao.PartidaDAO;

public class TelaFinal extends Fragment {

    private DatabaseReference partidaReference;
    private DatabaseReference campReference;

    private PartidaDAO partidaDAO;
    private ValueEventListener partidaListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_final,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView lblMandantePartida1 = view.findViewById(R.id.lbl_mandante_partida1);
        final TextView lblMandantePlacar1 = view.findViewById(R.id.lbl_placar_mandante_partida1);
        final TextView lblVisitantePlacar1 = view.findViewById(R.id.lbl_placar_visitante_partida1);
        final TextView lblMandantePenaltis1 = view.findViewById(R.id.lbl_penalti_mandante_partida1);
        final TextView lblVisitantePartida1 = view.findViewById(R.id.lbl_visitante_partida1);
        final TextView lblVisitantePenaltis1 = view.findViewById(R.id.lbl_penalti_visitante_partida1);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String campKey = getArguments().getString("campKey");

        partidaDAO = new PartidaDAO();
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        partidaReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-partidas").child(campKey);
        DatabaseReference timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);

        PartidaHelper partidaHelper = new PartidaHelper(partidaReference);
        TimeHelper timeHelper = new TimeHelper(timeReference);

        final List<Partida> partidasGeral = partidaHelper.retrive();

        final List<Time> times = timeHelper.retrive();
        ValueEventListener mPartidaListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<Partida> partidas1 = partidaDAO.listarFase(partidasGeral, "final");
                final List<Partida> partidas = new ArrayList<>();
                for (Partida partida : partidas1) {
                    partida = partidaDAO.configurar(times, partida);
                    partidas.add(partida);
                }
                if (partidas.get(0).isCadastrada()) {
                    lblMandantePartida1.setText(partidas.get(0).getNomeMandante());
                    lblVisitantePartida1.setText(partidas.get(0).getNomeVisitante());
                    lblMandantePlacar1.setText(String.valueOf(partidas.get(0).getPlacarMandante()));
                    lblVisitantePlacar1.setText(String.valueOf(partidas.get(0).getPlacarVisitante()));
                    if (partidas.get(0).getPenaltisMandante() != 0 && partidas.get(0).getPenaltisVisitante() != 0) {
                        lblMandantePenaltis1.setText(String.valueOf(partidas.get(0).getPenaltisMandante()));
                        lblVisitantePenaltis1.setText(String.valueOf(partidas.get(0).getPenaltisVisitante()));
                    }
                }else{
                    lblMandantePartida1.setText(partidas.get(0).getNomeMandante());
                    lblVisitantePartida1.setText(partidas.get(0).getNomeVisitante());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        partidaReference.addListenerForSingleValueEvent(mPartidaListener);
        partidaListener = mPartidaListener;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (partidaListener != null) {
            partidaReference.removeEventListener(partidaListener);
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
                            Intent it = new Intent(TelaFinal.this, TelaPremios.class);
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
                campListener2 = mCampListener;
                break;
            case R.id.btn_ant:
                Intent it = new Intent(TelaFinal.this,TelaSemi.class);
                it.putExtra("user",user);
                it.putExtra("campKey",campKey);
                startActivity(it);
                finish();
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
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.ftc_voltar).withIcon(R.drawable.back_32px);
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
                        item4

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier()==1) {
                            Intent it = new Intent(TelaFinal.this, TelaJogos.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            startActivity(it);
                            finish();
                        } else if (drawerItem.getIdentifier()==2) {
                            Intent it = new Intent(TelaFinal.this, TelaArtilheiros.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            startActivity(it);
                            finish();
                        } else if (drawerItem.getIdentifier()==3) {
                            Intent it = new Intent(TelaFinal.this, TelaSuspensos.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            startActivity(it);
                            finish();
                        } else if (drawerItem.getIdentifier()==4) {
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
