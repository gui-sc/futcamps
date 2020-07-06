package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import model.bean.Campeonato;
import model.bean.Jogador;
import model.bean.Time;
import model.dao.JogadorDAO;

public class TelaArtilheiros extends AppCompatActivity {
    DatabaseReference jogadoresReference, campReference, timeReference;
    TimeHelper timeHelper;
    JogadorHelper jogadorHelper;
    ValueEventListener campListener,campListener2,jogadorListener;
    private Campeonato camp;
    String campKey;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_artilheiros);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.ftc_artilheiros);
        final TableLayout tbArtilheiros = findViewById(R.id.tabela_artilheiros);

        user = getIntent().getParcelableExtra("user");
        campKey = getIntent().getStringExtra("campKey");
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        jogadoresReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-jogadores").child(campKey);
        timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);
        camp = new Campeonato();

        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                camp = dataSnapshot.getValue(Campeonato.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addValueEventListener(mCampListener);
        campListener = mCampListener;
        jogadorHelper = new JogadorHelper(jogadoresReference);
        timeHelper = new TimeHelper(timeReference);
        final List<Time> times = timeHelper.retrive();
        final List<Jogador> jogadors = jogadorHelper.retrive();
        final JogadorDAO jogadorDAO = new JogadorDAO();
        ValueEventListener mJogadorListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Jogador> artilheiros = jogadorDAO.listarArtilheiros(jogadors,times);
                for (Jogador jogador : artilheiros) {
                    View tr = getLayoutInflater().inflate(R.layout.tabela_artilheiros_linha, null, false);
                    TextView txtNome = tr.findViewById(R.id.nome_jogador);
                    txtNome.setText(jogador.getApelido());
                    TextView txtTime = tr.findViewById(R.id.time);
                    txtTime.setText(jogador.getTime().getNome());
                    TextView txtGols = tr.findViewById(R.id.gols);
                    txtGols.setText(String.valueOf(jogador.getGols()));
                    tbArtilheiros.addView(tr, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        jogadoresReference.addListenerForSingleValueEvent(mJogadorListener);
        jogadorListener = mJogadorListener;

    }
    @Override
    public void onStop(){
        super.onStop();
        if (campListener != null){
            campReference.removeEventListener(campListener);
        }
        if (campListener2 != null){
            campReference.removeEventListener(campListener2);
        }
        if (jogadorListener != null){
            jogadoresReference.removeEventListener(jogadorListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            criarActivity();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed(){
        criarActivity();
    }
    private void criarActivity(){
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                camp = dataSnapshot.getValue(Campeonato.class);
                if (camp.isFaseDeGrupos()) {
                    Intent it = new Intent(TelaArtilheiros.this, TelaQuatroGrupos.class);
                    it.putExtra("user",user);
                    it.putExtra("campKey",campKey);
                    startActivity(it);
                    finish();
                } else if (camp.isOitavas()) {
                    Intent it = new Intent(TelaArtilheiros.this, TelaOitavas.class);
                    it.putExtra("user",user);
                    it.putExtra("campKey",campKey);
                    startActivity(it);
                    finish();
                } else if (camp.isQuartas()) {
                    Intent it = new Intent(TelaArtilheiros.this, TelaQuartas.class);
                    it.putExtra("user",user);
                    it.putExtra("campKey",campKey);
                    startActivity(it);
                    finish();
                } else if (camp.isSemi()) {
                    Intent it = new Intent(TelaArtilheiros.this, TelaSemi.class);
                    it.putExtra("user",user);
                    it.putExtra("campKey",campKey);
                    startActivity(it);
                    finish();
                } else if (camp.isFinal()) {
                    Intent it = new Intent(TelaArtilheiros.this, TelaFinal.class);
                    it.putExtra("user",user);
                    it.putExtra("campKey",campKey);
                    startActivity(it);
                    finish();
                }else if(camp.isFinalizado()){
                    Intent it = new Intent(TelaArtilheiros.this, TelaPremios.class);
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
        campReference.addValueEventListener(mCampListener);
        campListener2 = mCampListener;
    }

}
