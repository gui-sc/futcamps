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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.ehComplicado.FirebaseHelper.TimeHelper;
import com.example.ehComplicado.FirebaseHelper.UsuarioHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;

import model.bean.Campeonato;
import model.bean.Partida;
import model.bean.Time;
import model.bean.Usuario;
import model.dao.CampeonatoDAO;
import model.dao.PartidaDAO;
import model.dao.TimeDAO;

public class TelaCadastroTime extends AppCompatActivity {
    private DatabaseReference campReference;
    TimeHelper timeHelper;
    private String campKey;
    private TimeDAO timeDAO;
    private Campeonato camp;
    private CampeonatoDAO campDAO;
    private String nome, dirigente, cidade;
    private boolean cabecaDeChave;
    private TextInputLayout nomeTextInput;
    private TextInputEditText nomeEditText;
    private TextInputLayout dirigenteTextInput;
    private TextInputEditText dirigenteEditText;
    private TextInputLayout cidadeTextInput;
    private TextInputEditText cidadeEditText;
    private Switch swCabecaDeChave;
    private PartidaDAO partidaDAO;
    private ValueEventListener campListener, campListener2, campListener3;
    FirebaseUser user;
    TimeHelper helper;
    List<Usuario> usuarios;
    UsuarioHelper usuarioHelper;

    List<Time> times;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_time);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.ftc_cadastro_time);
        user = getIntent().getParcelableExtra("user");
        campKey = getIntent().getStringExtra("campKey");
        partidaDAO = new PartidaDAO();
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        DatabaseReference timesReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-seguidores").child(campKey);
        usuarioHelper = new UsuarioHelper(userRef);
        usuarios = usuarioHelper.retrive();
        timeDAO = new TimeDAO();
        timeHelper = new TimeHelper(timesReference);
        times = timeHelper.retrive();
        nomeTextInput = findViewById(R.id.nome_text_input);
        nomeEditText = findViewById(R.id.nome_edit_text);
        dirigenteTextInput = findViewById(R.id.dirigente_text_input);
        dirigenteEditText = findViewById(R.id.dirigente_edit_text);
        cidadeTextInput = findViewById(R.id.cidade_text_input);
        cidadeEditText = findViewById(R.id.cidade_edit_text);
        swCabecaDeChave = findViewById(R.id.sw_cabecaDeChave);
        helper = new TimeHelper(timesReference);
        times = helper.retrive();
        ValueEventListener a = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                camp = dataSnapshot.getValue(Campeonato.class);
                if (camp.getFormato().equals(getString(R.string.matamata))) {
                    swCabecaDeChave.setVisibility(View.INVISIBLE);
                    swCabecaDeChave.setChecked(false);
                    swCabecaDeChave.setClickable(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(a);
        campListener = a;
        swCabecaDeChave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                ValueEventListener mCampListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        camp = dataSnapshot.getValue(Campeonato.class);
                        if (isChecked) {
                            if (camp.getCabecasDeChave() == camp.getNumGrupos()) {
                                Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_cabeca, Toast.LENGTH_SHORT).show();
                                swCabecaDeChave.setChecked(false);
                                cabecaDeChave = false;
                            } else {
                                cabecaDeChave = true;
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ftc_menu_salvar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                camp = dataSnapshot.getValue(Campeonato.class);
                campDAO = new CampeonatoDAO();
                switch (item.getItemId()) {
                    case android.R.id.home:
                        criarActivity();
                        break;
                    case R.id.btn_salvar:
                        nome = nomeEditText.getText().toString();
                        dirigente = dirigenteEditText.getText().toString();
                        cidade = cidadeEditText.getText().toString();
                        if (valida(nome)) {
                            nomeTextInput.setError(getString(R.string.ftc_aviso_vazio));
                        } else if (valida(dirigente)) {
                            dirigenteTextInput.setError(getString(R.string.ftc_aviso_vazio));
                        } else if (valida(cidade)) {
                            cidadeTextInput.setError(getString(R.string.ftc_aviso_vazio));
                        } else {

                            if (cabecaDeChave) {
                                campDAO.cabecaDeChaveMais(camp, usuarios);
                                cadastro();
                            } else {
                                if (camp.getNumGrupos() == 4) {
                                    if (camp.getNumTimes() == 16) {
                                        if (camp.getCabecasDeChave() == 0 && times.size() == 12) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else if (camp.getCabecasDeChave() == 1 && times.size() == 13) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else if (camp.getCabecasDeChave() == 2 && times.size() == 14) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else if (camp.getCabecasDeChave() == 13 && times.size() == 15) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else {
                                            cadastro();
                                        }
                                    } else if (camp.getNumTimes() == 12) {
                                        if (camp.getCabecasDeChave() == 0 && times.size() == 8) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else if (camp.getCabecasDeChave() == 1 && times.size() == 9) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else if (camp.getCabecasDeChave() == 2 && times.size() == 10) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else if (camp.getCabecasDeChave() == 3 && times.size() == 11) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else {
                                            cadastro();
                                        }
                                    }
                                } else if (camp.getNumGrupos() == 2) {
                                    if (camp.getNumTimes() == 6) {
                                        if (camp.getCabecasDeChave() == 0 && times.size() == 4) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else if (camp.getCabecasDeChave() == 1 && times.size() == 5) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else {
                                            cadastro();
                                        }
                                    } else if (camp.getNumTimes() == 8) {
                                        if (camp.getCabecasDeChave() == 0 && times.size() == 6) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else if (camp.getCabecasDeChave() == 1 && times.size() == 7) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else {
                                            cadastro();
                                        }
                                    } else if (camp.getNumTimes() == 10) {
                                        if (camp.getCabecasDeChave() == 0 && times.size() == 8) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else if (camp.getCabecasDeChave() == 1 && times.size() == 9) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else {
                                            cadastro();
                                        }
                                    } else if (camp.getNumTimes() == 12) {
                                        if (camp.getCabecasDeChave() == 0 && times.size() == 10) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else if (camp.getCabecasDeChave() == 1 && times.size() == 11) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else {
                                            cadastro();
                                        }
                                    } else if (camp.getNumTimes() == 16) {
                                        if (camp.getCabecasDeChave() == 0 && times.size() == 14) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else if (camp.getCabecasDeChave() == 1 && times.size() == 15) {
                                            Toast.makeText(TelaCadastroTime.this, R.string.ftc_aviso_time_cabeca, Toast.LENGTH_SHORT).show();
                                        } else {
                                            cadastro();
                                        }
                                    }
                                } else {
                                    cadastro();
                                }
                            }
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(mCampListener);
        campListener2 = mCampListener;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        criarActivity();
    }

    private void criarActivity() {
        Intent it = new Intent(TelaCadastroTime.this, TelaEditarCamp.class);
        it.putExtra("user", user);
        it.putExtra("campKey", campKey);
        startActivity(it);
        finish();
    }

    private boolean valida(String s) {
        return s.equals("");
    }

    private void cadastro() {
        Toast.makeText(this, R.string.ftc_cadastrando, Toast.LENGTH_SHORT).show();
        Time time = new Time();
        time.setNome(nome);
        time.setDirigente(dirigente);
        time.setCidade(cidade);
        time.setCabecaDeChave(cabecaDeChave);
        time.setUid(campKey);
        time.setGrupo(0);
        time.setPontos(0);
        time.setEliminado(false);
        time.setPrimeiro(false);
        time.setSaldo(0);
        time.setGolsPro(0);
        time.setGolsPro(0);
        timeDAO = new TimeDAO();
        String key = timeDAO.inserir(time, campKey);
        times.add(time);
        if (camp.getFormato().equals(getString(R.string.fasedegrupos))) {
            if (times.size() == camp.getNumTimes()) {
                List<Time> timesNormal = timeDAO.listarNaoCabecasDeChave(times);
                Collections.shuffle(timesNormal);
                List<Time> cabecasDeChave = timeDAO.listarCabecasDeChave(times);
                Collections.shuffle(cabecasDeChave);
                for (int i = 0; i < cabecasDeChave.size(); i++) {
                    cabecasDeChave.get(i).setGrupo(i + 1);
                }
                if (camp.getNumGrupos() == 1) {
                    for (Time time1 : timesNormal) {
                        time1.setGrupo(1);
                    }
                }
                if (camp.getNumGrupos() == 2) {
                    for (int i = 0; i < timesNormal.size(); i++) {
                        if (i < timesNormal.size() / 2) {
                            timesNormal.get(i).setGrupo(1);
                        } else {
                            timesNormal.get(i).setGrupo(2);
                        }
                    }
                }
                if (camp.getNumGrupos() == 4) {
                    for (int i = 0; i < timesNormal.size(); i++) {
                        if (i < timesNormal.size() / 4) {
                            timesNormal.get(i).setGrupo(1);
                        } else if ((i >= timesNormal.size() / 4) && (i < timesNormal.size() / 2)) {
                            timesNormal.get(i).setGrupo(2);
                        } else if ((i >= timesNormal.size() / 2) && (i < (timesNormal.size() / 4) * 3)) {
                            timesNormal.get(i).setGrupo(3);
                        } else if (i >= (timesNormal.size() / 4) * 3) {
                            timesNormal.get(i).setGrupo(4);
                        }
                    }
                }
                for (Time time1 : timesNormal) {
                    timeDAO.numGrupo(time1, campKey);
                }
                for (Time time1 : cabecasDeChave) {
                    timeDAO.numGrupo(time1, campKey);
                }
                Partida partida;
                if (camp.getNumGrupos() == 2) {
                    List<Time> times1 = timeDAO.listarGrupoComCC(1, times);
                    List<Time> times2 = timeDAO.listarGrupoComCC(2, times);
                    partida = new Partida();
                    partida.setFase("grupos");
                    if (camp.getNumTimes() == 8) {
                        for (int i = 0; i < 3; i++) {
                            if (i == 0) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(2));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(2));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else if (i == 1) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(1));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(1));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(1));
                                partida.setVisitante(times1.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(1));
                                partida.setVisitante(times2.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            }
                        }
                    } else if (camp.getNumTimes() == 6) {
                        for (int i = 0; i < 3; i++) {
                            if (i == 0) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else if (i == 1) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else {
                                partida.setMandante(times1.get(1));
                                partida.setVisitante(times1.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(1));
                                partida.setVisitante(times2.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            }
                        }
                    } else if (camp.getNumTimes() == 10) {
                        for (int i = 0; i < 5; i++) {
                            if (i == 0) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(2));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(2));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else if (i == 1) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(1));
                                partida.setVisitante(times1.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(1));
                                partida.setVisitante(times2.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else if (i == 2) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(1));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(1));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else if (i == 3) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(2));
                                partida.setVisitante(times1.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(2));
                                partida.setVisitante(times2.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else {
                                partida.setMandante(times1.get(1));
                                partida.setVisitante(times1.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(3));
                                partida.setVisitante(times1.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(1));
                                partida.setVisitante(times2.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(3));
                                partida.setVisitante(times2.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            }
                        }
                    } else if (camp.getNumTimes() == 12) {
                        for (int i = 0; i < 5; i++) {
                            if (i == 0) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(2));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(4));
                                partida.setVisitante(times1.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(2));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(4));
                                partida.setVisitante(times2.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else if (i == 1) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(1));
                                partida.setVisitante(times1.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(3));
                                partida.setVisitante(times1.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(1));
                                partida.setVisitante(times2.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(3));
                                partida.setVisitante(times2.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else if (i == 2) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(1));
                                partida.setVisitante(times1.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(4));
                                partida.setVisitante(times1.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(1));
                                partida.setVisitante(times2.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(4));
                                partida.setVisitante(times2.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else if (i == 3) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(1));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(2));
                                partida.setVisitante(times1.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(1));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(2));
                                partida.setVisitante(times2.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(2));
                                partida.setVisitante(times1.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(4));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(2));
                                partida.setVisitante(times2.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(4));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            }
                        }
                    } else if (camp.getNumTimes() == 16) {
                        for (int i = 0; i < 7; i++) {
                            if (i == 0) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(2));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(4));
                                partida.setVisitante(times1.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(6));
                                partida.setVisitante(times1.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(2));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(4));
                                partida.setVisitante(times2.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(6));
                                partida.setVisitante(times2.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else if (i == 1) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(1));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(4));
                                partida.setVisitante(times1.get(6));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(5));
                                partida.setVisitante(times1.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);

                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(1));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(4));
                                partida.setVisitante(times2.get(6));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(5));
                                partida.setVisitante(times2.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else if (i == 2) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(2));
                                partida.setVisitante(times1.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(4));
                                partida.setVisitante(times1.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(6));
                                partida.setVisitante(times1.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);

                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(2));
                                partida.setVisitante(times2.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(4));
                                partida.setVisitante(times2.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(6));
                                partida.setVisitante(times2.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else if (i == 3) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(2));
                                partida.setVisitante(times1.get(6));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(1));
                                partida.setVisitante(times1.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(3));
                                partida.setVisitante(times1.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);

                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(2));
                                partida.setVisitante(times2.get(6));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(1));
                                partida.setVisitante(times2.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(3));
                                partida.setVisitante(times2.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else if (i == 4) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(2));
                                partida.setVisitante(times1.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(4));
                                partida.setVisitante(times1.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(6));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);

                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(2));
                                partida.setVisitante(times2.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(4));
                                partida.setVisitante(times2.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(6));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else if (i == 5) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(6));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(2));
                                partida.setVisitante(times1.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(3));
                                partida.setVisitante(times1.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(1));
                                partida.setVisitante(times1.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);

                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(6));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(2));
                                partida.setVisitante(times2.get(4));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(3));
                                partida.setVisitante(times2.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(1));
                                partida.setVisitante(times2.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(2));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(4));
                                partida.setVisitante(times1.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(6));
                                partida.setVisitante(times1.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);

                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(7));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(2));
                                partida.setVisitante(times2.get(5));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(4));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(6));
                                partida.setVisitante(times2.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            }
                        }
                    }
                } else if (camp.getNumGrupos() == 4) {
                    List<Time> times1 = timeDAO.listarGrupoComCC(1, times);
                    List<Time> times2 = timeDAO.listarGrupoComCC(2, times);
                    List<Time> times3 = timeDAO.listarGrupoComCC(3, times);
                    List<Time> times4 = timeDAO.listarGrupoComCC(4, times);
                    partida = new Partida();
                    partida.setFase("grupos");
                    if (camp.getNumTimes() == 16) {
                        for (int i = 0; i < 3; i++) {
                            if (i == 0) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(2));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(2));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times3.get(0));
                                partida.setVisitante(times3.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times3.get(2));
                                partida.setVisitante(times3.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times4.get(0));
                                partida.setVisitante(times4.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times4.get(2));
                                partida.setVisitante(times4.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else if (i == 1) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(1));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(1));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times3.get(0));
                                partida.setVisitante(times3.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times3.get(1));
                                partida.setVisitante(times3.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times4.get(0));
                                partida.setVisitante(times4.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times4.get(1));
                                partida.setVisitante(times4.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times1.get(1));
                                partida.setVisitante(times1.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(1));
                                partida.setVisitante(times2.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times3.get(0));
                                partida.setVisitante(times3.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times3.get(1));
                                partida.setVisitante(times3.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times4.get(0));
                                partida.setVisitante(times4.get(3));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times4.get(1));
                                partida.setVisitante(times4.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            }
                        }
                    } else if (camp.getNumTimes() == 12) {
                        for (int i = 0; i < 3; i++) {
                            if (i == 0) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times3.get(0));
                                partida.setVisitante(times3.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times4.get(0));
                                partida.setVisitante(times4.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else if (i == 1) {
                                partida.setMandante(times1.get(0));
                                partida.setVisitante(times1.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(0));
                                partida.setVisitante(times2.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times3.get(0));
                                partida.setVisitante(times3.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times4.get(0));
                                partida.setVisitante(times4.get(2));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            } else {
                                partida.setMandante(times1.get(2));
                                partida.setVisitante(times1.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times2.get(2));
                                partida.setVisitante(times2.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times3.get(2));
                                partida.setVisitante(times3.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                                partida.setMandante(times4.get(2));
                                partida.setVisitante(times4.get(1));
                                partidaDAO.cadastrarPrevia(partida, campKey);
                            }
                        }
                    }
                }
                campDAO = new CampeonatoDAO();
                camp.setFaseDeGrupos(true);
                campDAO.passarDeFase(camp, usuarios);
            }
        } else {
            if (times.size() == camp.getNumTimes()) {
                Partida partida = new Partida();
                Collections.shuffle(times);
                if (camp.getNumTimes() == 6) {
                    partida.setFase("quartas");
                    partida.setMandante(times.get(0));
                    partida.setVisitante(times.get(1));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    partida.setMandante(times.get(2));
                    partida.setVisitante(times.get(3));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    campDAO = new CampeonatoDAO();
                    camp.setQuartas(true);
                } else if (camp.getNumTimes() == 8) {
                    partida.setFase("quartas");
                    partida.setMandante(times.get(0));
                    partida.setVisitante(times.get(1));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    partida.setMandante(times.get(2));
                    partida.setVisitante(times.get(3));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    partida.setMandante(times.get(4));
                    partida.setVisitante(times.get(5));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    partida.setMandante(times.get(6));
                    partida.setVisitante(times.get(7));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    camp.setQuartas(true);
                } else if (camp.getNumTimes() == 10) {
                    partida.setFase("oitavas");
                    partida.setMandante(times.get(0));
                    partida.setVisitante(times.get(1));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    partida.setMandante(times.get(2));
                    partida.setVisitante(times.get(3));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    camp.setOitavas(true);
                } else if (camp.getNumTimes() == 12) {
                    partida.setFase("oitavas");
                    partida.setMandante(times.get(0));
                    partida.setVisitante(times.get(1));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    partida.setMandante(times.get(2));
                    partida.setVisitante(times.get(3));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    partida.setMandante(times.get(4));
                    partida.setVisitante(times.get(5));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    partida.setMandante(times.get(6));
                    partida.setVisitante(times.get(7));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    camp.setOitavas(true);
                } else if (camp.getNumTimes() == 16) {
                    partida.setFase("oitavas");
                    partida.setMandante(times.get(0));
                    partida.setVisitante(times.get(1));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    partida.setMandante(times.get(2));
                    partida.setVisitante(times.get(3));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    partida.setMandante(times.get(4));
                    partida.setVisitante(times.get(5));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    partida.setMandante(times.get(6));
                    partida.setVisitante(times.get(7));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    partida.setMandante(times.get(8));
                    partida.setVisitante(times.get(9));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    partida.setMandante(times.get(10));
                    partida.setVisitante(times.get(11));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    partida.setMandante(times.get(12));
                    partida.setVisitante(times.get(13));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    partida.setMandante(times.get(14));
                    partida.setVisitante(times.get(15));
                    partidaDAO.cadastrarPrevia(partida, campKey);
                    camp.setOitavas(true);
                }
                campDAO = new CampeonatoDAO();
                campDAO.passarDeFase(camp, usuarios);
            }
        }
        Toast.makeText(this, R.string.ftc_sucesso, Toast.LENGTH_SHORT).show();
        Intent it = new Intent(TelaCadastroTime.this, TelaCadastroJogador.class);
        it.putExtra("timeKey", key);
        it.putExtra("user", user);
        it.putExtra("campKey", campKey);
        startActivity(it);
        finish();
    }

}
