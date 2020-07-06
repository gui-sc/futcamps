package com.example.ehComplicado;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseUser;

public class Tela extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela);
        FirebaseUser user = getIntent().getParcelableExtra("user");
        String key = getIntent().getStringExtra("key");
        String campKey = getIntent().getStringExtra("campKey");
        if ("jogos".equals(key)) {
            startActivity(new Intent(Tela.this, TelaJogos.class).putExtra("user", user).putExtra("campKey", campKey));
            finish();
        }
    }
}
