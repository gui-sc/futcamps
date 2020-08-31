package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ehComplicado.FirebaseHelper.CampeonatoHelper;
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

public class TelaSeguindo extends Fragment {
    private DatabaseReference campReference;
    private FirebaseUser user;
    private Campeonato camp;
    private CampeonatosAdapter2 adapter;
    private ValueEventListener campListener;
    private List<Campeonato> list;
    private ListView lv;

    public static TelaSeguindo newInstance(){
        return new TelaSeguindo();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_seguindo,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lv = view.findViewById(R.id.lst_camp);
        user = FirebaseAuth.getInstance().getCurrentUser();
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("user-segue").child(user.getUid());
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter = new CampeonatosAdapter2(getContext(),list,true,user.getUid());
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(mCampListener);
        campListener = mCampListener;
        CampeonatoHelper helperSegue = new CampeonatoHelper(campReference);
        list= helperSegue.retrive();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                camp = list.get(position);
                Bundle data = new Bundle();
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
                    Toast.makeText(getContext(), R.string.avisoCampeonato, Toast.LENGTH_SHORT).show();
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
    @Override
    public void onStop(){
        super.onStop();
        if (campListener != null){
            campReference.removeEventListener(campListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                criarActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void criarActivity() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
        dlg.setMessage(R.string.ftc_perg_sessao).setPositiveButton(R.string.ftc_sim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        }).setNeutralButton(R.string.ftc_cancelar, null);
        dlg.show();
    }

    private static class CampeonatosAdapter2 extends BaseAdapter {
        private DatabaseReference userReference;
        private final Context ctx;
        private final List<Campeonato> campeonatos;
        private DatabaseReference campeonatoReference = FirebaseDatabase.getInstance().getReference();
        private boolean usuarioSegue;
        private String uid;
        private Usuario usuario;
        CampeonatosAdapter2(Context ctx, List<Campeonato> campeonatos,boolean usuarioSegue,String uid) {
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
            }else if(campeonato.isFaseDeGrupos() || campeonato.isOitavas() || campeonato.isQuartas() || campeonato.isSemi() || campeonato.isFinal()){
                situacao.setText(R.string.ftc_td_ok);
                situacao.setTextColor(Color.BLUE);
            }else{
                situacao.setText(R.string.ftc_nao_iniciado);
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
