package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ehComplicado.FirebaseHelper.TimeHelper;
import com.google.firebase.auth.FirebaseAuth;
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

public class TelaQuatroGrupos extends Fragment {
    private DatabaseReference campReference;
    private List<Time> times;
    private ValueEventListener campListener,campListener2, campListener3, campListener4,campListener5,campListener6;

    private Campeonato camp;

    private FirebaseUser user;
    private String campKey;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_quatro_grupos,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TableLayout tbGrupo1 = view.findViewById(R.id.tabela_grupo_1);
        final Spinner spinner = view.findViewById(R.id.spGrupo);
        spinner.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        user = FirebaseAuth.getInstance().getCurrentUser();
        campKey = getArguments().getString("campKey");
        campReference = FirebaseDatabase.getInstance().getReference().child("campeonatos").child(campKey);
        DatabaseReference timesReference = FirebaseDatabase.getInstance().getReference().child("campeonato-times").child(campKey);
        TimeHelper timeHelper = new TimeHelper(timesReference);
        times = timeHelper.retrive();
        final TimeDAO timeDAO = new TimeDAO();
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
                        new ArrayAdapter<>(getContext(), R.layout.personalizado_list_item, grupos);
                spinner.setAdapter(adapter);
               /* if (!camp.isFinalizado()) {
                    createDrawer();
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }*/
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
                campListener6 = eventListener;

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
        TextView tv = view.findViewById(R.id.lbl_depois);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                        if(!camp.isFaseDeGrupos()){
                            TelaOitavas t = new TelaOitavas();
                            Bundle data = new Bundle();
                            data.putString("campKey",campKey);
                            t.setArguments(data);
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.container, t);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }else{
                            Toast.makeText(getContext(), "Você ainda não pode avançar!", Toast.LENGTH_SHORT).show();
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
        }if (campListener6 != null) {
            campReference.removeEventListener(campListener6);
        }
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
                            Intent it = new Intent(getContext(), TelaPremios.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            TelaCamps t = (TelaCamps)getActivity();
                            t.startActivity(it);
                            t.finish();
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
                            Intent it = new Intent(getContext(), TelaOitavas.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            TelaCamps t = (TelaCamps)getActivity();
                            t.startActivity(it);
                            t.finish();
                        } else {
                            Intent it = new Intent(getContext(), TelaQuartas.class);
                            it.putExtra("user", user);
                            it.putExtra("campKey", campKey);
                            TelaCamps t = (TelaCamps)getActivity();
                            t.startActivity(it);
                            t.finish();
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

    private void criarActivity() {
        Intent it = new Intent(getContext(), TelaCarregarCamp.class);
        it.putExtra("user", user);
        TelaCamps t = (TelaCamps)getActivity();
        t.startActivity(it);
        t.finish();
    }

}
