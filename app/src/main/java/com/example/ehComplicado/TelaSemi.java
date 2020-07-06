package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.example.ehComplicado.FirebaseHelper.PartidaHelper;
import com.example.ehComplicado.FirebaseHelper.TimeHelper;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;
import model.bean.Campeonato;
import model.bean.Partida;
import model.bean.Time;
import model.dao.PartidaDAO;

public class TelaSemi extends AppCompatActivity {
    AccountHeader headerNavigation;
    DatabaseReference partidaReference, timeReference,campReference;
    FirebaseUser user;
    private String campKey;
    Toolbar toolbar;
    PartidaHelper partidaHelper;
    TimeHelper timeHelper;
    ValueEventListener partidaListener,campListener,campListener2,campListener3;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_semi);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.ftc_semi_finais);
        final TextView lblMandantePartida1 = findViewById(R.id.lbl_mandante_partida1);
        final TextView lblMandantePartida2 = findViewById(R.id.lbl_mandante_partida2);
        final TextView lblMandantePlacar1 = findViewById(R.id.lbl_placar_mandante_partida1);
        final TextView lblMandantePlacar2 = findViewById(R.id.lbl_placar_mandante_partida2);
        final TextView lblVisitantePlacar1 = findViewById(R.id.lbl_placar_visitante_partida1);
        final TextView lblVisitantePlacar2 = findViewById(R.id.lbl_placar_visitante_partida2);
        final TextView lblMandantePenaltis1 = findViewById(R.id.lbl_penalti_mandante_partida1);
        final TextView lblMandantePenaltis2 = findViewById(R.id.lbl_penalti_mandante_partida2);
        final TextView lblVisitantePartida1 = findViewById(R.id.lbl_visitante_partida1);
        final TextView lblVisitantePartida2 = findViewById(R.id.lbl_visitante_partida2);
        final TextView lblVisitantePenaltis1 = findViewById(R.id.lbl_penalti_visitante_partida1);
        final TextView lblVisitantePenaltis2 = findViewById(R.id.lbl_penalti_visitante_partida2);

        final PartidaDAO partidaDAO = new PartidaDAO();

        user = getIntent().getParcelableExtra("user");
        campKey = getIntent().getStringExtra("campKey");
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        partidaReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-partidas").child(campKey);
        timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);
        timeHelper = new TimeHelper(timeReference);
        partidaHelper = new PartidaHelper(partidaReference);
        final List<Partida> partidasGeral = partidaHelper.retrive();


        final List<Time> times = timeHelper.retrive();
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                if (!camp.isFinalizado()) {
                    createDrawer();
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(mCampListener);
        campListener = mCampListener;
        ValueEventListener mPartidaListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<Partida> partidas1 = partidaDAO.listarFase(partidasGeral, "semi");
                final List<Partida> partidas = new ArrayList<>();
                for (Partida partida :partidas1){
                    partida = partidaDAO.configurar(times,partida);
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
                if (partidas.get(1).isCadastrada()) {
                    lblMandantePartida2.setText(partidas.get(1).getNomeMandante());
                    lblVisitantePartida2.setText(partidas.get(1).getNomeVisitante());
                    lblMandantePlacar2.setText(String.valueOf(partidas.get(1).getPlacarMandante()));
                    lblVisitantePlacar2.setText(String.valueOf(partidas.get(1).getPlacarVisitante()));
                    if (partidas.get(1).getPenaltisMandante() != 0 && partidas.get(1).getPenaltisVisitante() != 0) {
                        lblMandantePenaltis2.setText(String.valueOf(partidas.get(1).getPenaltisMandante()));
                        lblVisitantePenaltis2.setText(String.valueOf(partidas.get(1).getPenaltisVisitante()));
                    }
                }else{
                    lblMandantePartida2.setText(partidas.get(1).getNomeMandante());
                    lblVisitantePartida2.setText(partidas.get(1).getNomeVisitante());
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
    public void onStop(){
        super.onStop();
        if (partidaListener!=null){
            partidaReference.removeEventListener(partidaListener);
        }
        if (campListener != null) {
            campReference.removeEventListener(campListener);
        }
        if (campListener2 != null) {
            campReference.removeEventListener(campListener2);
        }
        if (campListener3 != null) {
            campReference.removeEventListener(campListener3);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                if (!camp.isSemi()) {
                    getMenuInflater().inflate(R.menu.ftc_menu_fases, menu);
                } else {
                    getMenuInflater().inflate(R.menu.ftc_menu_ant, menu);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(mCampListener);
        campListener2 = mCampListener;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ValueEventListener mCampListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                        if (camp.isFinalizado()) {
                            Intent it = new Intent(TelaSemi.this, TelaPremios.class);
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
                Intent it = new Intent(TelaSemi.this, TelaFinal.class);
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
                        if((camp.getNumGrupos()==4 && camp.getClassificados()==1) ||
                                (camp.getNumGrupos()==2 && camp.getClassificados() == 2)){
                            Intent it = new Intent(TelaSemi.this,TelaQuatroGrupos.class);
                            it.putExtra("user",user);
                            it.putExtra("campKey",campKey);
                            startActivity(it);
                            finish();
                        }else{
                            Intent it = new Intent(TelaSemi.this,TelaQuartas.class);
                            it.putExtra("user",user);
                            it.putExtra("campKey",campKey);
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

    @Override
    public void onBackPressed() {
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                if (camp.isFinalizado()){
                    Intent it = new Intent(TelaSemi.this,TelaPremios.class);
                    it.putExtra("user",user);
                    it.putExtra("campKey",campKey);
                    startActivity(it);
                    finish();
                }else{
                    criarActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(mCampListener);
        campListener3 = mCampListener;
    }

    private void criarActivity() {
        Intent it = new Intent(TelaSemi.this, TelaCarregarCamp.class);
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra("user",user);
        startActivity(it);
        finish();
    }

    private void createDrawer() {
        //Itens do Drawer
        headerNavigation = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimaryDark)
                .withProfileImagesVisible(false)
                .addProfiles(new ProfileDrawerItem().withName(user.getDisplayName()).withEmail(user.getEmail())
                )
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
                        if (drawerItem.getIdentifier()==1) {
                            Intent it = new Intent(TelaSemi.this, TelaJogos.class);
                            it.putExtra("user",user);
                            it.putExtra("campKey",campKey);
                            startActivity(it);
                            finish();
                        } else if (drawerItem.getIdentifier()==2) {
                            Intent it = new Intent(TelaSemi.this, TelaArtilheiros.class);
                            it.putExtra("user",user);
                            it.putExtra("campKey",campKey);
                            startActivity(it);
                            finish();
                        } else if (drawerItem.getIdentifier()==3) {
                            Intent it = new Intent(TelaSemi.this, TelaSuspensos.class);
                            it.putExtra("user",user);
                            it.putExtra("campKey",campKey);
                            startActivity(it);
                            finish();
                        }else if(drawerItem.getIdentifier()==4){
                            Intent it = new Intent(TelaSemi.this,TelaPendurados.class);
                            it.putExtra("user",user);
                            it.putExtra("campKey",campKey);
                            startActivity(it);
                            finish();
                        } else if (drawerItem.getIdentifier()==5) {
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
    }
}
