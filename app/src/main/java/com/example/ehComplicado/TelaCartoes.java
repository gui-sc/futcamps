package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.ehComplicado.FirebaseHelper.JogadorHelper;
import com.example.ehComplicado.FirebaseHelper.TimeHelper;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import model.bean.CAmarelo;
import model.bean.Campeonato;
import model.bean.Jogador;
import model.bean.Partida;
import model.bean.Time;
import model.dao.CAmareloDAO;
import model.dao.JogadorDAO;
import model.dao.PartidaDAO;

public class TelaCartoes extends AppCompatActivity {
    FirebaseUser user;
    String campKey;
    DatabaseReference timeReference, partidaReference, jogadorReference, campReference;
    TimeHelper timeHelper;
    JogadorHelper jogadorHelperTime1, jogadorHelperTime2;
    private JogadorDAO jogadorDAO;
    private CAmareloDAO amareloDAO;
    private ListView lstCa;
    List<Time> times;
    ValueEventListener partidaListener,campListener;
    private Partida partida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cartoes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.ftc_amarelo);
        lstCa = findViewById(R.id.ftc_lista_ca);
        final ListView lstJogadoresCaMandante = findViewById(R.id.ftc_lista__jogadores_ca_mandante);
        final ListView lstJogadoresCaVisitante = findViewById(R.id.ftc_lista__jogadores_ca_visitante);
        jogadorDAO = new JogadorDAO();
        amareloDAO = new CAmareloDAO();
        final PartidaDAO partidaDAO = new PartidaDAO();
        user = getIntent().getParcelableExtra("user");
        campKey = getIntent().getStringExtra("campKey");
        partida = getIntent().getParcelableExtra("partida");
        partidaReference = FirebaseDatabase.getInstance().getReference()
                .child("partidas").child(partida.getId());
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);
        timeHelper = new TimeHelper(timeReference);
        times = timeHelper.retrive();
        final TextView lblMandante = findViewById(R.id.lbl_mandante);
        final TextView lblVisitante = findViewById(R.id.lbl_visitante);
        jogadorReference = FirebaseDatabase.getInstance().getReference()
                .child("time-jogadores").child(partida.getIdMandante());
        jogadorHelperTime1 = new JogadorHelper(jogadorReference);
        jogadorReference = FirebaseDatabase.getInstance().getReference()
                .child("time-jogadores").child(partida.getIdVisitante());
        jogadorHelperTime2 = new JogadorHelper(jogadorReference);
        final ArrayAdapter<Jogador> ca = new ArrayAdapter<>(this, R.layout.personalizado_list_item);
        lstCa.setAdapter(ca);
        List<Jogador> primeiraLista1 = jogadorHelperTime1.retrive();
        List<Jogador> primeiraLista2 = jogadorHelperTime2.retrive();
        final ArrayAdapter<Jogador> jogadoresGeral = new ArrayAdapter<>(this, R.layout.personalizado_list_item, primeiraLista1);
        final ArrayAdapter<Jogador> jogadorArrayAdapter = new ArrayAdapter<>(this,R.layout.personalizado_list_item,primeiraLista2);
        ValueEventListener mPartidaListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lstJogadoresCaMandante.setAdapter(jogadoresGeral);
                lstJogadoresCaVisitante.setAdapter(jogadorArrayAdapter);
                lblMandante.setText(partida.getNomeMandante());
                lblVisitante.setText(partida.getNomeVisitante());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        partidaReference.addListenerForSingleValueEvent(mPartidaListener);
        partidaListener = mPartidaListener;
        lstJogadoresCaMandante.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ca.add(jogadoresGeral.getItem(position));
                lstCa.setAdapter(ca);
            }
        });
        lstJogadoresCaVisitante.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ca.add(jogadorArrayAdapter.getItem(position));
                lstCa.setAdapter(ca);
            }
        });
        lstCa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ca.remove(ca.getItem(position));
                lstCa.setAdapter(ca);
            }
        });
    }
    @Override
    public void onStop(){
        super.onStop();
        if (partidaListener != null) {
            partidaReference.removeEventListener(partidaListener);
        }
        if (campListener != null) {
            campReference.removeEventListener(campListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ftc_menu_prox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog dlg = new AlertDialog.Builder(this).setTitle(R.string.ftc_acao_inexperada).setMessage(R.string.ftc_aviso_acao_inexperada)
                        .setNeutralButton(R.string.ftc_ok, null).show();
            case R.id.btn_prox:
                ValueEventListener mCampListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                        for (int i = 0; i < lstCa.getCount(); i++) {
                            CAmarelo amarelo = new CAmarelo();
                            Jogador jogador = (Jogador) lstCa.getItemAtPosition(i);
                            jogador = jogadorDAO.configurar(times,jogador);
                            amarelo.setJogador(jogador.getApelido());
                            amarelo.setTime(jogador.getTime().getNome());
                            amareloDAO.inserir(amarelo, partida.getId());
                            if (jogador.isPendurado()) {
                                jogador.setSuspenso(true);
                                jogador.setPendurado(false);
                            } else if ((jogador.getCa()+1)==camp.getCartoesPendurado()){
                                jogador.setPendurado(true);
                            }else{
                                jogador.setCa(jogador.getCa()+1);
                            }
                            jogadorDAO.atualizar(jogador, jogador.getIdTime(),campKey);
                        }

                        Intent it = new Intent(TelaCartoes.this,TelaVermelhos.class);
                        it.putExtra("user",user);
                        it.putExtra("campKey",campKey);
                        it.putExtra("partida",partida);
                        startActivity(it);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                campReference.addListenerForSingleValueEvent(mCampListener);
                campListener = mCampListener;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        AlertDialog dlg = new AlertDialog.Builder(this).setTitle(R.string.ftc_acao_inexperada).setMessage(R.string.ftc_aviso_acao_inexperada)
                .setNeutralButton(R.string.ftc_ok,null).show();
    }
}
