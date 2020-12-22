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

public class TelaEditarJogador extends Fragment {
	private DatabaseReference jogadorReference, timeReference;
	private String campKey;
	private String timeKey;
	private FirebaseUser user;
	private ValueEventListener jogadorListener, timeListener,jogadorListener2;
	private Jogador jogador;
	private Time time;
	private String nome, apelido;
	private TextInputLayout nomeTextInput, apelidoTextInput;
	private TextInputEditText nomeEditText;
	private String jogadorKey;
	private TextInputEditText apelidoEditText;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_tela_editar_jogador, container, false);
	}

	public void openFragment(Fragment fragment){
		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		jogadorKey = getArguments().getString("jogadorKey");
		campKey = getArguments().getString("campKey");
		timeKey = getArguments().getString("timeKey");
		user = FirebaseAuth.getInstance().getCurrentUser();
		Button btnSalvar = view.findViewById(R.id.salvar_button);
		Button btnDeletar = view.findViewById(R.id.deletar_button);
		btnSalvar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nome = nomeEditText.getText().toString();
				apelido = apelidoEditText.getText().toString();
				if (!valida(nome)) {
					nomeTextInput.setError(getString(R.string.ftc_aviso_vazio));
				} else {
					nomeTextInput.setError(null);
					if (!valida(apelido)) {
						apelidoTextInput.setError(getString(R.string.ftc_aviso_vazio));
					} else {
						apelidoTextInput.setError(null);
						altera();
						JogadoresFragment fragment = new JogadoresFragment();
						Bundle data = new Bundle();
						data.putString("timeKey",timeKey);
						data.putString("campKey",campKey);
						fragment.setArguments(data);
						openFragment(fragment);
					}
				}
			}
		});
		btnDeletar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ValueEventListener eventListener = new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						jogador = dataSnapshot.getValue(Jogador.class);
						JogadorDAO jogadorDAO = new JogadorDAO();
						jogadorDAO.excluir(jogador.getId(),timeKey,campKey);
						JogadoresFragment jogadoresFragment = new JogadoresFragment();
						Bundle data = new Bundle();
						data.putString("campKey",campKey);
						data.putString("timeKey",timeKey);
						jogadoresFragment.setArguments(data);
						openFragment(jogadoresFragment);
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {

					}
				};
				jogadorReference.addListenerForSingleValueEvent(eventListener);
				jogadorListener2 = eventListener;
			}
		});
		timeReference = FirebaseDatabase.getInstance().getReference().child("times").child(timeKey);
		jogadorReference = FirebaseDatabase.getInstance().getReference().child("jogadores").child(jogadorKey);
		nomeTextInput = view.findViewById(R.id.nome_text_input);
		nomeEditText = view.findViewById(R.id.nome_edit_text);
		apelidoTextInput = view.findViewById(R.id.apelido_text_input);
		apelidoEditText = view.findViewById(R.id.apelido_edit_text);
		final TextInputEditText dataNascEditText = view.findViewById(R.id.idade_edit_text);
		final TextView lblTime = view.findViewById(R.id.lbl_time);

		jogador = new Jogador();
		time = new Time();
		ValueEventListener mTimeListener = new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				time = dataSnapshot.getValue(Time.class);
				String string = lblTime.getText() + " " + time.getNome();
				lblTime.setText(string);

			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		};
		timeReference.addValueEventListener(mTimeListener);
		timeListener = mTimeListener;

		ValueEventListener mJogadorListener = new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				jogador = dataSnapshot.getValue(Jogador.class);
				nomeEditText.setText(jogador.getNome());
				apelidoEditText.setText(jogador.getApelido());
				dataNascEditText.setText(jogador.getDataNasc());
				dataNascEditText.setEnabled(false);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		};
		jogadorReference.addListenerForSingleValueEvent(mJogadorListener);
		jogadorListener = mJogadorListener;

	}

	@Override
	public void onStop() {
		super.onStop();
		if (jogadorListener != null) {
			jogadorReference.removeEventListener(jogadorListener);
		}
		if (timeListener != null) {
			timeReference.removeEventListener(timeListener);
		}
		if (jogadorListener2 != null){
			jogadorReference.removeEventListener(jogadorListener2);
		}
	}

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                criarActivity();
                break;
            case R.id.btn_ok:
                nome = nomeEditText.getText().toString();
                apelido = apelidoEditText.getText().toString();
                if (!valida(nome)) {
                    nomeTextInput.setError(getString(R.string.ftc_aviso_vazio));
                } else {
                    nomeTextInput.setError(null);
                    if (!valida(apelido)) {
                        apelidoTextInput.setError(getString(R.string.ftc_aviso_vazio));
                    } else {
                        apelidoTextInput.setError(null);
                        altera();
                        Intent it = new Intent(TelaEditarJogador.this, TelaEditarTime.class);
                        it.putExtra("user",user);
                        it.putExtra("timeKey",timeKey);
                        it.putExtra("campKey",campKey);
                        startActivity(it);
                        finish();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

	private void altera() {
		jogador.setNome(nome);
		jogador.setApelido(apelido);
		jogador.setId(jogadorKey);
		JogadorDAO jogadorDAO = new JogadorDAO();
		jogadorDAO.alterar(jogador, timeKey, campKey);
		Toast.makeText(getContext(), R.string.ftc_jogador_alterado, Toast.LENGTH_SHORT).show();
	}

	private boolean valida(String s) {
		return !s.equals("");
	}

}
