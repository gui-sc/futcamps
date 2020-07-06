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
import android.widget.Toast;

import com.example.ehComplicado.FirebaseHelper.JogadorHelper;
import com.example.ehComplicado.FirebaseHelper.TimeHelper;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import model.bean.Campeonato;
import model.bean.Gols;
import model.bean.Jogador;
import model.bean.Partida;
import model.bean.Time;
import model.dao.GolsDAO;
import model.dao.JogadorDAO;
import model.dao.PartidaDAO;

public class TelaGols extends AppCompatActivity {
    FirebaseUser user;
    private Partida partida;
    String campKey;
    private ListView lstGols;
    List<Time> times;
    ValueEventListener partidaListener, campListener2;
    DatabaseReference timeReference, partidaReference, jogadorReference, campReference;
    TimeHelper timeHelper;
    JogadorHelper jogadorHelperTime1, jogadorHelperTime2;
    private GolsDAO golDAO;
    private JogadorDAO jogadorDAO;
    int golsMandante, golsVisitante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gols);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.gols);
        lstGols = findViewById(R.id.ftc_lista_gols);
        final ListView lstJogadoresGolsMandante = findViewById(R.id.ftc_lista__jogadores_gols_mandante);
        final ListView lstJogadoresGolsVisitante = findViewById(R.id.ftc_lista_jogadores_gols_visitante);
        golDAO = new GolsDAO();
        jogadorDAO = new JogadorDAO();
        golsMandante = 0;
        golsVisitante = 0;
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
        final TextView lblMandante = findViewById(R.id.lbl_mandante);
        final TextView lblVisitante = findViewById(R.id.lbl_visitante);
        times = timeHelper.retrive();
        jogadorReference = FirebaseDatabase.getInstance().getReference()
                .child("time-jogadores").child(partida.getIdMandante());
        jogadorHelperTime1 = new JogadorHelper(jogadorReference);
        jogadorReference = FirebaseDatabase.getInstance().getReference()
                .child("time-jogadores").child(partida.getIdVisitante());
        jogadorHelperTime2 = new JogadorHelper(jogadorReference);
        List<Jogador> primeiraLista1 = jogadorHelperTime1.retrive();
        List<Jogador> primeiraLista2 = jogadorHelperTime2.retrive();
        final ArrayAdapter<Jogador> jogadoresGeral = new ArrayAdapter<>(this, R.layout.personalizado_list_item, primeiraLista1);
        final ArrayAdapter<Jogador> jogadorArrayAdapter = new ArrayAdapter<>(this, R.layout.personalizado_list_item, primeiraLista2);
        final ArrayAdapter<Jogador> gols = new ArrayAdapter<>(this, R.layout.personalizado_list_item);
        ValueEventListener mPartidaListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lstGols.setAdapter(gols);
                lstJogadoresGolsMandante.setAdapter(jogadoresGeral);
                lstJogadoresGolsVisitante.setAdapter(jogadorArrayAdapter);
                lblMandante.setText(partida.getNomeMandante());
                lblVisitante.setText(partida.getNomeVisitante());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        partidaReference.addListenerForSingleValueEvent(mPartidaListener);
        partidaListener = mPartidaListener;
        lstJogadoresGolsMandante.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (partida.getPlacarMandante() > golsMandante) {
                    gols.add(jogadoresGeral.getItem(position));
                    lstGols.setAdapter(gols);
                    golsMandante++;
                } else {
                    Toast.makeText(TelaGols.this, "Limite de gols do " + partida.getNomeMandante() + " atingido...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lstJogadoresGolsVisitante.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (partida.getPlacarVisitante() > golsVisitante) {
                    gols.add(jogadorArrayAdapter.getItem(position));
                    lstGols.setAdapter(gols);
                    golsVisitante++;
                } else {
                    Toast.makeText(TelaGols.this, "Limite de gols do " + partida.getNomeVisitante() + " atingido...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lstGols.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Jogador jogador = gols.getItem(position);
                if (jogador.getIdTime().equals(partida.getIdMandante())) {
                    golsMandante--;
                } else if (jogador.getIdTime().equals(partida.getIdVisitante())) {
                    golsVisitante--;
                }
                gols.remove(gols.getItem(position));
                lstGols.setAdapter(gols);
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog dlg = new AlertDialog.Builder(this).setTitle(R.string.ftc_acao_inexperada).setMessage(R.string.ftc_aviso_acao_inexperada)
                .setNeutralButton(R.string.ftc_ok, null).show();
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
                break;
            case R.id.btn_prox:
                if (golsMandante == partida.getPlacarMandante() && golsVisitante == partida.getPlacarVisitante()) {
                    for (int i = 0; i < lstGols.getCount(); i++) {
                        Gols gol = new Gols();
                        Jogador jogador = (Jogador) lstGols.getItemAtPosition(i);
                        jogador = jogadorDAO.configurar(times, jogador);
                        jogador.setGols(jogador.getGols() + 1);
                        gol.setJogador(jogador.getApelido());
                        gol.setTime(jogador.getTime().getNome());
                        golDAO.inserir(gol, partida.getId());
                        jogadorDAO.atualizar(jogador, jogador.getIdTime(), campKey);
                    }
                    Intent it = new Intent(TelaGols.this, TelaCartoes.class);
                    it.putExtra("user", user);
                    it.putExtra("campKey", campKey);
                    it.putExtra("partida", partida);
                    startActivity(it);
                    finish();
                    break;
                } else {
                    Toast.makeText(this, "faltam gols...", Toast.LENGTH_SHORT).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (partidaListener != null) {
            partidaReference.removeEventListener(partidaListener);
        }
        if (campListener2 != null) {
            campReference.removeEventListener(campListener2);
        }
    }
}
