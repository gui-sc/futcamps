package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import com.example.ehComplicado.FirebaseHelper.JogadorHelper;
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
import model.bean.Time;
import model.bean.Usuario;
import model.dao.CampeonatoDAO;
import model.dao.TimeDAO;

public class TelaEditarTime extends Fragment {
    private DatabaseReference timeReference, campReference;
    private String campKey;
    private TimeDAO timeDAO;
    private Time time;
    private TextInputLayout nomeTextInput, dirigenteTextInput, cidadeTextInput;
    private TextInputEditText nomeEditText, dirigenteEditText, cidadeEditText;
    private String nome, dirigente, cidade;
    private boolean eraCabecaDeChave, primeira = false, segunda = false;
    private List<Usuario> usuarios;
    private ValueEventListener timeListener, campListener;
    private Switch swCabecaDeChave;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela_editar_time,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        campKey = getArguments().getString("campKey");
        String timeKey = getArguments().getString("timeKey");
        user = FirebaseAuth.getInstance().getCurrentUser();
        timeReference = FirebaseDatabase.getInstance().getReference().child("campeonato-times").child(campKey).child(timeKey);
        DatabaseReference jogadoresReference = FirebaseDatabase.getInstance().getReference().child("time-jogadores").child(timeKey);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("campeonato-seguidores").child(campKey);
        campReference = FirebaseDatabase.getInstance().getReference().child("campeonatos").child(campKey);
        timeDAO = new TimeDAO();
        swCabecaDeChave = view.findViewById(R.id.sw_cabecaDeChave);
        nomeTextInput = view.findViewById(R.id.nome_text_input);
        nomeEditText = view.findViewById(R.id.nome_edit_text);
        dirigenteTextInput = view.findViewById(R.id.dirigente_text_input);
        dirigenteEditText = view.findViewById(R.id.dirigente_edit_text);
        cidadeTextInput = view.findViewById(R.id.cidade_text_input);
        cidadeEditText = view.findViewById(R.id.cidade_edit_text);
        time = new Time();
        ValueEventListener mTimeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                time = dataSnapshot.getValue(Time.class);
                nomeEditText.setText(time.getNome());
                dirigenteEditText.setText(time.getDirigente());
                cidadeEditText.setText(time.getCidade());
                swCabecaDeChave.setChecked(time.isCabecaDeChave());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        UsuarioHelper helper1 = new UsuarioHelper(userRef);
        usuarios = helper1.retrive();
        timeReference.addListenerForSingleValueEvent(mTimeListener);
        timeListener = mTimeListener;
        JogadorHelper helper = new JogadorHelper(jogadoresReference);
        final List<Jogador> jogadores = helper.retrive();
        final ArrayAdapter<Jogador> adapter = new ArrayAdapter<>(getContext(), R.layout.personalizado_list_item, jogadores);
        //createDrawer()
        swCabecaDeChave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (primeira) {
                    if (!segunda) {
                        segunda = true;
                        eraCabecaDeChave = !isChecked;
                    }
                } else {
                    primeira = true;
                }

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (timeListener != null) {
            timeReference.removeEventListener(timeListener);
        }
        if (campListener != null) {
            campReference.removeEventListener(campListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                criarActivity();
                break;
            case R.id.btn_salvar:
                ValueEventListener mCampListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                        nome = nomeEditText.getText().toString();
                        dirigente = dirigenteEditText.getText().toString();
                        cidade = cidadeEditText.getText().toString();
                        if (!valida(nome)) {
                            nomeTextInput.setError(getString(R.string.ftc_aviso_vazio));
                        } else {
                            nomeTextInput.setError(null);
                            if (!valida(dirigente)) {
                                dirigenteTextInput.setError(getString(R.string.ftc_aviso_vazio));
                            } else {
                                dirigenteTextInput.setError(null);
                                if (!valida(cidade)) {
                                    cidadeTextInput.setError(getString(R.string.ftc_aviso_vazio));
                                } else {
                                    cidadeTextInput.setError(null);
                                    CampeonatoDAO campDAO = new CampeonatoDAO();
                                    if (segunda) {
                                        if (eraCabecaDeChave && !swCabecaDeChave.isChecked()) {
                                            campDAO.cabecaDeChaveMenos(camp, usuarios);
                                        } else if (!eraCabecaDeChave && swCabecaDeChave.isChecked()) {
                                            campDAO.cabecaDeChaveMais(camp, usuarios);
                                        }
                                    }
                                    altera();
                                    Intent it = new Intent(getContext(), TelaEditarCamp.class);
                                    it.putExtra("user", user);
                                    it.putExtra("campKey", campKey);
                                    TelaCamps t = (TelaCamps)getActivity();
                                    t.startActivity(it);
                                    t.finish();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                campReference.addListenerForSingleValueEvent(mCampListener);
                campListener = mCampListener;
        }
        return super.onOptionsItemSelected(item);
    }


    private void criarActivity() {
        Intent it = new Intent(getContext(), TelaEditarCamp.class);
        it.putExtra("user", user);
        it.putExtra("campKey", campKey);
        TelaCamps t = (TelaCamps)getActivity();
        t.startActivity(it);
        t.finish();
    }

    private boolean valida(String s) {
        return !s.equals("");
    }

    private void altera() {
        time.setNome(nome);
        time.setDirigente(dirigente);
        time.setCidade(cidade);
        time.setCabecaDeChave(swCabecaDeChave.isChecked());
        timeDAO.alterar(time, campKey);
        Toast.makeText(getContext(), R.string.timeAlterado, Toast.LENGTH_SHORT).show();
    }


   /* private void createDrawer() {
        headerNavigation = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimaryDark)
                .withProfileImagesVisible(false)
                .addProfiles(new ProfileDrawerItem().withName(user.getDisplayName()).withEmail(user.getEmail())
                )
                .build();
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.ftc_adicionar_jogador).withIcon(R.drawable.plus_32px);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.ftc_excluir_time).withIcon(R.drawable.trash_32px);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.ftc_voltar).withIcon(R.drawable.back_32px);
        Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withSliderBackgroundDrawableRes(R.drawable.gradient)
                .withToolbar(toolbar)
                .withAccountHeader(headerNavigation)
                .addDrawerItems(
                        item2,
                        new DividerDrawerItem(),
                        item3,
                        new DividerDrawerItem(),
                        item4
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 1) {
                            Intent it = new Intent(TelaEditarTime.this, TelaAdicionarJogador.class);
                            it.putExtra("user", user);
                            it.putExtra("timeKey", timeKey);
                            it.putExtra("campKey", campKey);
                            startActivity(it);
                            finish();
                        } else if (drawerItem.getIdentifier() == 2) {
                            AlertDialog.Builder dlg = new AlertDialog.Builder(TelaEditarTime.this);
                            dlg.setMessage(R.string.ftc_certeza).setPositiveButton(R.string.ftc_excluir, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ValueEventListener valueEventListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                                            if (!camp.isIniciado() && !camp.isFinalizado()) {
                                                timeDAO.excluir(timeKey, campKey);
                                                Intent it = new Intent(TelaEditarTime.this, TelaEditarCamp.class);
                                                it.putExtra("user", user);
                                                it.putExtra("campKey", campKey);
                                                startActivity(it);
                                                finish();
                                            }else{
                                                Toast.makeText(TelaEditarTime.this, "Você não pode mais alterar esse campeonato!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }

                                    };
                                    campReference.addListenerForSingleValueEvent(valueEventListener);
                                    campListener1 = valueEventListener;
                                }
                            }).setNeutralButton(R.string.ftc_cancelar, null);
                            dlg.show();
                        } else if (drawerItem.getIdentifier() == 3) {
                            criarActivity();
                        }

                        return false;
                    }
                })
                .withSelectedItem(-1)
                .build();
        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer.getActionBarDrawerToggle().setHomeAsUpIndicator(R.drawable.menu_32px);
    }*/
}
