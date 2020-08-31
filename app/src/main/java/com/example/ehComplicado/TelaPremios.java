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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import model.bean.Resultados;

public class TelaPremios extends Fragment {
    TextView lblCampeao, lblVice, lblArtilheiro, lblGols;
    String campKey;
    FirebaseUser user;
    Resultados resultados;
    ValueEventListener resultadoListener, campListener;
    DatabaseReference resultadoReference, campReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_premios, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        campKey = getArguments().getString("campKey");
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        resultadoReference = FirebaseDatabase.getInstance().getReference()
                .child("resultado-campeonatos").child(campKey);
        lblCampeao = view.findViewById(R.id.lbl_campeao);
        lblVice = view.findViewById(R.id.lbl_vice_campeao);
        lblArtilheiro = view.findViewById(R.id.lbl_artilheiro);
        lblGols = view.findViewById(R.id.lbl_gols);
        resultados = new Resultados();
        ValueEventListener mResultadoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                resultados = dataSnapshot.getValue(Resultados.class);
                lblCampeao.setText(resultados.getCampeao());
                lblVice.setText(resultados.getViceCampeao());
                lblArtilheiro.setText(resultados.getArtilheiro());
                lblGols.setText(String.valueOf(resultados.getGols()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        resultadoReference.addValueEventListener(mResultadoListener);
        resultadoListener = mResultadoListener;
        Button btnChave = view.findViewById(R.id.chaveamento_button);
        btnChave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelaOitavas oitavas = new TelaOitavas();
                Bundle data = new Bundle();
                data.putString("campKey", campKey);
                oitavas.setArguments(data);
                openFragment(oitavas);
            }
        });
        Button btnJogos = view.findViewById(R.id.jogos_button);
        btnJogos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelaJogos t = new TelaJogos();
                Bundle data = new Bundle();
                data.putString("campKey",campKey);
                t.setArguments(data);
                openFragment(t);
            }
        });
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (resultadoListener != null) {
            resultadoReference.removeEventListener(resultadoListener);
        }
        if (campListener != null) {
            campReference.removeEventListener(campListener);
        }
    }
}