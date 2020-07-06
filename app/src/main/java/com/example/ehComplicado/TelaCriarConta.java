package com.example.ehComplicado;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import model.bean.Usuario;

public class TelaCriarConta extends BaseActivity {

    private EditText email, senha, usuario;
    private TextView jaTemConta;
    private Button criarConta;
    private FirebaseAuth firabaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_criar_conta);
        initComponents();
        setProgressBar(R.id.progressBar);
        firabaseAuth = FirebaseAuth.getInstance();
        jaTemConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(TelaCriarConta.this, MainActivity.class);
                startActivity(it);
                finish();
            }
        });
        criarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criarNovaConta();
            }
        });
    }


    private void initComponents() {
        email = findViewById(R.id.email);
        senha = findViewById(R.id.password);
        usuario = findViewById(R.id.usuario);
        jaTemConta = findViewById(R.id.temConta);
        criarConta = findViewById(R.id.criarConta);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    private void criarNovaConta() {
        final String userEmail = email.getText().toString();
        String userSenha = senha.getText().toString();
        showProgressBar();
        final String userName = usuario.getText().toString();
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, R.string.pedidoEmail, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userSenha)) {
            Toast.makeText(this, R.string.pedidoSenha, Toast.LENGTH_SHORT).show();
        } else {
            firabaseAuth.createUserWithEmailAndPassword(userEmail, userSenha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        AuthResult result = task.getResult();
                        FirebaseUser user = result.getUser();
                        Usuario usuario = new Usuario();
                        usuario.setId(user.getUid());
                        usuario.setNome(userName);
                        usuario.setEmail(userEmail);
                        usuario.setToken("");
                        usuario.saveDB();
                        hideProgressBar();
                        Intent it = new Intent(TelaCriarConta.this, MainActivity.class);
                        startActivity(it);
                        finish();
                    } else {
                        Toast.makeText(TelaCriarConta.this, "Ocorreu um erro...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
