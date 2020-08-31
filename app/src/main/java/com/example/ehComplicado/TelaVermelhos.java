package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ehComplicado.FirebaseHelper.JogadorHelper;
import com.example.ehComplicado.FirebaseHelper.TimeHelper;
import com.example.ehComplicado.FirebaseHelper.UsuarioHelper;
import com.google.firebase.auth.FirebaseAuth;
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

public class TelaVermelhos extends Fragment {
    private FirebaseUser user;
    private String campKey;
    private DatabaseReference partidaReference;
    private DatabaseReference campReference;
    private List<Usuario> usuarios;
    private CampeonatoDAO campDAO;
    private JogadorDAO jogadorDAO;
    private CVermelhoDAO vermelhoDAO;
    private List<Time> times;
    private ValueEventListener partidaListener, campListener2;
    private Partida partida;
    private ListView lstCv;
    private Campeonato camp, campNome;
    private TimeDAO timeDAO;
    private Resultados resultados;
    private ResultadosDAO resultadosDAO;
    private PartidaDAO partidaDAO;
    private List<Jogador> jogadores1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_vermelhos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lstCv = view.findViewById(R.id.ftc_lista_cv);
        final ListView lstJogadoresCvMandante = view.findViewById(R.id.ftc_lista__jogadores_cv_mandante);
        final ListView lstJogadoresCvVisitante = view.findViewById(R.id.ftc_lista__jogadores_cv_visitante);
        jogadorDAO = new JogadorDAO();
        vermelhoDAO = new CVermelhoDAO();
        timeDAO = new TimeDAO();
        camp = new Campeonato();
        campNome = new Campeonato();
        resultados = new Resultados();
        resultadosDAO = new ResultadosDAO();
        partidaDAO = new PartidaDAO();
        campDAO = new CampeonatoDAO();
        user = FirebaseAuth.getInstance().getCurrentUser();
        campKey = getArguments().getString("campKey");
        partida = getArguments().getParcelable("partida");
        partidaReference = FirebaseDatabase.getInstance().getReference()
                .child("partidas").child(partida.getId());
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        DatabaseReference timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);
        TimeHelper timeHelper = new TimeHelper(timeReference);
        times = timeHelper.retrive();
        DatabaseReference jogadorReference = FirebaseDatabase.getInstance().getReference()
                .child("time-jogadores").child(partida.getIdMandante());
        JogadorHelper jogadorHelperTime1 = new JogadorHelper(jogadorReference);
        jogadorReference = FirebaseDatabase.getInstance().getReference()
                .child("time-jogadores").child(partida.getIdVisitante());
        JogadorHelper jogadorHelperTime2 = new JogadorHelper(jogadorReference);
        List<Jogador> primeiraLista1 = jogadorHelperTime1.retrive();
        List<Jogador> primeiraLista2 = jogadorHelperTime2.retrive();
        final TextView lblMandante = view.findViewById(R.id.lbl_mandante);
        final TextView lblVisitante = view.findViewById(R.id.lbl_visitante);
        final ArrayAdapter<Jogador> cv = new ArrayAdapter<>(getContext(), R.layout.personalizado_list_item);
        final ArrayAdapter<Jogador> jogadoresGeral = new ArrayAdapter<>(getContext(), R.layout.personalizado_list_item, primeiraLista1);
        final ArrayAdapter<Jogador> jogadorArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.personalizado_list_item, primeiraLista2);

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
        UsuarioHelper usuarioHelper = new UsuarioHelper(userRef);
        usuarios = usuarioHelper.retrive();
        DatabaseReference jogadorRef = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-jogadores").child(campKey);
        JogadorHelper jogadorHelper = new JogadorHelper(jogadorRef);
        jogadores1 = jogadorHelper.retrive();
        Button btnSalvar = view.findViewById(R.id.salvar_button);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                timeDAO.novaPartida(partida.getMandante(), campKey);
                                timeDAO.novaPartida(partida.getVisitante(), campKey);
                                timeDAO.eliminar(partida.getVisitante(), campKey);
                                partidaDAO.cadastrarCompleto(partida, campKey, usuarios, campNome.getNome());

                            }
                        } else if (partida.getPenaltisVisitante() > partida.getPenaltisMandante()) {
                            if (partida.getCampeonato().isFinal()) {
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
                        TelaJogos t = new TelaJogos();
                        Bundle data = new Bundle();
                        data.putString("campKey", campKey);
                        data.putParcelable("partida", partida);
                        t.setArguments(data);
                        openFragment(t);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                campReference.addListenerForSingleValueEvent(mCampListener);
                campListener2 = mCampListener;


            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        if (partidaListener != null) {
            partidaReference.removeEventListener(partidaListener);
        }
        if (campListener2 != null) {
            campReference.removeEventListener(campListener2);
        }
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
