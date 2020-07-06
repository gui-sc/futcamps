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

public class TelaAdicionarJogador extends AppCompatActivity {
    private DatabaseReference timeReference;
    private ValueEventListener timeListener;
    private String timeKey, campKey;
    FirebaseUser user;
    private JogadorDAO jogadorDAO;
    private Time time;
    private String nome, apelido,dataNasc;
    private TextInputLayout nomeTextInput, apelidoTextInput,  dataNascTextInput;
    private TextInputEditText nomeEditText, apelidoEditText, dataNascEditText;
    private TextView lblTime;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_adicionar_jogador);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.ftc_adicionar_jogador);
        nomeTextInput = findViewById(R.id.nome_text_input);
        nomeEditText = findViewById(R.id.nome_edit_text);
        apelidoTextInput = findViewById(R.id.apelido_text_input);
        apelidoEditText = findViewById(R.id.apelido_edit_text);
        lblTime = findViewById(R.id.lbl_time);
        dataNascTextInput = findViewById(R.id.idade_text_input);
        dataNascEditText = findViewById(R.id.idade_edit_text);

        user = getIntent().getParcelableExtra("user");
        timeKey = getIntent().getStringExtra("timeKey");
        campKey = getIntent().getStringExtra("campKey");
        timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey).child(timeKey);
        jogadorDAO = new JogadorDAO();

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

    }

    @Override
    public void onStop() {
        super.onStop();
        if (timeListener != null) {
            timeReference.removeEventListener(timeListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ftc_menu_salvar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                criarActivity();
                break;
            case R.id.btn_salvar:
                nome = nomeEditText.getText().toString();
                apelido = apelidoEditText.getText().toString();
                dataNasc = dataNascEditText.getText().toString();
                if (valida(nome)) {
                    nomeTextInput.setError(getString(R.string.ftc_aviso_vazio));
                } else {
                    nomeTextInput.setError(null);
                    if (valida(apelido)) {
                        apelidoTextInput.setError(getString(R.string.ftc_aviso_vazio));
                    } else {
                        apelidoTextInput.setError(null);


                            if (valida(dataNasc)) {
                                dataNascTextInput.setError(getString(R.string.ftc_aviso_vazio));
                            } else {
                                dataNascTextInput.setError(null);
                                adiciona();
                                Intent it = new Intent(TelaAdicionarJogador.this, TelaEditarTime.class);
                                it.putExtra("campKey", campKey);
                                it.putExtra("timeKey", timeKey);
                                it.putExtra("user", user);
                                startActivity(it);
                                finish();

                            }
                    }
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        criarActivity();
    }

    private void criarActivity() {
        startActivity(new Intent(TelaAdicionarJogador.this, TelaEditarTime.class).putExtra("user", user)
                .putExtra("timeKey", timeKey).putExtra("campKey", campKey));
        finish();
    }

    public boolean valida(String s) {
        return s.equals("");
    }

    public void adiciona() {
        Toast.makeText(this, R.string.ftc_inserindo, Toast.LENGTH_SHORT).show();
        Jogador jogador = new Jogador();
        jogador.setNome(nome);
        jogador.setApelido(apelido);
        jogador.setDataNasc(dataNasc);
        jogadorDAO.inserir(jogador, timeKey, campKey);
        Toast.makeText(this, R.string.ftc_inserido, Toast.LENGTH_SHORT).show();
    }


}
