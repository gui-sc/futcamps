package com.example.ehComplicado;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class TelaRecuperarSenha extends BaseActivity {
    private EditText email;
    private Button enviaEmail;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_recuperar_senha);
        initComponents();
        setProgressBar(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        enviaEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarEmail();
            }
        });
    }

    private void initComponents() {
        email = findViewById(R.id.email);
        enviaEmail = findViewById(R.id.confirmar);
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
        finish();
    }

    private void enviarEmail() {
        showProgressBar();
        String userEmail = email.getText().toString();
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, R.string.pedidoEmail, Toast.LENGTH_SHORT).show();
        } else {
            auth.sendPasswordResetEmail(userEmail)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        hideProgressBar();
                        androidx.appcompat.app.AlertDialog.Builder dlg = new AlertDialog.Builder(TelaRecuperarSenha.this);
                        dlg.setMessage("Email enviado com sucesso!");
                        dlg.setTitle("Sucesso");
                        dlg.setNeutralButton("OK",null);
                        dlg.show();
                    }else{
                        hideProgressBar();
                        Toast.makeText(TelaRecuperarSenha.this, "Ocorreu um erro...", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
