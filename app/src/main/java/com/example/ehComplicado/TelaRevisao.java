package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import model.bean.Campeonato;
import model.bean.Usuario;
import model.dao.CampeonatoDAO;

public class TelaRevisao extends AppCompatActivity {
    FirebaseUser user;
    DatabaseReference database;
    Campeonato camp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_revisao);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.revisao);
        user = getIntent().getParcelableExtra("user");
        camp = getIntent().getParcelableExtra("camp");
        database = FirebaseDatabase.getInstance().getReference();
        TextView lblNome = findViewById(R.id.lbl_nome);
        TextView lblCidade = findViewById(R.id.lbl_cidade);
        TextView lblPremiacao = findViewById(R.id.lbl_premiacao);
        TextView lblFormato = findViewById(R.id.lbl_formato);
        TextView lblNumTimes = findViewById(R.id.lbl_num_times);
        TextView lblNumGrupos = findViewById(R.id.lbl_num_grupos);
        TextView lblClassificados = findViewById(R.id.lbl_class_grupo);
        TextView lblZerar = findViewById(R.id.lbl_zerar);
        TextView lblPendurado = findViewById(R.id.lbl_pendurado);
        TextView classificados = findViewById(R.id.class_grupo);
        TextView zerar = findViewById(R.id.zerar);
        TextView numGrupos = findViewById(R.id.num_grupos);
        Button btnAlterar = findViewById(R.id.btn_alterar);
        Button btnSalvar = findViewById(R.id.btn_salvar);
        lblNome.setText(camp.getNome());
        lblCidade.setText(camp.getCidade());
        lblNumTimes.setText(String.valueOf(camp.getNumTimes()));
        lblPremiacao.setText(camp.getPremiacao());
        lblFormato.setText(camp.getFormato());
        if(camp.getFormato().equals(getString(R.string.matamata))){
            classificados.setVisibility(View.INVISIBLE);
            zerar.setVisibility(View.INVISIBLE);
            numGrupos.setVisibility(View.INVISIBLE);
        }else {
            lblClassificados.setText(String.valueOf(camp.getClassificados()));
            lblNumGrupos.setText(String.valueOf(camp.getNumGrupos()));
            lblPendurado.setText(String.valueOf(camp.getCartoesPendurado() + 1));
            String string = "";
            if (camp.isZerarCartoesOitavas()) {
                if (camp.isZerarCartoesQuartas()) {
                    if (camp.isZerarCartoesSemi()) {
                        string = getString(R.string.oitavasQuartasSemi);
                    } else {
                        string = getString(R.string.oitavasQuartas);
                    }
                } else {
                    string = getString(R.string.oitavas);
                }
            } else if (camp.isZerarCartoesQuartas()) {
                if (camp.isZerarCartoesSemi()) {
                    string = getString(R.string.quartasSemi);
                } else {
                    string = getString(R.string.Quartas);
                }
            } else if (camp.isZerarCartoesSemi()) {
                string = getString(R.string.Semi);
            }
            lblZerar.setText(string);
        }
        btnAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criarActivity();
            }
        });
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastro();
            }
        });
    }

    private void cadastro() {
        Toast.makeText(this, R.string.ftc_cadastrando, Toast.LENGTH_SHORT).show();
        final String userId = user.getUid();
        database.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario user = dataSnapshot.getValue(Usuario.class);
                if (user == null) {
                    Toast.makeText(TelaRevisao.this, R.string.erro, Toast.LENGTH_SHORT).show();
                } else {
                    camp.setUid(userId);
                    novoCampeonato(camp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==android.R.id.home){
            criarActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void criarActivity() {
        Intent it = new Intent(TelaRevisao.this, TelaNovoCamp.class);
        it.putExtra("user", user);
        it.putExtra("camp", camp);
        startActivity(it);
        finish();
    }

    private void novoCampeonato(Campeonato campeonato) {
        CampeonatoDAO campDAO = new CampeonatoDAO();
        String key = campDAO.inserir(campeonato);
        Toast.makeText(this, R.string.cadastrado, Toast.LENGTH_SHORT).show();
        Intent it = new Intent(TelaRevisao.this, TelaEditarCamp.class);
        it.putExtra("campKey", key);
        it.putExtra("user", user);
        startActivity(it);
        finish();
    }
}
