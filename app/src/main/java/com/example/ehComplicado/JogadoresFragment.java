package com.example.ehComplicado;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.ehComplicado.FirebaseHelper.JogadorHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import model.bean.Jogador;


public class JogadoresFragment extends Fragment {
    private Jogador jogador;
    private String campKey;
    private ValueEventListener listener;
    private DatabaseReference jogadoresReference;


    private void openFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String timeKey = getArguments().getString("timeKey");
        campKey = getArguments().getString("campKey");
        final ListView lstJogadores = view.findViewById(R.id.lst_jogadores);
        jogadoresReference = FirebaseDatabase.getInstance().getReference().child("time-jogadores").child(timeKey);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        JogadorHelper helper = new JogadorHelper(jogadoresReference);
        final List<Jogador> jogadores = helper.retrive();
        final ArrayAdapter<Jogador> adapter = new ArrayAdapter<>(getContext(), R.layout.personalizado_list_item, jogadores);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lstJogadores.setAdapter(adapter);
                lstJogadores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        jogador = jogadores.get(position);
                        TelaEditarJogador t = new TelaEditarJogador();
                        Bundle data = new Bundle();
                        data.putString("jogadorKey",jogador.getId());
                        data.putString("campKey",campKey);
                        data.putString("timeKey",timeKey);
                        t.setArguments(data);
                        openFragment(t);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        jogadoresReference.addListenerForSingleValueEvent(eventListener);
        listener = eventListener;
        Button btnSalvar = view.findViewById(R.id.salvar_button);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelaCadastroJogador t = new TelaCadastroJogador();
                Bundle b = new Bundle();
                b.putString("campKey",campKey);
                b.putString("timeKey",timeKey);
                t.setArguments(b);
                openFragment(t);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_jogadores, container, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (listener != null){
            jogadoresReference.removeEventListener(listener);
        }
    }
}
