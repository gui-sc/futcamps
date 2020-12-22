package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import model.bean.Campeonato;

public class TelaPrincipalTimes extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener{
    private Campeonato camp;
    private String timeKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal_times);
        BottomNavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setOnNavigationItemSelectedListener(this);
        camp = getIntent().getParcelableExtra("camp");
        timeKey = getIntent().getStringExtra("timeKey");
        TelaEditarTime editarTime = new TelaEditarTime();
        Bundle dataEditar = new Bundle();
        dataEditar.putString("campKey",camp.getId());
        dataEditar.putString("timeKey",timeKey);
        editarTime.setArguments(dataEditar);
        openFragment(editarTime);

    }
    public void openFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.editar_button:
                TelaEditarTime editarTime = new TelaEditarTime();
                Bundle dataEditar = new Bundle();
                dataEditar.putString("campKey",camp.getId());
                dataEditar.putString("timeKey",timeKey);
                editarTime.setArguments(dataEditar);
                openFragment(editarTime);
                break;
            case R.id.jogadores_button:
                JogadoresFragment fragment = new JogadoresFragment();
                Bundle data = new Bundle();
                data.putString("timeKey",timeKey);
                data.putString("campKey",camp.getId());
                fragment.setArguments(data);
                openFragment(fragment);
                break;
            case R.id.voltar_button:
                Intent it = new Intent(this,TelaCamps.class);
                it.putExtra("camp",camp);
                startActivity(it);
                finish();
                break;
        }
        return true;
    }
}
