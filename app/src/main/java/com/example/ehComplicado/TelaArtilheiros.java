package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.ehComplicado.FirebaseHelper.JogadorHelper;
import com.example.ehComplicado.FirebaseHelper.TimeHelper;
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
import model.bean.Time;
import model.dao.JogadorDAO;

public class TelaArtilheiros extends Fragment {
    private DatabaseReference jogadoresReference;
    private DatabaseReference campReference;
    private ValueEventListener campListener, jogadorListener;
private Campeonato camp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_artilheiros,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TableLayout tbArtilheiros = view.findViewById(R.id.tabela_artilheiros);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String campKey = getArguments().getString("campKey");
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        jogadoresReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-jogadores").child(campKey);
        DatabaseReference timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey);
        camp = new Campeonato();

        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                camp = dataSnapshot.getValue(Campeonato.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addValueEventListener(mCampListener);
        campListener = mCampListener;
        JogadorHelper jogadorHelper = new JogadorHelper(jogadoresReference);
        TimeHelper timeHelper = new TimeHelper(timeReference);
        final List<Time> times = timeHelper.retrive();
        final List<Jogador> jogadors = jogadorHelper.retrive();
        final JogadorDAO jogadorDAO = new JogadorDAO();
        ValueEventListener mJogadorListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Jogador> artilheiros = jogadorDAO.listarArtilheiros(jogadors, times);
                for (Jogador jogador : artilheiros) {
                    View tr = getLayoutInflater().inflate(R.layout.tabela_artilheiros_linha, null, false);
                    TextView txtNome = tr.findViewById(R.id.nome_jogador);
                    txtNome.setText(jogador.getApelido());
                    TextView txtTime = tr.findViewById(R.id.time);
                    txtTime.setText(jogador.getTime().getNome());
                    TextView txtGols = tr.findViewById(R.id.gols);
                    txtGols.setText(String.valueOf(jogador.getGols()));
                    tbArtilheiros.addView(tr, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        jogadoresReference.addListenerForSingleValueEvent(mJogadorListener);
        jogadorListener = mJogadorListener;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (campListener != null) {
            campReference.removeEventListener(campListener);
        }
        if (jogadorListener != null) {
            jogadoresReference.removeEventListener(jogadorListener);
        }
    }

   /* private void criarActivity() {
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                camp = dataSnapshot.getValue(Campeonato.class);
                if (camp.isFaseDeGrupos()) {
                    Intent it = new Intent(TelaArtilheiros.this, TelaQuatroGrupos.class);
                    it.putExtra("user", user);
                    it.putExtra("campKey", campKey);
                    startActivity(it);
                    finish();
                } else if (camp.isOitavas()) {
                    Intent it = new Intent(TelaArtilheiros.this, TelaOitavas.class);
                    it.putExtra("user", user);
                    it.putExtra("campKey", campKey);
                    startActivity(it);
                    finish();
                } else if (camp.isQuartas()) {
                    Intent it = new Intent(TelaArtilheiros.this, TelaQuartas.class);
                    it.putExtra("user", user);
                    it.putExtra("campKey", campKey);
                    startActivity(it);
                    finish();
                } else if (camp.isSemi()) {
                    Intent it = new Intent(TelaArtilheiros.this, TelaSemi.class);
                    it.putExtra("user", user);
                    it.putExtra("campKey", campKey);
                    startActivity(it);
                    finish();
                } else if (camp.isFinal()) {
                    Intent it = new Intent(TelaArtilheiros.this, TelaFinal.class);
                    it.putExtra("user", user);
                    it.putExtra("campKey", campKey);
                    startActivity(it);
                    finish();
                } else if (camp.isFinalizado()) {
                    Intent it = new Intent(TelaArtilheiros.this, TelaPremios.class);
                    it.putExtra("user", user);
                    it.putExtra("campKey", campKey);
                    startActivity(it);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addValueEventListener(mCampListener);
        campListener2 = mCampListener;
    }*/

}
