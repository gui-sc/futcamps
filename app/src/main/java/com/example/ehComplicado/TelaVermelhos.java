package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ehComplicado.FirebaseHelper.JogadorHelper;
import com.example.ehComplicado.FirebaseHelper.TimeHelper;
import com.example.ehComplicado.FirebaseHelper.UsuarioHelper;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import model.bean.CVermelho;
import model.bean.Campeonato;
import model.bean.Jogador;
import model.bean.Partida;
import model.bean.Resultados;
import model.bean.Time;
import model.bean.Usuario;
import model.dao.CVermelhoDAO;
import model.dao.CampeonatoDAO;
import model.dao.JogadorDAO;
import model.dao.PartidaDAO;
import model.dao.ResultadosDAO;
import model.dao.TimeDAO;

public class TelaVermelhos extends AppCompatActivity {
    FirebaseUser user;
    String campKey;
    DatabaseReference timeReference, partidaReference, jogadorReference, campReference;
    TimeHelper timeHelper;
    List<Usuario> usuarios;
    CampeonatoDAO campDAO;
    UsuarioHelper usuarioHelper;
    JogadorHelper jogadorHelperTime1, jogadorHelperTime2, jogadorHelper;
    private JogadorDAO jogadorDAO;
    private CVermelhoDAO vermelhoDAO;
    List<Time> times;
    ValueEventListener partidaListener,campListener2;
    private Partida partida;
    private ListView lstCv;
    Campeonato camp, campNome;
    TimeDAO timeDAO;
    Resultados resultados;
    ResultadosDAO resultadosDAO;
    PartidaDAO partidaDAO;
    List<Jogador> jogadores1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_vermelhos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.vermelhos);
        lstCv = findViewById(R.id.ftc_lista_cv);
        final ListView lstJogadoresCvMandante = findViewById(R.id.ftc_lista__jogadores_cv_mandante);
        final ListView lstJogadoresCvVisitante = findViewById(R.id.ftc_lista__jogadores_cv_visitante);
        jogadorDAO = new JogadorDAO();
        vermelhoDAO = new CVermelhoDAO();
        timeDAO = new TimeDAO();
        camp = new Campeonato();
        campNome = new Campeonato();
        resultados = new Resultados();
        resultadosDAO = new ResultadosDAO();
        partidaDAO = new PartidaDAO();
        campDAO = new CampeonatoDAO();
        final PartidaDAO partidaDAO = new PartidaDAO();
        user = getIntent().getParcelableExtra("user");
        campKey = getIntent().getStringExtra("campKey");
        partida = getIntent().getParcelableExtra("partida");
        partidaReference = FirebaseDatabase.getInstance().getReference()
                .child("partidas").child(partida.getId());
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);
        timeHelper = new TimeHelper(timeReference);
        times = timeHelper.retrive();
        jogadorReference = FirebaseDatabase.getInstance().getReference()
                .child("time-jogadores").child(partida.getIdMandante());
        jogadorHelperTime1 = new JogadorHelper(jogadorReference);
        jogadorReference = FirebaseDatabase.getInstance().getReference()
                .child("time-jogadores").child(partida.getIdVisitante());
        jogadorHelperTime2 = new JogadorHelper(jogadorReference);
        List<Jogador> primeiraLista1 = jogadorHelperTime1.retrive();
        List<Jogador> primeiraLista2 = jogadorHelperTime2.retrive();
        final TextView lblMandante = findViewById(R.id.lbl_mandante);
        final TextView lblVisitante = findViewById(R.id.lbl_visitante);
        final ArrayAdapter<Jogador> cv = new ArrayAdapter<>(this, R.layout.personalizado_list_item);
        final ArrayAdapter<Jogador> jogadoresGeral = new ArrayAdapter<>(this, R.layout.personalizado_list_item, primeiraLista1);
        final ArrayAdapter<Jogador> jogadorArrayAdapter = new ArrayAdapter<>(this, R.layout.personalizado_list_item, primeiraLista2);

        ValueEventListener mPartidaListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                lstCv.setAdapter(cv);
                lstJogadoresCvMandante.setAdapter(jogadoresGeral);
                lstJogadoresCvVisitante.setAdapter(jogadorArrayAdapter);
                lblMandante.setText(partida.getNomeMandante());
                lblVisitante.setText(partida.getNomeVisitante());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        partidaReference.addListenerForSingleValueEvent(mPartidaListener);
        partidaListener = mPartidaListener;

        lstJogadoresCvMandante.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cv.add(jogadoresGeral.getItem(position));
                lstCv.setAdapter(cv);
            }
        });
        lstJogadoresCvVisitante.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cv.add(jogadorArrayAdapter.getItem(position));
                lstCv.setAdapter(cv);
            }
        });
        lstCv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cv.remove(cv.getItem(position));
                lstCv.setAdapter(cv);
            }
        });
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-seguidores").child(campKey);
        usuarioHelper = new UsuarioHelper(userRef);
        usuarios = usuarioHelper.retrive();
        DatabaseReference jogadorRef = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-jogadores").child(campKey);
        jogadorHelper = new JogadorHelper(jogadorRef);
        jogadores1 = jogadorHelper.retrive();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (partidaListener != null) {
            partidaReference.removeEventListener(partidaListener);
        }
        if(campListener2 != null){
            campReference.removeEventListener(campListener2);
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
                AlertDialog dlg = new AlertDialog.Builder(this).setTitle(R.string.ftc_acao_inexperada).setMessage(R.string.ftc_aviso_acao_inexperada)
                        .setNeutralButton(R.string.ftc_ok, null).show();
            case R.id.btn_salvar:
                for (int i = 0; i < lstCv.getCount(); i++) {
                    CVermelho vermelho = new CVermelho();
                    Jogador jogador = (Jogador) lstCv.getItemAtPosition(i);
                    jogador = jogadorDAO.configurar(times, jogador);
                    vermelho.setJogador(jogador.getApelido());
                    vermelho.setTime(jogador.getTime().getNome());
                    vermelhoDAO.inserir(vermelho, partida.getId());
                    jogadorDAO.suspenso(jogador, jogador.getIdTime(), campKey);
                }
                ValueEventListener mCampListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        camp = dataSnapshot.getValue(Campeonato.class);
                        campNome.setNome(camp.getNome());
                        partida = partidaDAO.configurar(times, partida);
                        partida.setCampeonato(camp);
                        if (partida.getPenaltisMandante() > partida.getPenaltisVisitante()) {
                            if (partida.getCampeonato().isFinal()) {
                                partida.getMandante().setPontos(partida.getMandante().getPontos() + 3);
                                partida.getMandante().setGolsPro(partida.getMandante().getGolsPro() + partida.getPlacarMandante());
                                partida.getVisitante().setGolsPro(partida.getVisitante().getGolsPro() + partida.getPlacarVisitante());
                                partida.getMandante().setGolsContra(partida.getMandante().getGolsContra() + partida.getPlacarVisitante());
                                partida.getVisitante().setGolsContra(partida.getVisitante().getGolsContra() + partida.getPlacarMandante());
                                partida.getMandante().setSaldo(partida.getMandante().getGolsPro() - partida.getMandante().getGolsContra());
                                partida.getVisitante().setSaldo(partida.getVisitante().getGolsPro() - partida.getVisitante().getGolsContra());
                                timeDAO.novaPartida(partida.getMandante(), campKey);
                                timeDAO.novaPartida(partida.getVisitante(), campKey);
                                resultados.setCampeao(partida.getMandante().getNome());
                                resultados.setViceCampeao(partida.getVisitante().getNome());
                                Jogador jogador = jogadorDAO.artilheiro(jogadores1, times);
                                resultados.setArtilheiro(jogador.getApelido());
                                resultados.setGols(jogador.getGols());
                                resultadosDAO.inserir(resultados, campKey);
                                partidaDAO.cadastrarCompleto(partida, campKey, usuarios, campNome.getNome());
                                campDAO.finalizar(partida.getCampeonato(), usuarios, campNome.getNome());
                            } else {
                                partida.getMandante().setPontos(partida.getMandante().getPontos() + 3);
                                partida.getMandante().setGolsPro(partida.getMandante().getGolsPro() + partida.getPlacarMandante());
                                partida.getVisitante().setGolsPro(partida.getVisitante().getGolsPro() + partida.getPlacarVisitante());
                                partida.getMandante().setGolsContra(partida.getMandante().getGolsContra() + partida.getPlacarVisitante());
                                partida.getVisitante().setGolsContra(partida.getVisitante().getGolsContra() + partida.getPlacarMandante());
                                partida.getMandante().setSaldo(partida.getMandante().getGolsPro() - partida.getMandante().getGolsContra());
                                partida.getVisitante().setSaldo(partida.getVisitante().getGolsPro() - partida.getVisitante().getGolsContra());
                                timeDAO.novaPartida(partida.getMandante(), campKey);
                                timeDAO.novaPartida(partida.getVisitante(), campKey);
                                timeDAO.eliminar(partida.getVisitante(), campKey);
                                partidaDAO.cadastrarCompleto(partida, campKey, usuarios, campNome.getNome());

                            }
                        } else if (partida.getPenaltisVisitante() > partida.getPenaltisMandante()) {
                            if (partida.getCampeonato().isFinal()) {
                                partida.getMandante().setPontos(partida.getMandante().getPontos() + 3);
                                partida.getMandante().setGolsPro(partida.getMandante().getGolsPro() + partida.getPlacarMandante());
                                partida.getVisitante().setGolsPro(partida.getVisitante().getGolsPro() + partida.getPlacarVisitante());
                                partida.getMandante().setGolsContra(partida.getMandante().getGolsContra() + partida.getPlacarVisitante());
                                partida.getVisitante().setGolsContra(partida.getVisitante().getGolsContra() + partida.getPlacarMandante());
                                partida.getMandante().setSaldo(partida.getMandante().getGolsPro() - partida.getMandante().getGolsContra());
                                partida.getVisitante().setSaldo(partida.getVisitante().getGolsPro() - partida.getVisitante().getGolsContra());
                                timeDAO.novaPartida(partida.getMandante(), campKey);
                                timeDAO.novaPartida(partida.getVisitante(), campKey);
                                resultados.setCampeao(partida.getVisitante().getNome());
                                resultados.setViceCampeao(partida.getMandante().getNome());
                                Jogador jogador = jogadorDAO.artilheiro(jogadores1, times);
                                resultados.setArtilheiro(jogador.getApelido());
                                resultados.setGols(jogador.getGols());
                                resultadosDAO.inserir(resultados, campKey);
                                partidaDAO.cadastrarCompleto(partida, campKey, usuarios, campNome.getNome());
                                campDAO.finalizar(partida.getCampeonato(), usuarios, campNome.getNome());
                            } else {
                                partida.getMandante().setPontos(partida.getMandante().getPontos() + 3);
                                partida.getMandante().setGolsPro(partida.getMandante().getGolsPro() + partida.getPlacarMandante());
                                partida.getVisitante().setGolsPro(partida.getVisitante().getGolsPro() + partida.getPlacarVisitante());
                                partida.getMandante().setGolsContra(partida.getMandante().getGolsContra() + partida.getPlacarVisitante());
                                partida.getVisitante().setGolsContra(partida.getVisitante().getGolsContra() + partida.getPlacarMandante());
                                partida.getMandante().setSaldo(partida.getMandante().getGolsPro() - partida.getMandante().getGolsContra());
                                partida.getVisitante().setSaldo(partida.getVisitante().getGolsPro() - partida.getVisitante().getGolsContra());
                                timeDAO.novaPartida(partida.getMandante(), campKey);
                                timeDAO.novaPartida(partida.getVisitante(), campKey);
                                timeDAO.eliminar(partida.getMandante(), campKey);
                                partidaDAO.cadastrarCompleto(partida, campKey, usuarios, campNome.getNome());
                            }
                        }
                        if (partida.getPlacarMandante() > partida.getPlacarVisitante()) {
                            if (partida.getCampeonato().isFaseDeGrupos()) {
                                partida.getMandante().setPontos(partida.getMandante().getPontos() + 3);
                                partida.getMandante().setGolsPro(partida.getMandante().getGolsPro() + partida.getPlacarMandante());
                                partida.getVisitante().setGolsPro(partida.getVisitante().getGolsPro() + partida.getPlacarVisitante());
                                partida.getMandante().setGolsContra(partida.getMandante().getGolsContra() + partida.getPlacarVisitante());
                                partida.getVisitante().setGolsContra(partida.getVisitante().getGolsContra() + partida.getPlacarMandante());
                                partida.getMandante().setSaldo(partida.getMandante().getGolsPro() - partida.getMandante().getGolsContra());
                                partida.getVisitante().setSaldo(partida.getVisitante().getGolsPro() - partida.getVisitante().getGolsContra());
                                timeDAO.novaPartida(partida.getMandante(), campKey);
                                timeDAO.novaPartida(partida.getVisitante(), campKey);
                                partidaDAO.cadastrarCompleto(partida, campKey, usuarios, campNome.getNome());
                            } else {
                                if (partida.getCampeonato().isFinal()) {
                                    partida.getMandante().setPontos(partida.getMandante().getPontos() + 3);
                                    partida.getMandante().setGolsPro(partida.getMandante().getGolsPro() + partida.getPlacarMandante());
                                    partida.getVisitante().setGolsPro(partida.getVisitante().getGolsPro() + partida.getPlacarVisitante());
                                    partida.getMandante().setGolsContra(partida.getMandante().getGolsContra() + partida.getPlacarVisitante());
                                    partida.getVisitante().setGolsContra(partida.getVisitante().getGolsContra() + partida.getPlacarMandante());
                                    partida.getMandante().setSaldo(partida.getMandante().getGolsPro() - partida.getMandante().getGolsContra());
                                    partida.getVisitante().setSaldo(partida.getVisitante().getGolsPro() - partida.getVisitante().getGolsContra());
                                    timeDAO.novaPartida(partida.getMandante(), campKey);
                                    timeDAO.novaPartida(partida.getVisitante(), campKey);
                                    resultados.setCampeao(partida.getMandante().getNome());
                                    resultados.setViceCampeao(partida.getVisitante().getNome());
                                    Jogador jogador = jogadorDAO.artilheiro(jogadores1, times);
                                    resultados.setArtilheiro(jogador.getApelido());
                                    resultados.setGols(jogador.getGols());
                                    resultadosDAO.inserir(resultados, campKey);
                                    partidaDAO.cadastrarCompleto(partida, campKey, usuarios, campNome.getNome());
                                    campDAO.finalizar(partida.getCampeonato(), usuarios, campNome.getNome());
                                } else {
                                    partida.getMandante().setPontos(partida.getMandante().getPontos() + 3);
                                    partida.getMandante().setGolsPro(partida.getMandante().getGolsPro() + partida.getPlacarMandante());
                                    partida.getVisitante().setGolsPro(partida.getVisitante().getGolsPro() + partida.getPlacarVisitante());
                                    partida.getMandante().setGolsContra(partida.getMandante().getGolsContra() + partida.getPlacarVisitante());
                                    partida.getVisitante().setGolsContra(partida.getVisitante().getGolsContra() + partida.getPlacarMandante());
                                    partida.getMandante().setSaldo(partida.getMandante().getGolsPro() - partida.getMandante().getGolsContra());
                                    partida.getVisitante().setSaldo(partida.getVisitante().getGolsPro() - partida.getVisitante().getGolsContra());
                                    timeDAO.novaPartida(partida.getMandante(), campKey);
                                    timeDAO.novaPartida(partida.getVisitante(), campKey);
                                    timeDAO.eliminar(partida.getVisitante(), campKey);
                                    partidaDAO.cadastrarCompleto(partida, campKey, usuarios, campNome.getNome());
                                }
                            }
                        } else if (partida.getPlacarVisitante() > partida.getPlacarMandante()) {
                            if (partida.getCampeonato().isFaseDeGrupos()) {
                                partida.getVisitante().setPontos(partida.getVisitante().getPontos() + 3);
                                partida.getMandante().setGolsPro(partida.getMandante().getGolsPro() + partida.getPlacarMandante());
                                partida.getVisitante().setGolsPro(partida.getVisitante().getGolsPro() + partida.getPlacarVisitante());
                                partida.getMandante().setGolsContra(partida.getMandante().getGolsContra() + partida.getPlacarVisitante());
                                partida.getVisitante().setGolsContra(partida.getVisitante().getGolsContra() + partida.getPlacarMandante());
                                partida.getMandante().setSaldo(partida.getMandante().getGolsPro() - partida.getMandante().getGolsContra());
                                partida.getVisitante().setSaldo(partida.getVisitante().getGolsPro() - partida.getVisitante().getGolsContra());
                                timeDAO.novaPartida(partida.getMandante(), campKey);
                                timeDAO.novaPartida(partida.getVisitante(), campKey);
                                partidaDAO.cadastrarCompleto(partida, campKey, usuarios, campNome.getNome());
                            } else {
                                if (partida.getCampeonato().isFinal()) {
                                    partida.getMandante().setPontos(partida.getMandante().getPontos() + 3);
                                    partida.getMandante().setGolsPro(partida.getMandante().getGolsPro() + partida.getPlacarMandante());
                                    partida.getVisitante().setGolsPro(partida.getVisitante().getGolsPro() + partida.getPlacarVisitante());
                                    partida.getMandante().setGolsContra(partida.getMandante().getGolsContra() + partida.getPlacarVisitante());
                                    partida.getVisitante().setGolsContra(partida.getVisitante().getGolsContra() + partida.getPlacarMandante());
                                    partida.getMandante().setSaldo(partida.getMandante().getGolsPro() - partida.getMandante().getGolsContra());
                                    partida.getVisitante().setSaldo(partida.getVisitante().getGolsPro() - partida.getVisitante().getGolsContra());
                                    timeDAO.novaPartida(partida.getMandante(), campKey);
                                    timeDAO.novaPartida(partida.getVisitante(), campKey);
                                    resultados.setCampeao(partida.getVisitante().getNome());
                                    resultados.setViceCampeao(partida.getMandante().getNome());
                                    Jogador jogador = jogadorDAO.artilheiro(jogadores1, times);
                                    resultados.setArtilheiro(jogador.getApelido());
                                    resultados.setGols(jogador.getGols());
                                    resultadosDAO.inserir(resultados, campKey);
                                    partidaDAO.cadastrarCompleto(partida, campKey, usuarios, campNome.getNome());
                                    campDAO.finalizar(partida.getCampeonato(), usuarios, campNome.getNome());
                                } else {
                                    partida.getMandante().setPontos(partida.getMandante().getPontos() + 3);
                                    partida.getMandante().setGolsPro(partida.getMandante().getGolsPro() + partida.getPlacarMandante());
                                    partida.getVisitante().setGolsPro(partida.getVisitante().getGolsPro() + partida.getPlacarVisitante());
                                    partida.getMandante().setGolsContra(partida.getMandante().getGolsContra() + partida.getPlacarVisitante());
                                    partida.getVisitante().setGolsContra(partida.getVisitante().getGolsContra() + partida.getPlacarMandante());
                                    partida.getMandante().setSaldo(partida.getMandante().getGolsPro() - partida.getMandante().getGolsContra());
                                    partida.getVisitante().setSaldo(partida.getVisitante().getGolsPro() - partida.getVisitante().getGolsContra());
                                    timeDAO.novaPartida(partida.getMandante(), campKey);
                                    timeDAO.novaPartida(partida.getVisitante(), campKey);
                                    timeDAO.eliminar(partida.getMandante(), campKey);
                                    partidaDAO.cadastrarCompleto(partida, campKey, usuarios, campNome.getNome());
                                }
                            }
                        } else {
                            if (partida.getCampeonato().isFaseDeGrupos()) {
                                partida.getMandante().setPontos(partida.getMandante().getPontos() + 1);
                                partida.getVisitante().setPontos(partida.getVisitante().getPontos() + 1);
                                partida.getMandante().setGolsPro(partida.getMandante().getGolsPro() + partida.getPlacarMandante());
                                partida.getVisitante().setGolsPro(partida.getVisitante().getGolsPro() + partida.getPlacarVisitante());
                                partida.getMandante().setGolsContra(partida.getMandante().getGolsContra() + partida.getPlacarVisitante());
                                partida.getVisitante().setGolsContra(partida.getVisitante().getGolsContra() + partida.getPlacarMandante());
                                partida.getMandante().setSaldo(partida.getMandante().getGolsPro() - partida.getMandante().getGolsContra());
                                partida.getVisitante().setSaldo(partida.getVisitante().getGolsPro() - partida.getVisitante().getGolsContra());
                                timeDAO.novaPartida(partida.getMandante(), campKey);
                                timeDAO.novaPartida(partida.getVisitante(), campKey);
                                partidaDAO.cadastrarCompleto(partida, campKey, usuarios, campNome.getNome());
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                campReference.addListenerForSingleValueEvent(mCampListener);
                campListener2 = mCampListener;
                Intent it = new Intent(TelaVermelhos.this, TelaJogos.class);
                it.putExtra("user", user);
                it.putExtra("campKey", campKey);
                startActivity(it);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
