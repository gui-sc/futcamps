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

import model.bean.Campeonato;
import model.bean.Jogador;
import model.bean.Time;
import model.dao.JogadorDAO;

public class TelaCadastroJogador extends AppCompatActivity {
    private DatabaseReference timeReference,campRef;
    private ValueEventListener timeListener,campListener;
    private String timeKey, campKey;
    private Time time;
    JogadorDAO jogadorDAO;
    private String nome, apelido,dataNasc;
    private TextInputLayout nomeTextInput;
    private TextInputEditText nomeEditText;
    private TextInputLayout apelidoTextInput;
    private TextInputEditText apelidoEditText;
    private TextInputLayout  dataNascTextInput;
    private TextInputEditText  dataNascEditText;
    private TextView lblNomeTime;
    FirebaseUser user;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_jogador);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHideOnContentScrollEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.ftc_cadastro_jogador);
        user = getIntent().getParcelableExtra("user");
        timeKey = getIntent().getStringExtra("timeKey");
        campKey = getIntent().getStringExtra("campKey");
        timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey).child(timeKey);
        campRef = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        jogadorDAO = new JogadorDAO();
        lblNomeTime = findViewById(R.id.lbl_time);
        nomeTextInput = findViewById(R.id.nome_text_input);
        nomeEditText = findViewById(R.id.nome_edit_text);
        apelidoTextInput = findViewById(R.id.apelido_text_input);
        apelidoEditText = findViewById(R.id.apelido_edit_text);
        dataNascTextInput = findViewById(R.id.idade_text_input);
        dataNascEditText = findViewById(R.id.idade_edit_text);
        time = new Time();
        ValueEventListener mTimeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                time = dataSnapshot.getValue(Time.class);
                String string = lblNomeTime.getText() + " " + time.getNome();
                lblNomeTime.setText(string);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        timeReference.addValueEventListener(mTimeListener);
        timeListener = mTimeListener;

    }

    @Override
    public void onStop(){
        super.onStop();
        if (timeListener !=null){
            timeReference.removeEventListener(timeListener);
        }
        if (campListener != null){
            campRef.removeEventListener(campListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.ftc_menu_salvar,menu);
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
                } else if (valida(apelido)) {
                    apelidoTextInput.setError(getString(R.string.ftc_aviso_vazio));
                }else if(valida(dataNasc)){
                    dataNascTextInput.setError(getString(R.string.ftc_aviso_vazio));
                } else {
                    cadastro();
                    limpar();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        criarActivity();
    }

    private void criarActivity() {
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                if (camp.isFaseDeGrupos() || camp.isOitavas() || camp.isQuartas()){
                    Intent it = new Intent(TelaCadastroJogador.this,TelaEditarCamp.class);
                    it.putExtra("campKey",campKey);
                    it.putExtra("user",user);
                    startActivity(it);
                    finish();
                }else{
                    Intent it = new Intent(TelaCadastroJogador.this, TelaCadastroTime.class);
                    it.putExtra("campKey", campKey);
                    it.putExtra("user",user);
                    startActivity(it);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campRef.addListenerForSingleValueEvent(mCampListener);
        campListener = mCampListener;
    }

    private void limpar() {
        nomeEditText.setText("");
        nomeEditText.requestFocus();
        apelidoEditText.setText("");
        dataNascEditText.setText("");
    }

    private boolean valida(String s) {
        return s.equals("");
    }

    private void cadastro() {
        Toast.makeText(this, R.string.ftc_inserindo, Toast.LENGTH_SHORT).show();
        Jogador jogador = new Jogador();
        jogador.setNome(nome);
        jogador.setApelido(apelido);
        jogador.setDataNasc(dataNasc);
        jogadorDAO.inserir(jogador,timeKey,campKey);
        Toast.makeText(this, R.string.ftc_inserido, Toast.LENGTH_SHORT).show();
    }

}
