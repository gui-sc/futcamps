package com.example.ehComplicado;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ehComplicado.FirebaseHelper.CampeonatoHelper;
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
import model.bean.Usuario;
import model.dao.CampeonatoDAO;

public class BuscarFragment extends Fragment {
    private FirebaseUser user;
    private CampeonatoDAO campDAO;
    private Campeonato camp;
    private List<Campeonato> listSegue, listGeral,listBusca;
    private ListView lv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_buscar,container,false);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        campDAO = new CampeonatoDAO();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        lv = view.findViewById(R.id.lst_camp);
        user = FirebaseAuth.getInstance().getCurrentUser();
        CampeonatoHelper helperSegue = new CampeonatoHelper(db.getReference("/user-segue/" + user.getUid()));
        CampeonatoHelper helperGeral = new CampeonatoHelper(db.getReference("/campeonatos/"));
        listSegue = helperSegue.retrive();
        listGeral = helperGeral.retrive();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                camp = listBusca.get(position);
                final Bundle data = new Bundle();
                data.putString("campKey", camp.getId());
                if (camp.isFaseDeGrupos()){
                    TelaQuatroGrupos quatroGrupos = new TelaQuatroGrupos();
                    quatroGrupos.setArguments(data);
                    openFragment(quatroGrupos);
                }else if (camp.isOitavas() || camp.isQuartas() || camp.isSemi() || camp.isFinal()) {
                    TelaOitavas t = new TelaOitavas();
                    t.setArguments(data);
                    openFragment(t);
                }else if(camp.isFinalizado()){
                    TelaPremios premios = new TelaPremios();
                    premios.setArguments(data);
                    openFragment(premios);
                }else{
                    Toast.makeText(getContext(), R.string.ftc_aviso_campeonato, Toast.LENGTH_SHORT).show();
                }
            }

        });
        Button btnBuscar = view.findViewById(R.id.btnBuscar);
        final TextInputLayout textInputLayout = view.findViewById(R.id.nome_text_input);
        final TextInputEditText textInputEditText = view.findViewById(R.id.nome_edit_text);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cod = textInputEditText.getText().toString();
                if(textInputEditText.getText().length() == 0){
                    textInputLayout.setError("Digite um código...");
                }else{
                    listBusca = campDAO.pesquisa(listGeral,listSegue,cod,user.getUid());
                   CampeonatosAdapter2 adapter = new CampeonatosAdapter2(getContext(), listBusca, false,user.getUid());
                    lv.setAdapter(adapter);
                }
            }
        });
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static BuscarFragment newInstance(){
        return new BuscarFragment();
    }
    private static class CampeonatosAdapter2 extends BaseAdapter {
        private DatabaseReference userReference;
        private final Context ctx;
        private final List<Campeonato> campeonatos;
        private DatabaseReference campeonatoReference = FirebaseDatabase.getInstance().getReference();
        private boolean usuarioSegue;
        private String uid;
        private Usuario usuario;
        CampeonatosAdapter2(Context ctx, List<Campeonato> campeonatos, boolean usuarioSegue,String uid) {
            this.ctx = ctx;
            this.campeonatos = campeonatos;
            this.usuarioSegue = usuarioSegue;
            this.uid = uid;
        }

        @Override
        public int getCount() {
            return campeonatos.size();
        }

        @Override
        public Object getItem(int position) {
            return campeonatos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Campeonato campeonato = campeonatos.get(position);
            if (convertView == null){
                final LayoutInflater layoutInflater = LayoutInflater.from(ctx);
                convertView = layoutInflater.inflate(R.layout.linearlayout_camp2,null);
            }
            usuario = new Usuario();
            userReference  = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(uid);
            final TextView nomeCamp = convertView.findViewById(R.id.nomeCamp);
            final TextView anoFormatoCamp = convertView.findViewById(R.id.anoFormatoCamp);
            final ImageView star = convertView.findViewById(R.id.star);
            final TextView situacao = convertView.findViewById(R.id.txtSituacao);
            if (campeonato.isIniciado()){
                situacao.setText(R.string.ftc_em_and);
                situacao.setTextColor(Color.GREEN);
            }else if (campeonato.isFinalizado()){
                situacao.setText(R.string.ftc_finalizado);
                situacao.setTextColor(Color.RED);
            }else{
                situacao.setText(R.string.ftc_td_ok);
                situacao.setTextColor(Color.BLUE);
            }
            if (!usuarioSegue){
                star.setImageResource(R.drawable.estrela_vazia);
            }
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ValueEventListener userListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            usuario = dataSnapshot.getValue(Usuario.class);
                            if (usuarioSegue){
                                campeonatoReference.child("user-segue").child(uid).child(campeonato.getId()).removeValue();
                                campeonatoReference.child("campeonato-seguidores").child(campeonato.getId()).child(uid).removeValue();
                                star.setImageResource(R.drawable.estrela_vazia);
                                campeonatos.remove(campeonato);
                            }else{
                                campeonatoReference.child("user-segue").child(uid).child(campeonato.getId()).setValue(campeonato);
                                campeonatoReference.child("campeonato-seguidores").child(campeonato.getId()).child(uid).setValue(usuario);
                                star.setImageResource(R.drawable.estrela_preenchida);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    userReference.addListenerForSingleValueEvent(userListener);
                }
            });
            nomeCamp.setText(campeonato.getNome());
            String string = campeonato.getAno() + " - " + campeonato.getFormato();
            anoFormatoCamp.setText(string);
            return convertView;
        }
    }
}
