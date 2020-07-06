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
import android.widget.Toast;

import com.example.ehComplicado.FirebaseHelper.JogadorHelper;
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

import java.util.List;

import model.bean.Campeonato;
import model.bean.Jogador;
import model.bean.Partida;
import model.bean.Resultados;
import model.bean.Time;
import model.bean.Usuario;
import model.dao.CampeonatoDAO;
import model.dao.JogadorDAO;
import model.dao.PartidaDAO;
import model.dao.ResultadosDAO;
import model.dao.TimeDAO;

public class TelaNovaPartida extends AppCompatActivity {
    FirebaseUser user;
    String campKey, partidaKey;
    DatabaseReference timeReference, partidaReference, jogadorReference, campReference;
    TimeHelper timeHelper;
    JogadorHelper jogadorHelperTime1, jogadorHelperTime2, jogadorHelper;
    ValueEventListener partidaListener, campListener, partidaListener2, campListener2;
    Campeonato camp;
    List<Usuario> usuarios;
    UsuarioHelper usuarioHelper;
    private PartidaDAO partidaDAO;
    private Partida partida;
    private JogadorDAO jogadorDAO;
    private TimeDAO timeDAO;
    private ResultadosDAO resultadosDAO;
    private CampeonatoDAO campDAO;
    private Resultados resultados;
    private TextInputLayout estadioTextInput, dataTextInput;
    private TextInputEditText estadioEditText, dataEditText, obsEditText;
    private TextView lblPenaltiMandante, lblPenaltiVisitante;
    Campeonato campNome;
    List<Time> times;
    TextView lblMandante, lblVisitante;
    private String estadio, data, obs;
    List<Jogador> jogadores1;
    private int placarMandante, placarVisitante, penaltisMandante, penaltisVisitante;
    List<Jogador> time1, time2;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_nova_partida);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.ftc_nova_partida);
        estadioTextInput = findViewById(R.id.estadio_text_input);
        estadioEditText = findViewById(R.id.estadio_edit_text);
        dataTextInput = findViewById(R.id.data_text_input);
        dataEditText = findViewById(R.id.data_edit_text);
        lblPenaltiMandante = findViewById(R.id.lbl_penalti_mandante);
        lblPenaltiVisitante = findViewById(R.id.lbl_penalti_visitante);
        final TextView lblPlacarMandante = findViewById(R.id.lbl_placar_mandante);
        final TextView lblPlacarVisitante = findViewById(R.id.lbl_placar_visitante);
        campNome = new Campeonato();
        obsEditText = findViewById(R.id.obs_edit_text);
        lblMandante = findViewById(R.id.lbl_mandante);
        lblVisitante = findViewById(R.id.lbl_visitante);

        partidaDAO = new PartidaDAO();
        jogadorDAO = new JogadorDAO();
        campDAO = new CampeonatoDAO();
        resultadosDAO = new ResultadosDAO();
        timeDAO = new TimeDAO();
        placarMandante = 0;
        placarVisitante = 0;
        penaltisMandante = 0;
        penaltisVisitante = 0;
        lblPenaltiMandante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                penaltisMandante++;
                lblPenaltiMandante.setText(String.valueOf(penaltisMandante));
            }
        });
        lblPenaltiMandante.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                penaltisMandante = 0;
                lblPenaltiMandante.setText(String.valueOf(penaltisMandante));
                return false;
            }
        });
        lblPenaltiVisitante.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                penaltisVisitante = 0;
                lblPenaltiVisitante.setText(String.valueOf(penaltisVisitante));
                return false;
            }
        });
        lblPenaltiVisitante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                penaltisVisitante++;
                lblPenaltiVisitante.setText(String.valueOf(penaltisVisitante));
            }
        });
        lblPlacarMandante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placarMandante++;
                lblPlacarMandante.setText(String.valueOf(placarMandante));
            }
        });
        lblPlacarMandante.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                placarMandante = 0;
                lblPlacarMandante.setText(String.valueOf(placarMandante));
                return false;
            }
        });
        lblPlacarVisitante.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                placarVisitante = 0;
                lblPlacarVisitante.setText(String.valueOf(placarVisitante));
                return false;
            }
        });
        lblPlacarVisitante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placarVisitante++;
                lblPlacarVisitante.setText(String.valueOf(placarVisitante));
            }
        });
        user = getIntent().getParcelableExtra("user");
        campKey = getIntent().getStringExtra("campKey");
        partidaKey = getIntent().getStringExtra("partidaKey");
        partida = getIntent().getParcelableExtra("partida");
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);
        partidaReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-partidas").child(campKey).child(partidaKey);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-seguidores").child(campKey);
        DatabaseReference jogadorRef = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-jogadores").child(campKey);
        usuarioHelper = new UsuarioHelper(userRef);
        usuarios = usuarioHelper.retrive();
        timeHelper = new TimeHelper(timeReference);
        camp = new Campeonato();
        times = timeHelper.retrive();
        ValueEventListener mPartidaListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lblMandante.setText(partida.getNomeMandante());
                lblVisitante.setText(partida.getNomeVisitante());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        partidaReference.addListenerForSingleValueEvent(mPartidaListener);
        partidaListener = mPartidaListener;
        jogadorReference = FirebaseDatabase.getInstance().getReference()
                .child("time-jogadores").child(partida.getIdMandante());
        jogadorHelperTime1 = new JogadorHelper(jogadorReference);
        jogadorReference = FirebaseDatabase.getInstance().getReference()
                .child("time-jogadores").child(partida.getIdVisitante());
        jogadorHelperTime2 = new JogadorHelper(jogadorReference);
        time1 = jogadorHelperTime1.retrive();
        time2 = jogadorHelperTime2.retrive();
        jogadorHelper = new JogadorHelper(jogadorRef);
        resultados = new Resultados();
        jogadores1 = jogadorHelper.retrive();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ftc_menu_prox, menu);
        return true;
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
        if (partidaListener2 != null) {
            partidaReference.removeEventListener(partidaListener2);
        }
        if (campListener2 != null) {
            campReference.removeEventListener(campListener2);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                criarActivity();
                break;
            case R.id.btn_prox:
                ValueEventListener mPartidaListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        partida = dataSnapshot.getValue(Partida.class);
                        partida = partidaDAO.configurar(times, partida);
                        ValueEventListener mCampListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                camp = dataSnapshot.getValue(Campeonato.class);
                                campNome.setNome(camp.getNome());
                                partida.setCampeonato(camp);
                                estadio = String.valueOf(estadioEditText.getText());
                                data = String.valueOf(dataEditText.getText());
                                obs = String.valueOf(obsEditText.getText());
                                if (!valida(estadio)) {
                                    estadioTextInput.setError(getString(R.string.ftc_aviso_vazio));
                                } else {
                                    estadioTextInput.setError(null);
                                    if (!valida(data)) {
                                        dataTextInput.setError(getString(R.string.ftc_aviso_vazio));
                                    } else {
                                        dataTextInput.setError(null);
                                        if (lblPenaltiMandante.getVisibility() == View.VISIBLE) {
                                            cadastroMataMata();
                                        } else {
                                            if (partida.getCampeonato().isFaseDeGrupos()) {
                                                cadastro();
                                            } else {
                                                if (placarMandante == placarVisitante) {
                                                    lblPenaltiMandante.setVisibility(View.VISIBLE);
                                                    lblPenaltiVisitante.setVisibility(View.VISIBLE);
                                                    Toast.makeText(TelaNovaPartida.this, R.string.avisoPenaltis, Toast.LENGTH_SHORT).show();
                                                } else {
                                                    cadastroMataMata();
                                                }
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                partidaReference.addListenerForSingleValueEvent(mPartidaListener);
                partidaListener2 = mPartidaListener;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        criarActivity();
    }

    private void criarActivity() {
        Intent it = new Intent(TelaNovaPartida.this, TelaJogos.class);
        it.putExtra("user", user);
        it.putExtra("campKey", campKey);
        startActivity(it);
        finish();
    }

    private void cadastro() {
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                camp = dataSnapshot.getValue(Campeonato.class);
                List<Jogador> jogadores1 = jogadorDAO.listarSuspensosTime(time1);
                List<Jogador> jogadores2 = jogadorDAO.listarSuspensosTime(time2);
                for (Jogador jogador : jogadores1) {
                    jogadorDAO.cumpriuSuspensao(jogador, partida.getIdMandante(), campKey);
                }
                for (Jogador jogador : jogadores2) {
                    jogadorDAO.cumpriuSuspensao(jogador, partida.getIdVisitante(), campKey);
                }
                partida.setNomeMandante(String.valueOf(lblMandante.getText()));
                partida.setNomeVisitante(String.valueOf(lblVisitante.getText()));
                partida.setLocal(estadio);
                partida.setData(data);
                partida.setPlacarMandante(placarMandante);
                partida.setPlacarVisitante(placarVisitante);
                partida.setObservacoes(obs);
                partida.setCadastrada(true);
                Toast.makeText(TelaNovaPartida.this, R.string.partidaCadastrada, Toast.LENGTH_SHORT).show();
                if (!camp.isIniciado()) {
                    campDAO.iniciar(camp, usuarios, campNome.getNome());
                }
                if ((placarMandante + placarVisitante) == 0) {
                    Intent it = new Intent(TelaNovaPartida.this, TelaCartoes.class);
                    it.putExtra("campKey", campKey);
                    it.putExtra("user", user);
                    it.putExtra("partida", partida);
                    startActivity(it);
                    finish();
                } else {
                    Intent it = new Intent(TelaNovaPartida.this, TelaGols.class);
                    it.putExtra("campKey", campKey);
                    it.putExtra("user", user);
                    it.putExtra("partida", partida);
                    startActivity(it);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(mCampListener);
        campListener = mCampListener;


    }

    private void cadastroMataMata() {
        List<Jogador> jogadores1 = jogadorDAO.listarSuspensosTime(time1);
        List<Jogador> jogadores2 = jogadorDAO.listarSuspensosTime(time2);
        for (Jogador jogador : jogadores1) {
            jogadorDAO.cumpriuSuspensao(jogador, partida.getIdMandante(), campKey);
        }
        for (Jogador jogador : jogadores2) {
            jogadorDAO.cumpriuSuspensao(jogador, partida.getIdVisitante(), campKey);
        }
        partida.setLocal(estadio);
        partida.setData(data);
        partida.setPlacarMandante(placarMandante);
        partida.setPlacarVisitante(placarVisitante);
        partida.setPenaltisMandante(penaltisMandante);
        partida.setPenaltisVisitante(penaltisVisitante);
        partida.setObservacoes(obs);
        partida.setCadastrada(true);
        if (!camp.isIniciado()) {
            campDAO.iniciar(camp, usuarios, campNome.getNome());
        }
        Toast.makeText(this, R.string.partidaCadastrada, Toast.LENGTH_SHORT).show();
        if ((placarMandante + placarVisitante) == 0) {
            Intent it = new Intent(TelaNovaPartida.this, TelaCartoes.class);
            it.putExtra("campKey", campKey);
            it.putExtra("user", user);
            it.putExtra("partida", partida);
            startActivity(it);
            finish();
        } else {
            Intent it = new Intent(TelaNovaPartida.this, TelaGols.class);
            it.putExtra("campKey", campKey);
            it.putExtra("user", user);
            it.putExtra("partida", partida);
            startActivity(it);
            finish();
        }

    }

    private boolean valida(String s) {
        return !s.equals("");
    }

}
