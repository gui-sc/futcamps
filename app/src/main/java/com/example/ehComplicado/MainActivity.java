package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

public class MainActivity extends BaseActivity{
    private FirebaseAuth mFirebaseAuth;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private Button btnEntrar, btnNovaConta;
    private TextView esqueceuSenha;
    private EditText email,senha;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        mFirebaseAuth = FirebaseAuth.getInstance();
        setProgressBar(R.id.progressBar);
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            Intent it = new Intent(MainActivity.this, TelaCarregarCamp.class);
            it.putExtra("user", user);
            startActivity(it);
            finish();
        }
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entrar();
            }
        });
        btnNovaConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this,TelaCriarConta.class);
                startActivity(it);
                finish();
            }
        });
        esqueceuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,TelaRecuperarSenha.class));
                finish();
            }
        });

    }
    private void initComponents(){
        btnEntrar = findViewById(R.id.entrar);
        btnNovaConta = findViewById(R.id.novaConta);
        esqueceuSenha = findViewById(R.id.senhaEsquecida);
        email = findViewById(R.id.email);
        senha = findViewById(R.id.password);
    }
    private void entrar(){
        showProgressBar();
        String userEmail = email.getText().toString();
        String userSenha = senha.getText().toString();
        mFirebaseAuth.signInWithEmailAndPassword(userEmail,userSenha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            Intent it = new Intent(MainActivity.this,TelaCarregarCamp.class);
                            it.putExtra("user",user);
                            startActivity(it);
                            hideProgressBar();
                            finish();
                        }else{
                            hideProgressBar();
                            Toast.makeText(MainActivity.this, "Ocorreu um erro...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
