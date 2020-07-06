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

public class TelaPendurados extends AppCompatActivity {
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
        setContentView(R.layout.activity_tela_pendurados);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.ftc_pendurados);
        final TableLayout tbPendurados = findViewById(R.id.tabela_pendurados);

        user = getIntent().getParcelableExtra("user");
        campKey = getIntent().getStringExtra("campKey");
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);
        jogadoresReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-jogadores").child(campKey);
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
        timeHelper = new TimeHelper(timeReference);
        final List<Time> times = timeHelper.retrive();
        jogadorHelper = new JogadorHelper(jogadoresReference);
        final List<Jogador> jogadors = jogadorHelper.retrive();
        final JogadorDAO jogadorDAO = new JogadorDAO();
        ValueEventListener mJogadorListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Jogador>pendurados=jogadorDAO.listarPendurados(jogadors,times);
                for (Jogador jogador : pendurados){
                    View tr = getLayoutInflater().inflate(R.layout.tabela_cartoes_linha,null,false);
                    TextView txtNome = tr.findViewById(R.id.nome_jogador);
                    TextView txtTime = tr.findViewById(R.id.time);
                    txtNome.setText(jogador.getApelido());
                    txtTime.setText(jogador.getTime().getNome());
                    tbPendurados.addView(tr,new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
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
        if (jogadorListener != null){
            jogadoresReference.removeEventListener(jogadorListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                criarActivity();
                break;
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
                        Intent it = new Intent(TelaPendurados.this, TelaQuatroGrupos.class);
                        it.putExtra("user",user);
                        it.putExtra("campKey",campKey);
                        startActivity(it);
                        finish();
                } else if (camp.isOitavas()) {
                    Intent it = new Intent(TelaPendurados.this, TelaOitavas.class);
                    it.putExtra("user",user);
                    it.putExtra("campKey",campKey);
                    startActivity(it);
                    finish();
                } else if (camp.isQuartas()) {
                    Intent it = new Intent(TelaPendurados.this, TelaQuartas.class);
                    it.putExtra("user",user);
                    it.putExtra("campKey",campKey);
                    startActivity(it);
                    finish();
                } else if (camp.isSemi()) {
                    Intent it = new Intent(TelaPendurados.this, TelaSemi.class);
                    it.putExtra("user",user);
                    it.putExtra("campKey",campKey);
                    startActivity(it);
                    finish();
                } else if (camp.isFinal()) {
                    Intent it = new Intent(TelaPendurados.this, TelaFinal.class);
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
