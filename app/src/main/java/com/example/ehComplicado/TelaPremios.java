package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

import model.bean.Campeonato;
import model.bean.Resultados;

public class TelaPremios extends AppCompatActivity {
    TextView lblCampeao, lblVice, lblArtilheiro, lblGols;
    String campKey;
    FirebaseUser user;
    Resultados resultados;
    ValueEventListener resultadoListener, campListener;
    DatabaseReference resultadoReference, campReference;
    Toolbar toolbar;
    AccountHeader headerNavigation;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_premios);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.ftc_resultados);
        user = getIntent().getParcelableExtra("user");
        campKey = getIntent().getStringExtra("campKey");
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        resultadoReference = FirebaseDatabase.getInstance().getReference()
                .child("resultado-campeonatos").child(campKey);
        lblCampeao = findViewById(R.id.lbl_campeao);
        lblVice = findViewById(R.id.lbl_vice_campeao);
        lblArtilheiro = findViewById(R.id.lbl_artilheiro);
        lblGols = findViewById(R.id.lbl_gols);
        resultados = new Resultados();
        ValueEventListener mResultadoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                resultados = dataSnapshot.getValue(Resultados.class);
                lblCampeao.setText(resultados.getCampeao());
                lblVice.setText(resultados.getViceCampeao());
                lblArtilheiro.setText(resultados.getArtilheiro());
                lblGols.setText(String.valueOf(resultados.getGols()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        resultadoReference.addValueEventListener(mResultadoListener);
        resultadoListener = mResultadoListener;
        createDrawer();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (resultadoListener != null) {
            resultadoReference.removeEventListener(resultadoListener);
        }
        if (campListener != null) {
            campReference.removeEventListener(campListener);
        }
    }

    @Override
    public void onBackPressed() {
        criarActivity();
    }

    private void criarActivity() {
        Intent it = new Intent(TelaPremios.this, TelaCarregarCamp.class);
        it.putExtra("user", user);
        startActivity(it);
        finish();
    }

    private void createDrawer() {
        //Itens do Drawer
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                headerNavigation = new AccountHeaderBuilder()
                        .withActivity(TelaPremios.this)
                        .withHeaderBackground(R.color.colorPrimaryDark)
                        .withProfileImagesVisible(false)
                        .addProfiles(new ProfileDrawerItem().withName(user.getDisplayName()).withEmail(user.getEmail())
                        )
                        .build();
                PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.ftc_jogos).withIcon(R.drawable.soccer_ball_32px);
                PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.ftc_chaveamento).withIcon(R.drawable.soccer_32px);
                PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.ftc_artilheiros).withIcon(R.drawable.soccer_card_32px);
                PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.ftc_voltar).withIcon(R.drawable.back_32px);
                //Definição do Drawer
                Drawer drawer = new DrawerBuilder()
                        .withActivity(TelaPremios.this)
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
                                if (drawerItem.getIdentifier() == 1) {
                                    Intent it = new Intent(TelaPremios.this, TelaJogos.class);
                                    it.putExtra("user", user);
                                    it.putExtra("campKey", campKey);
                                    startActivity(it);
                                    finish();
                                } else if (drawerItem.getIdentifier() == 3) {
                                    Intent it = new Intent(TelaPremios.this, TelaArtilheiros.class);
                                    it.putExtra("campKey", campKey);
                                    it.putExtra("user", user);
                                    startActivity(it);
                                    finish();
                                } else if (drawerItem.getIdentifier() == 2) {
                                    if (camp.getNumGrupos() > 0) {
                                        Intent it = new Intent(TelaPremios.this, TelaQuatroGrupos.class);
                                        it.putExtra("campKey", campKey);
                                        it.putExtra("user", user);
                                        startActivity(it);
                                        finish();
                                    }else{
                                        if(camp.getNumTimes()==6 || camp.getNumTimes()==8){
                                            Intent it = new Intent(TelaPremios.this, TelaQuartas.class);
                                            it.putExtra("campKey", campKey);
                                            it.putExtra("user", user);
                                            startActivity(it);
                                            finish();
                                        }else{
                                            Intent it = new Intent(TelaPremios.this, TelaOitavas.class);
                                            it.putExtra("campKey", campKey);
                                            it.putExtra("user", user);
                                            startActivity(it);
                                            finish();
                                        }
                                    }
                                } else if (drawerItem.getIdentifier() == 4) {
                                    Intent it = new Intent(TelaPremios.this, TelaCarregarCamp.class);
                                    it.putExtra("user", user);
                                    startActivity(it);
                                    finish();
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(mCampListener);
        campListener = mCampListener;
    }
}
