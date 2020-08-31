package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ehComplicado.FirebaseHelper.JogadorHelper;
import com.example.ehComplicado.FirebaseHelper.TimeHelper;
import com.example.ehComplicado.FirebaseHelper.UsuarioHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import model.bean.Campeonato;
import model.bean.Jogador;
import model.bean.Partida;
import model.bean.Resultados;
import model.bean.Time;
import model.bean.Usuario;
import model.dao.CampeonatoDAO;
import model.dao.JogadorDAO;
import model.dao.PartidaDAO;
import model.dao.ResultadosDAO;
import model.dao.TimeDAO;

public class TelaNovaPartida extends Fragment {
    private String campKey;
    private DatabaseReference partidaReference;
    private DatabaseReference campReference;
    private ValueEventListener partidaListener, campListener, partidaListener2, campListener2;
    private Campeonato camp;
    private List<Usuario> usuarios;
    private Partida partida;
    private JogadorDAO jogadorDAO;
    private CampeonatoDAO campDAO;
    private TextInputLayout estadioTextInput, dataTextInput;
    private TextInputEditText estadioEditText, dataEditText, obsEditText;
    private TextView lblPenaltiMandante, lblPenaltiVisitante;
    private Campeonato campNome;
    private List<Time> times;
    private TextView lblMandante, lblVisitante;
    private String estadio, data, obs;
    private int placarMandante, placarVisitante, penaltisMandante, penaltisVisitante;
    private List<Jogador> time1, time2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_nova_partida, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        estadioTextInput = view.findViewById(R.id.estadio_text_input);
        estadioEditText = view.findViewById(R.id.estadio_edit_text);
        dataTextInput = view.findViewById(R.id.data_text_input);
        dataEditText = view.findViewById(R.id.data_edit_text);
        lblPenaltiMandante = view.findViewById(R.id.lbl_penalti_mandante);
        lblPenaltiVisitante = view.findViewById(R.id.lbl_penalti_visitante);
        final TextView lblPlacarMandante = view.findViewById(R.id.lbl_placar_mandante);
        final TextView lblPlacarVisitante = view.findViewById(R.id.lbl_placar_visitante);
        campNome = new Campeonato();
        obsEditText = view.findViewById(R.id.obs_edit_text);
        lblMandante = view.findViewById(R.id.lbl_mandante);
        lblVisitante = view.findViewById(R.id.lbl_visitante);

        final PartidaDAO partidaDAO = new PartidaDAO();
        jogadorDAO = new JogadorDAO();
        campDAO = new CampeonatoDAO();
        ResultadosDAO resultadosDAO = new ResultadosDAO();
        TimeDAO timeDAO = new TimeDAO();
        placarMandante = 0;
        placarVisitante = 0;
        penaltisMandante = 0;
        penaltisVisitante = 0;
        lblPenaltiMandante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                penaltisMandante++;
                lblPenaltiMandante.setText(String.valueOf(penaltisMandante));
            }
        });
        lblPenaltiMandante.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                penaltisMandante = 0;
                lblPenaltiMandante.setText(String.valueOf(penaltisMandante));
                return false;
            }
        });
        lblPenaltiVisitante.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                penaltisVisitante = 0;
                lblPenaltiVisitante.setText(String.valueOf(penaltisVisitante));
                return false;
            }
        });
        lblPenaltiVisitante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                penaltisVisitante++;
                lblPenaltiVisitante.setText(String.valueOf(penaltisVisitante));
            }
        });
        lblPlacarMandante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placarMandante++;
                lblPlacarMandante.setText(String.valueOf(placarMandante));
            }
        });
        lblPlacarMandante.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                placarMandante = 0;
                lblPlacarMandante.setText(String.valueOf(placarMandante));
                return false;
            }
        });
        lblPlacarVisitante.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                placarVisitante = 0;
                lblPlacarVisitante.setText(String.valueOf(placarVisitante));
                return false;
            }
        });
        lblPlacarVisitante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placarVisitante++;
                lblPlacarVisitante.setText(String.valueOf(placarVisitante));
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        campKey = getArguments().getString("campKey");
        String partidaKey = getArguments().getString("partidaKey");
        partida = getArguments().getParcelable("partida");
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        DatabaseReference timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);
        partidaReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-partidas").child(campKey).child(partidaKey);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-seguidores").child(campKey);
        DatabaseReference jogadorRef = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-jogadores").child(campKey);
        UsuarioHelper usuarioHelper = new UsuarioHelper(userRef);
        usuarios = usuarioHelper.retrive();
        TimeHelper timeHelper = new TimeHelper(timeReference);
        camp = new Campeonato();
        times = timeHelper.retrive();
        ValueEventListener mPartidaListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lblMandante.setText(partida.getNomeMandante());
                lblVisitante.setText(partida.getNomeVisitante());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        partidaReference.addListenerForSingleValueEvent(mPartidaListener);
        partidaListener = mPartidaListener;
        DatabaseReference jogadorReference = FirebaseDatabase.getInstance().getReference()
                .child("time-jogadores").child(partida.getIdMandante());
        JogadorHelper jogadorHelperTime1 = new JogadorHelper(jogadorReference);
        jogadorReference = FirebaseDatabase.getInstance().getReference()
                .child("time-jogadores").child(partida.getIdVisitante());
        JogadorHelper jogadorHelperTime2 = new JogadorHelper(jogadorReference);
        time1 = jogadorHelperTime1.retrive();
        time2 = jogadorHelperTime2.retrive();
        JogadorHelper jogadorHelper = new JogadorHelper(jogadorRef);
        Resultados resultados = new Resultados();
        List<Jogador> jogadores1 = jogadorHelper.retrive();
        Button btnSalvar = view.findViewById(R.id.salvar_button);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueEventListener mPartidaListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        partida = dataSnapshot.getValue(Partida.class);
                        partida = partidaDAO.configurar(times, partida);
                        ValueEventListener mCampListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                camp = dataSnapshot.getValue(Campeonato.class);
                                campNome.setNome(camp.getNome());
                                partida.setCampeonato(camp);
                                estadio = String.valueOf(estadioEditText.getText());
                                data = String.valueOf(dataEditText.getText());
                                obs = String.valueOf(obsEditText.getText());
                                if (!valida(estadio)) {
                                    estadioTextInput.setError(getString(R.string.ftc_aviso_vazio));
                                } else {
                                    estadioTextInput.setError(null);
                                    if (!valida(data)) {
                                        dataTextInput.setError(getString(R.string.ftc_aviso_vazio));
                                    } else {
                                        dataTextInput.setError(null);
                                        if (lblPenaltiMandante.getVisibility() == View.VISIBLE) {
                                            cadastroMataMata();
                                        } else {
                                            if (partida.getCampeonato().isFaseDeGrupos()) {
                                                cadastro();
                                            } else {
                                                if (placarMandante == placarVisitante) {
                                                    lblPenaltiMandante.setVisibility(View.VISIBLE);
                                                    lblPenaltiVisitante.setVisibility(View.VISIBLE);
                                                    Toast.makeText(getContext(), R.string.avisoPenaltis, Toast.LENGTH_SHORT).show();
                                                } else {
                                                    cadastroMataMata();
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        };
                        campReference.addListenerForSingleValueEvent(mCampListener);
                        campListener2 = mCampListener;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                partidaReference.addListenerForSingleValueEvent(mPartidaListener);
                partidaListener2 = mPartidaListener;
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        if (partidaListener != null) {
            partidaReference.removeEventListener(partidaListener);
        }
        if (campListener != null) {
            campReference.removeEventListener(campListener);
        }
        if (partidaListener2 != null) {
            partidaReference.removeEventListener(partidaListener2);
        }
        if (campListener2 != null) {
            campReference.removeEventListener(campListener2);
        }
    }

    private void cadastro() {
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                camp = dataSnapshot.getValue(Campeonato.class);
                List<Jogador> jogadores1 = jogadorDAO.listarSuspensosTime(time1);
                List<Jogador> jogadores2 = jogadorDAO.listarSuspensosTime(time2);
                for (Jogador jogador : jogadores1) {
                    jogadorDAO.cumpriuSuspensao(jogador, partida.getIdMandante(), campKey);
                }
                for (Jogador jogador : jogadores2) {
                    jogadorDAO.cumpriuSuspensao(jogador, partida.getIdVisitante(), campKey);
                }
                partida.setNomeMandante(String.valueOf(lblMandante.getText()));
                partida.setNomeVisitante(String.valueOf(lblVisitante.getText()));
                partida.setLocal(estadio);
                partida.setData(data);
                partida.setPlacarMandante(placarMandante);
                partida.setPlacarVisitante(placarVisitante);
                partida.setObservacoes(obs);
                partida.setCadastrada(true);
                Toast.makeText(getContext(), R.string.partidaCadastrada, Toast.LENGTH_SHORT).show();
                if (!camp.isIniciado()) {
                    campDAO.iniciar(camp, usuarios, campNome.getNome());
                }
                if ((placarMandante + placarVisitante) == 0) {
                    TelaCartoes t = new TelaCartoes();
                    Bundle data = new Bundle();
                    data.putString("campKey",campKey);
                    data.putParcelable("partida",partida);
                    t.setArguments(data);
                    openFragment(t);
                } else {
                    TelaGols t = new TelaGols();
                    Bundle data = new Bundle();
                    data.putString("campKey",campKey);
                    data.putParcelable("partida",partida);
                    t.setArguments(data);
                    openFragment(t);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(mCampListener);
        campListener = mCampListener;
    }

    public void openFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void cadastroMataMata() {
        List<Jogador> jogadores1 = jogadorDAO.listarSuspensosTime(time1);
        List<Jogador> jogadores2 = jogadorDAO.listarSuspensosTime(time2);
        for (Jogador jogador : jogadores1) {
            jogadorDAO.cumpriuSuspensao(jogador, partida.getIdMandante(), campKey);
        }
        for (Jogador jogador : jogadores2) {
            jogadorDAO.cumpriuSuspensao(jogador, partida.getIdVisitante(), campKey);
        }
        partida.setLocal(estadio);
        partida.setData(data);
        partida.setPlacarMandante(placarMandante);
        partida.setPlacarVisitante(placarVisitante);
        partida.setPenaltisMandante(penaltisMandante);
        partida.setPenaltisVisitante(penaltisVisitante);
        partida.setObservacoes(obs);
        partida.setCadastrada(true);
        if (!camp.isIniciado()) {
            campDAO.iniciar(camp, usuarios, campNome.getNome());
        }
        Toast.makeText(getContext(), R.string.partidaCadastrada, Toast.LENGTH_SHORT).show();
        if ((placarMandante + placarVisitante) == 0) {
            TelaCartoes t = new TelaCartoes();
            Bundle data = new Bundle();
            data.putString("campKey",campKey);
            data.putParcelable("partida",partida);
            t.setArguments(data);
            openFragment(t);
        } else {
            TelaGols t = new TelaGols();
            Bundle data = new Bundle();
            data.putString("campKey",campKey);
            data.putParcelable("partida",partida);
            t.setArguments(data);
            openFragment(t);
        }

    }

    private boolean valida(String s) {
        return !s.equals("");
    }

}
