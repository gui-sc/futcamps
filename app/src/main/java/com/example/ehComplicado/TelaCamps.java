package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import model.bean.Campeonato;

public class TelaCamps extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Campeonato camp;
    FirebaseUser user;
    private DatabaseReference campRef;
    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela);
        BottomNavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setOnNavigationItemSelectedListener(this);
        camp = getIntent().getParcelableExtra("camp");
        user = FirebaseAuth.getInstance().getCurrentUser();
        campRef = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(camp.getId());
        TelaEditarCamp editarCamp = new TelaEditarCamp();
        Bundle dataEditar = new Bundle();
        dataEditar.putString("campKey", camp.getId());
        editarCamp.setArguments(dataEditar);
        openFragment(editarCamp);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
        final Bundle data = new Bundle();
        data.putString("campKey", camp.getId());
        switch (menuItem.getItemId()) {
            case R.id.tabela_button:
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (camp.isFinalizado()) {
                            TelaPremios premios = new TelaPremios();
                            premios.setArguments(data);
                            openFragment(premios);
                        } else if (camp.isFaseDeGrupos()) {
                            TelaQuatroGrupos quatroGrupos = new TelaQuatroGrupos();
                            quatroGrupos.setArguments(data);
                            openFragment(quatroGrupos);
                        } else if (camp.isOitavas() || camp.isQuartas() || camp.isSemi() || camp.isFinal()) {
                            TelaOitavas t = new TelaOitavas();
                            t.setArguments(data);
                            openFragment(t);
                        } else {
                            AlertDialog dlg = new AlertDialog.Builder(TelaCamps.this)
                                    .setTitle("Ação Inexperada")
                                    .setMessage("Você não pode entrar nessa aba!\nPor favor conclua o cadastro de times")
                                    .setNeutralButton("OK", null)
                                    .show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                campRef.addListenerForSingleValueEvent(eventListener);
                listener = eventListener;
                break;
            case R.id.times_button:
                TimesFragment timesFragment = new TimesFragment();
                Bundle dataTimes = new Bundle();
                dataTimes.putParcelable("camp", camp);
                timesFragment.setArguments(dataTimes);
                openFragment(timesFragment);
                break;
            case R.id.editar_button:
                TelaEditarCamp editarCamp = new TelaEditarCamp();
                Bundle dataEditar = new Bundle();
                dataEditar.putString("campKey", camp.getId());
                editarCamp.setArguments(dataEditar);
                openFragment(editarCamp);
                break;
            case R.id.voltar_button:
                Intent it = new Intent(TelaCamps.this, TelaCarregarCamp.class);
                startActivity(it);
                finish();
                break;
            case R.id.jogos_button:
                if (camp.isFinalizado()) {
                    Intent ite = new Intent(TelaCamps.this, TelaPrincipalJogos.class);
                    ite.putExtra("camp", camp);
                    startActivity(ite);
                    finish();
                } else if (camp.isFaseDeGrupos()) {
                    Intent ite = new Intent(TelaCamps.this, TelaPrincipalJogos.class);
                    ite.putExtra("camp", camp);
                    startActivity(ite);
                    finish();
                } else if (camp.isOitavas() || camp.isQuartas() || camp.isSemi() || camp.isFinal()) {
                    Intent ite = new Intent(TelaCamps.this, TelaPrincipalJogos.class);
                    ite.putExtra("camp", camp);
                    startActivity(ite);
                    finish();
                } else {
                    AlertDialog dlg = new AlertDialog.Builder(TelaCamps.this)
                            .setTitle("Ação Inexperada")
                            .setMessage("Você não pode entrar nessa aba!\nPor favor conclua o cadastro de times")
                            .setNeutralButton("OK", null)
                            .show();
                }

                break;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) {
            campRef.removeEventListener(listener);
        }
    }
}
