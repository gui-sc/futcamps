package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import model.bean.Campeonato;
import model.bean.Usuario;
import model.dao.CampeonatoDAO;

public class TelaRevisao extends Fragment {
    private FirebaseUser user;
    private DatabaseReference database;
    private Campeonato camp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_revisao, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        camp = getArguments().getParcelable("camp");
        database = FirebaseDatabase.getInstance().getReference();
        final TextView lbl1 = view.findViewById(R.id.lbl1);
        final TextView lbl2 = view.findViewById(R.id.lbl2);
        final TextView lbl3 = view.findViewById(R.id.lbl3);
        final TextView lbl4 = view.findViewById(R.id.lbl4);
        final TextView lbl5 = view.findViewById(R.id.lbl5);
        final TextView txt1 = view.findViewById(R.id.txt1);
        final TextView txt2 = view.findViewById(R.id.txt2);
        final TextView txt3 = view.findViewById(R.id.txt3);
        final TextView txt4 = view.findViewById(R.id.txt4);
        final TextView txt5 = view.findViewById(R.id.txt5);
        final TextView lblProx = view.findViewById(R.id.lblprox);
        final TextView lblAnt = view.findViewById(R.id.lblant);
        Button btnAlterar = view.findViewById(R.id.btn_alterar);
        Button btnSalvar = view.findViewById(R.id.btn_salvar);
        txt1.setText(camp.getNome());
        txt2.setText(camp.getCidade());
        txt3.setText(camp.getPremiacao());
        txt4.setText(camp.getFormato());
        txt5.setText(String.valueOf(camp.getNumTimes()));
        lblAnt.setVisibility(View.INVISIBLE);
        lblAnt.setClickable(false);
        lblProx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lbl5.setVisibility(View.INVISIBLE);
                txt5.setVisibility(View.INVISIBLE);
                if(camp.getFormato().equals(getString(R.string.fasedegrupos))){
                    lbl1.setText(R.string.ftc_num_grupos);
                    txt1.setText(String.valueOf(camp.getNumGrupos()));
                    lbl2.setText(R.string.ftc_classi_grupo);
                    txt2.setText(String.valueOf(camp.getClassificados()));
                    lbl3.setText(R.string.amarelos_pra_suspens_o);
                    txt3.setText(String.valueOf(camp.getCartoesPendurado()+1));
                    lbl4.setText(R.string.zerar_nas_fases);
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
                    txt4.setText(string);
                    lblProx.setClickable(false);
                    lblProx.setVisibility(View.INVISIBLE);
                    lblAnt.setVisibility(View.VISIBLE);
                    lblAnt.setClickable(true);
                }
            }
        });
        lblAnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt5.setVisibility(View.VISIBLE);
                lbl5.setVisibility(View.VISIBLE);
                lbl1.setText(R.string.ftc_nome);
                txt1.setText(camp.getNome());
                lbl2.setText(R.string.cidade);
                txt2.setText(camp.getCidade());
                lbl3.setText(R.string.premiacao);
                txt3.setText(camp.getPremiacao());
                lbl4.setText(R.string.formato);
                txt4.setText(camp.getFormato());
                lbl5.setText(R.string.ftc_num_times_abrev);
                txt5.setText(String.valueOf(camp.getNumTimes()));
            }
        });
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
        Toast.makeText(getContext(), R.string.ftc_cadastrando, Toast.LENGTH_SHORT).show();
        final String userId = user.getUid();
        database.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario user = dataSnapshot.getValue(Usuario.class);
                if (user == null) {
                    Toast.makeText(getContext(), R.string.erro, Toast.LENGTH_SHORT).show();
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


    private void criarActivity() {
        Fragment t = new NovoCampFragment();
        Bundle b = new Bundle();
        b.putParcelable("camp",camp);
        t.setArguments(b);
        openFragment(t);
    }

    private void novoCampeonato(Campeonato campeonato) {
        CampeonatoDAO campDAO = new CampeonatoDAO();
        String key = campDAO.inserir(campeonato);
        Toast.makeText(getContext(), R.string.cadastrado, Toast.LENGTH_SHORT).show();
        Fragment homeFragment = HomeFragment.newInstance();
        openFragment(homeFragment);
    }
    public void openFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
