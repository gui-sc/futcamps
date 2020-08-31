package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import model.bean.Jogador;
import model.bean.Time;
import model.dao.JogadorDAO;

public class TelaCadastroJogador extends Fragment {
    private DatabaseReference timeReference,campRef;
    private ValueEventListener timeListener;
    private String timeKey, campKey;
    private Time time;
    private JogadorDAO jogadorDAO;
    private String nome, apelido,dataNasc;
    private TextInputLayout nomeTextInput;
    private TextInputEditText nomeEditText;
    private TextInputLayout apelidoTextInput;
    private TextInputEditText apelidoEditText;
    private TextInputLayout  dataNascTextInput;
    private TextInputEditText  dataNascEditText;
    private TextView lblNomeTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_cadastro_jogador,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        timeKey = getArguments().getString("timeKey");
        campKey = getArguments().getString("campKey");
        timeReference = FirebaseDatabase.getInstance().getReference()
                .child("campeonato-times").child(campKey).child(timeKey);
        campRef = FirebaseDatabase.getInstance().getReference()
                .child("campeonatos").child(campKey);
        jogadorDAO = new JogadorDAO();
        lblNomeTime = view.findViewById(R.id.lbl_time);
        nomeTextInput = view.findViewById(R.id.nome_text_input);
        nomeEditText = view.findViewById(R.id.nome_edit_text);
        apelidoTextInput = view.findViewById(R.id.apelido_text_input);
        apelidoEditText = view.findViewById(R.id.apelido_edit_text);
        dataNascTextInput = view.findViewById(R.id.idade_text_input);
        dataNascEditText = view.findViewById(R.id.idade_edit_text);
        time = new Time();
        ValueEventListener mTimeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                time = dataSnapshot.getValue(Time.class);
                String string = lblNomeTime.getText() + " " + time.getNome();
                lblNomeTime.setText(string);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        timeReference.addValueEventListener(mTimeListener);
        timeListener = mTimeListener;
        Button btnSalvar = view.findViewById(R.id.salvar_button);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nome = nomeEditText.getText().toString();
                apelido = apelidoEditText.getText().toString();
                dataNasc = dataNascEditText.getText().toString();
                if (valida(nome)) {
                    nomeTextInput.setError(getString(R.string.ftc_aviso_vazio));
                } else if (valida(apelido)) {
                    apelidoTextInput.setError(getString(R.string.ftc_aviso_vazio));
                }else if(valida(dataNasc)){
                    dataNascTextInput.setError(getString(R.string.ftc_aviso_vazio));
                } else {
                    cadastro();
                    limpar();
                }
            }
        });
    }
    

    @Override
    public void onStop(){
        super.onStop();
        if (timeListener !=null){
            timeReference.removeEventListener(timeListener);
        }
    }

    private void limpar() {
        nomeEditText.setText("");
        nomeEditText.requestFocus();
        apelidoEditText.setText("");
        dataNascEditText.setText("");
    }

    private boolean valida(String s) {
        return s.equals("");
    }

    private void cadastro() {
        Toast.makeText(getContext(), R.string.ftc_inserindo, Toast.LENGTH_SHORT).show();
        Jogador jogador = new Jogador();
        jogador.setNome(nome);
        jogador.setApelido(apelido);
        jogador.setDataNasc(dataNasc);
        jogadorDAO.inserir(jogador,timeKey,campKey);
        Toast.makeText(getContext(), R.string.ftc_inserido, Toast.LENGTH_SHORT).show();
    }

}
