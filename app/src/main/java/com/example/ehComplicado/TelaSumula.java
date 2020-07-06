package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.ehComplicado.FirebaseHelper.AmareloHelper;
import com.example.ehComplicado.FirebaseHelper.GolHelper;
import com.example.ehComplicado.FirebaseHelper.VermelhoHelper;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import model.bean.CAmarelo;
import model.bean.CVermelho;
import model.bean.Gols;
import model.bean.Partida;


public class TelaSumula extends AppCompatActivity {
    DatabaseReference partidaReference, golReference, amareloReference, vermelhoReference;
    ValueEventListener partidaListener;
    GolHelper golHelper;
    AmareloHelper amareloHelper;
    VermelhoHelper vermelhoHelper;
    private Partida partida;
    FirebaseUser user;
    String campKey,partidaKey;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_sumula);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.ftc_sumula);
        final TextView lblEstadio = findViewById(R.id.lbl_estadio);
        final TextView lblData = findViewById(R.id.lbl_data);
        final TextView lblMandante = findViewById(R.id.lbl_mandante);
        final TextView lblVisitante = findViewById(R.id.lbl_visitante);
        final TextView lblPlacarMandante = findViewById(R.id.lbl_placar_mandante);
        final TextView lblPlacarVisitante = findViewById(R.id.lbl_placar_visitante);
        final TextView lblPenaltiMandante = findViewById(R.id.lbl_penaltis_mandante);
        final TextView lblPenaltiVisitante = findViewById(R.id.lbl_penaltis_visitante);
        final ListView lstGols = findViewById(R.id.ftc_lista_gols);
        final ListView lstCa = findViewById(R.id.ftc_lista_ca);
        final ListView lstCv = findViewById(R.id.ftc_lista_cv);
        final TextView lblObs = findViewById(R.id.lbl_obs);

        user = getIntent().getParcelableExtra("user");
        campKey = getIntent().getStringExtra("campKey");
        partidaKey = getIntent().getStringExtra("partidaKey");
        partida = getIntent().getParcelableExtra("partida");
        partidaReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-partidas").child(campKey).child(partidaKey);
        amareloReference = FirebaseDatabase.getInstance().getReference()
                .child("partida-amarelos").child(partidaKey);
        vermelhoReference = FirebaseDatabase.getInstance().getReference()
                .child("partida-vermelhos").child(partidaKey);
        golReference = FirebaseDatabase.getInstance().getReference()
                .child("partida-gols").child(partidaKey);
        amareloHelper = new AmareloHelper(amareloReference);
        vermelhoHelper = new VermelhoHelper(vermelhoReference);
        golHelper = new GolHelper(golReference);
        final List<Gols>gols = golHelper.retrive();
        final List<CAmarelo>amarelos = amareloHelper.retrive();
        final List<CVermelho>vermelhos = vermelhoHelper.retrive();
        ValueEventListener mPartidaListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lblEstadio.setText(partida.getLocal());
                lblData.setText(partida.getData());
                lblMandante.setText(partida.getNomeMandante());
                lblPlacarMandante.setText(String.valueOf(partida.getPlacarMandante()));
                lblPlacarVisitante.setText(String.valueOf(partida.getPlacarVisitante()));
                lblVisitante.setText(partida.getNomeVisitante());
                String string = lblObs.getText()+" "+partida.getObservacoes();
                lblObs.setText(string);
                if (partida.getPenaltisMandante()!=0 && partida.getPenaltisVisitante()!=0){
                    lblPenaltiMandante.setVisibility(View.VISIBLE);
                    lblPenaltiVisitante.setVisibility(View.VISIBLE);
                    lblPenaltiMandante.setText(String.valueOf(partida.getPenaltisMandante()));
                    lblPenaltiVisitante.setText(String.valueOf(partida.getPenaltisVisitante()));
                }
                ArrayAdapter<Gols>golsAdapter = new ArrayAdapter<>(TelaSumula.this,R.layout.personalizado_list_item,gols);
                ArrayAdapter<CAmarelo>amareloAdapter = new ArrayAdapter<>(TelaSumula.this,R.layout.personalizado_list_item,amarelos);
                ArrayAdapter<CVermelho>vermelhoAdapter = new ArrayAdapter<>(TelaSumula.this,R.layout.personalizado_list_item,vermelhos);
                lstGols.setAdapter(golsAdapter);
                lstCa.setAdapter(amareloAdapter);
                lstCv.setAdapter(vermelhoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        partidaReference.addValueEventListener(mPartidaListener);
        partidaListener = mPartidaListener;

    }

    @Override
    public void onStop(){
        super.onStop();
        if (partidaListener != null){
            partidaReference.removeEventListener(partidaListener);
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
        Intent it = new Intent(TelaSumula.this,TelaJogos.class);
        it.putExtra("user",user);
        it.putExtra("campKey",campKey);
        startActivity(it);
        finish();
    }

}
