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
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import model.bean.Jogador;
import model.bean.Time;
import model.dao.JogadorDAO;

public class TelaEditarJogador extends AppCompatActivity {
    private DatabaseReference jogadorReference,timeReference;
    private String campKey, timeKey,jogadorKey;
    FirebaseUser user;
    private ValueEventListener jogadorListener,timeListener;
    private JogadorDAO jogadorDAO;
    private Jogador jogador;
    private Time time;
    private String nome, apelido;
    private TextInputLayout nomeTextInput, apelidoTextInput;
    private TextInputEditText nomeEditText;
    private TextInputEditText apelidoEditText;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_editar_jogador);
        Toolbar toolbar  =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.ftc_editar_jogador);
        jogadorKey = getIntent().getStringExtra("jogadorKey");
        campKey = getIntent().getStringExtra("campKey");
        timeKey = getIntent().getStringExtra("timeKey");
        user = getIntent().getParcelableExtra("user");
        timeReference = FirebaseDatabase.getInstance().getReference().child("times").child(timeKey);
        jogadorReference = FirebaseDatabase.getInstance().getReference().child("jogadores").child(jogadorKey);
        nomeTextInput = findViewById(R.id.nome_text_input);
        nomeEditText = findViewById(R.id.nome_edit_text);
        apelidoTextInput = findViewById(R.id.apelido_text_input);
        apelidoEditText = findViewById(R.id.apelido_edit_text);
        final TextInputEditText dataNascEditText = findViewById(R.id.idade_edit_text);
        final TextView lblTime = findViewById(R.id.lbl_time);
        jogador = new Jogador();

        time = new Time();
        ValueEventListener mTimeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                time = dataSnapshot.getValue(Time.class);
                String string = lblTime.getText() + " " + time.getNome();
                lblTime.setText(string);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        timeReference.addValueEventListener(mTimeListener);
        timeListener = mTimeListener;

        ValueEventListener mJogadorListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jogador = dataSnapshot.getValue(Jogador.class);
                nomeEditText.setText(jogador.getNome());
                apelidoEditText.setText(jogador.getApelido());
                dataNascEditText.setText(jogador.getDataNasc());
                dataNascEditText.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        jogadorReference.addListenerForSingleValueEvent(mJogadorListener);
        jogadorListener = mJogadorListener;

        
    }

    @Override
    public void onStop(){
        super.onStop();
        if (jogadorListener != null){
            jogadorReference.removeEventListener(jogadorListener);
        }
        if (timeListener != null){
            timeReference.removeEventListener(timeListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.ftc_menu_jogador,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                criarActivity();
                break;
            case R.id.btn_ok:
                nome = nomeEditText.getText().toString();
                apelido = apelidoEditText.getText().toString();
                if (!valida(nome)) {
                    nomeTextInput.setError(getString(R.string.ftc_aviso_vazio));
                } else {
                    nomeTextInput.setError(null);
                    if (!valida(apelido)) {
                        apelidoTextInput.setError(getString(R.string.ftc_aviso_vazio));
                    } else {
                        apelidoTextInput.setError(null);
                        altera();
                        Intent it = new Intent(TelaEditarJogador.this, TelaEditarTime.class);
                        it.putExtra("user",user);
                        it.putExtra("timeKey",timeKey);
                        it.putExtra("campKey",campKey);
                        startActivity(it);
                        finish();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed(){
        criarActivity();
    }
    private void criarActivity(){
        Intent it = new Intent(TelaEditarJogador.this, TelaEditarTime.class);
        it.putExtra("user",user);
        it.putExtra("timeKey",timeKey);
        it.putExtra("campKey",campKey);
        startActivity(it);
        finish();
    }

    private void altera() {
        jogador.setNome(nome);
        jogador.setApelido(apelido);
        jogadorDAO = new JogadorDAO();
        jogadorDAO.alterar(jogador, timeKey,campKey);
        Toast.makeText(this, R.string.ftc_jogador_alterado, Toast.LENGTH_SHORT).show();
    }

    private boolean valida(String s) {
        return !s.equals("");
    }

}
