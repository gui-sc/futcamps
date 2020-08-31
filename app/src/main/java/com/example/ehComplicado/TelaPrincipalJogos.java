package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import model.bean.Campeonato;

public class TelaPrincipalJogos extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Campeonato camp;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal_jogos);
        user = FirebaseAuth.getInstance().getCurrentUser();
        camp = getIntent().getParcelableExtra("camp");
        BottomNavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setOnNavigationItemSelectedListener(this);
        Bundle data = new Bundle();
        data.putString("campKey", camp.getId());
        TelaJogos t = new TelaJogos();
        t.setArguments(data);
        openFragment(t);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Bundle data = new Bundle();
        data.putString("campKey", camp.getId());
        switch (menuItem.getItemId()) {
            case R.id.jogos_button:
                TelaJogos jogos = new TelaJogos();
                jogos.setArguments(data);
                openFragment(jogos);
                break;
            case R.id.artilheiros_button:
                TelaArtilheiros artilheiros = new TelaArtilheiros();
                artilheiros.setArguments(data);
                openFragment(artilheiros);
                break;
            case R.id.pendurados_button:
                TelaPendurados pendurados = new TelaPendurados();
                pendurados.setArguments(data);
                openFragment(pendurados);
                break;
            case R.id.suspensos_button:
                TelaSuspensos suspensos = new TelaSuspensos();
                suspensos.setArguments(data);
                openFragment(suspensos);
                break;
            case R.id.voltar_button:
                if (camp.getUid().equals(user.getUid())) {
                    Intent it = new Intent(TelaPrincipalJogos.this, TelaCamps.class);
                    it.putExtra("camp", camp);
                    startActivity(it);
                    finish();
                } else {
                    Intent it = new Intent(TelaPrincipalJogos.this, TelaCarregarCamp.class);
                    it.putExtra("camp", camp);
                    startActivity(it);
                    finish();
                }
                break;
        }
        return true;
    }
}
