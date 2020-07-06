package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
import model.bean.Time;
import model.dao.JogadorDAO;
import model.dao.TimeDAO;

public class TelaQuatroGrupos extends AppCompatActivity {
    DatabaseReference campReference;
    List<Time> times;
    ValueEventListener campListener, campListener2, campListener3, campListener4;
    AccountHeader headerNavigation;
    private Campeonato camp;
    JogadorDAO jogadorDAO;
    Toolbar toolbar;
    FirebaseUser user;
    String campKey;
    TimeHelper timeHelper;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_quatro_grupos);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.ftc_fase_de_grupos);

        final TableLayout tbGrupo1 = findViewById(R.id.tabela_grupo_1);
        final Spinner spinner = findViewById(R.id.spGrupo);
        spinner.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        user = getIntent().getParcelableExtra("user");
        campKey = getIntent().getStringExtra("campKey");
        campReference = FirebaseDatabase.getInstance().getReference().child("campeonatos").child(campKey);
        DatabaseReference timesReference = FirebaseDatabase.getInstance().getReference().child("campeonato-times").child(campKey);
        timeHelper = new TimeHelper(timesReference);
        times = timeHelper.retrive();
        final TimeDAO timeDAO = new TimeDAO();
        jogadorDAO = new JogadorDAO();
        camp = new Campeonato();
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                camp = dataSnapshot.getValue(Campeonato.class);
                List<String> grupos = new ArrayList<>();
                for (int i = 0; i < camp.getNumGrupos(); i++) {
                    grupos.add(getString(R.string.grupoEspaco) + (i + 1));
                }
                final ArrayAdapter adapter =
                        new ArrayAdapter<>(TelaQuatroGrupos.this, R.layout.personalizado_list_item, grupos);
                spinner.setAdapter(adapter);
                if (!camp.isFinalizado()) {
                    createDrawer();
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
                if (!camp.isIniciado() && !camp.isFinalizado()) {
                    List<Time> cabecasDeChave = timeDAO.listarCabecasDeChave(times);
                    for (Time time : cabecasDeChave) {
                        if (time.getGrupo() == 1) {
                            View tr = getLayoutInflater().inflate(R.layout.tabela_linha_layout, null, false);
                            TextView txtNome = tr.findViewById(R.id.nome);
                            txtNome.setText(time.getNome());
                            TextView txtPontos = tr.findViewById(R.id.pts);
                            txtPontos.setText(String.valueOf(time.getPontos()));
                            TextView txtSg = tr.findViewById(R.id.sg);
                            txtSg.setText(String.valueOf(time.getSaldo()));
                            TextView txtGp = tr.findViewById(R.id.gp);
                            txtGp.setText(String.valueOf(time.getGolsPro()));
                            tbGrupo1.addView(tr, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                        }
                    }
                    List<Time> times1 = timeDAO.listarGrupoSemCC(1, times);
                    for (Time time : times1) {
                        View tr = getLayoutInflater().inflate(R.layout.tabela_linha_layout, null, false);
                        TextView txtNome = tr.findViewById(R.id.nome);
                        txtNome.setText(time.getNome());
                        TextView txtPontos = tr.findViewById(R.id.pts);
                        txtPontos.setText(String.valueOf(time.getPontos()));
                        TextView txtSg = tr.findViewById(R.id.sg);
                        txtSg.setText(String.valueOf(time.getSaldo()));
                        TextView txtGp = tr.findViewById(R.id.gp);
                        txtGp.setText(String.valueOf(time.getGolsPro()));
                        tbGrupo1.addView(tr, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    }
                } else {
                    List<Time> times1 = timeDAO.listarGrupoComCC(1,times);
                    for (Time time : times1) {
                        View tr = getLayoutInflater().inflate(R.layout.tabela_linha_layout, null, false);
                        TextView txtNome = tr.findViewById(R.id.nome);
                        txtNome.setText(time.getNome());
                        TextView txtPontos = tr.findViewById(R.id.pts);
                        txtPontos.setText(String.valueOf(time.getPontos()));
                        TextView txtSg = tr.findViewById(R.id.sg);
                        txtSg.setText(String.valueOf(time.getSaldo()));
                        TextView txtGp = tr.findViewById(R.id.gp);
                        txtGp.setText(String.valueOf(time.getGolsPro()));
                        tbGrupo1.addView(tr, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        campReference.addValueEventListener(mCampListener);
        campListener = mCampListener;


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                tbGrupo1.removeAllViews();
                View tr = getLayoutInflater().inflate(R.layout.tabela_linha_layout, null, false);
                TextView txtNome = tr.findViewById(R.id.nome);
                txtNome.setText(getString(R.string.time_maiusculo));
                TextView txtPontos = tr.findViewById(R.id.pts);
                txtPontos.setText(getString(R.string.pts));
                TextView txtSg = tr.findViewById(R.id.sg);
                txtSg.setText(getString(R.string.sg));
                TextView txtGp = tr.findViewById(R.id.gp);
                txtGp.setText(getString(R.string.gp));
                tbGrupo1.addView(tr, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        camp = dataSnapshot.getValue(Campeonato.class);
                        if (!camp.isIniciado() && !camp.isFinalizado()) {
                            List<Time> cabecasDeChave = timeDAO.listarCabecasDeChave(times);
                            for (Time time : cabecasDeChave) {
                                if (time.getGrupo() == position + 1) {
                                    View tr = getLayoutInflater().inflate(R.layout.tabela_linha_layout, null, false);
                                    TextView txtNome = tr.findViewById(R.id.nome);
                                    txtNome.setText(time.getNome());
                                    TextView txtPontos = tr.findViewById(R.id.pts);
                                    txtPontos.setText(String.valueOf(time.getPontos()));
                                    TextView txtSg = tr.findViewById(R.id.sg);
                                    txtSg.setText(String.valueOf(time.getSaldo()));
                                    TextView txtGp = tr.findViewById(R.id.gp);
                                    txtGp.setText(String.valueOf(time.getGolsPro()));
                                    tbGrupo1.addView(tr, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                }
                            }
                            List<Time> grupoNormal = timeDAO.listarGrupoSemCC((position + 1), times);
                            for (Time time : grupoNormal) {
                                View tr = getLayoutInflater().inflate(R.layout.tabela_linha_layout, null, false);
                                TextView txtNome = tr.findViewById(R.id.nome);
                                txtNome.setText(time.getNome());
                                TextView txtPontos = tr.findViewById(R.id.pts);
                                txtPontos.setText(String.valueOf(time.getPontos()));
                                TextView txtSg = tr.findViewById(R.id.sg);
                                txtSg.setText(String.valueOf(time.getSaldo()));
                                TextView txtGp = tr.findViewById(R.id.gp);
                                txtGp.setText(String.valueOf(time.getGolsPro()));
                                tbGrupo1.addView(tr, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                            }
                        } else {
                            List<Time> times1 = timeDAO.listarGrupoComCC((position + 1), times);
                            for (Time time : times1) {
                                View tr = getLayoutInflater().inflate(R.layout.tabela_linha_layout, null, false);
                                TextView txtNome = tr.findViewById(R.id.nome);
                                txtNome.setText(time.getNome());
                                TextView txtPontos = tr.findViewById(R.id.pts);
                                txtPontos.setText(String.valueOf(time.getPontos()));
                                TextView txtSg = tr.findViewById(R.id.sg);
                                txtSg.setText(String.valueOf(time.getSaldo()));
                                TextView txtGp = tr.findViewById(R.id.gp);
                                txtGp.setText(String.valueOf(time.getGolsPro()));
                                tbGrupo1.addView(tr, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                campReference.addValueEventListener(valueEventListener);
                campListener4 = valueEventListener;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                camp = dataSnapshot.getValue(Campeonato.class);
                if (!camp.isFaseDeGrupos()) {
                    getMenuInflater().inflate(R.menu.ftc_menu_prox2, menu);
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
                        camp = dataSnapshot.getValue(Campeonato.class);
                        if (camp.isFinalizado()) {
                            Intent it = new Intent(TelaQuatroGrupos.this, TelaPremios.class);
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
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        camp = dataSnapshot.getValue(Campeonato.class);
                        if (camp.getNumTimes() == 16) {
                            Intent it = new Intent(TelaQuatroGrupos.this, TelaOitavas.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            startActivity(it);
                            finish();
                        } else {
                            Intent it = new Intent(TelaQuatroGrupos.this, TelaQuartas.class);
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

    @Override
    public void onBackPressed() {
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                camp = dataSnapshot.getValue(Campeonato.class);
                if (camp.isFinalizado()) {
                    Intent it = new Intent(TelaQuatroGrupos.this, TelaPremios.class);
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
    }

    private void criarActivity() {
        Intent it = new Intent(TelaQuatroGrupos.this, TelaCarregarCamp.class);
        it.putExtra("user", user);
        startActivity(it);
        finish();
    }


    private void createDrawer() {
        //Itens do Drawer
        headerNavigation = new AccountHeaderBuilder()
                .withActivity(this)
                .withProfileImagesVisible(false)
                .withHeaderBackground(R.color.colorPrimaryDark)
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
                            Intent it = new Intent(TelaQuatroGrupos.this, TelaJogos.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            startActivity(it);
                            finish();
                        } else if (drawerItem.getIdentifier()==2) {
                            Intent it = new Intent(TelaQuatroGrupos.this, TelaArtilheiros.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            startActivity(it);
                            finish();
                        } else if (drawerItem.getIdentifier()==3) {
                            Intent it = new Intent(TelaQuatroGrupos.this, TelaSuspensos.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            startActivity(it);
                            finish();
                        } else if (drawerItem.getIdentifier()==4) {
                            Intent it = new Intent(TelaQuatroGrupos.this, TelaPendurados.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
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
