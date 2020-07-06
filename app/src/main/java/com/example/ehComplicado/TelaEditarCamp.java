package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ehComplicado.FirebaseHelper.TimeHelper;
import com.example.ehComplicado.FirebaseHelper.UsuarioHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.List;

import model.bean.Campeonato;
import model.bean.Time;
import model.bean.Usuario;
import model.dao.CampeonatoDAO;

public class TelaEditarCamp extends AppCompatActivity {

    private DatabaseReference campReference;
    private ValueEventListener campListener, campListener2;
    private String campKey;

    AccountHeader headerNavigation;
    private TextInputLayout nomeTextInput;
    private TextInputLayout cidadeTextInput;
    private TextInputEditText nomeEditText, cidadeEditText, premiacaoEditText;
    private Campeonato camp;
    private CampeonatoDAO campDAO;
    private String nome, cidade, premiacao;
    Toolbar toolbar;
    UsuarioHelper helper1;
    FirebaseUser user;
    TimeHelper helper;
    List<Time> times;
    List<Usuario> usuarios;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_editar_camp);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        campDAO = new CampeonatoDAO();
        getSupportActionBar().setTitle(R.string.ftc_editar_campeonato);
        campKey = getIntent().getStringExtra("campKey");
        campReference = FirebaseDatabase.getInstance().getReference().child("campeonatos").child(campKey);
        DatabaseReference timesReference = FirebaseDatabase.getInstance().getReference().child("campeonato-times").child(campKey);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("campeonato-seguidores").child(campKey);
        user = getIntent().getParcelableExtra("user");
        ListView lstTimes = findViewById(R.id.lst_times);
        nomeTextInput = findViewById(R.id.nome_text_input);
        nomeEditText = findViewById(R.id.nome_edit_text);
        cidadeTextInput = findViewById(R.id.cidade_text_input);
        cidadeEditText = findViewById(R.id.cidade_edit_text);
        premiacaoEditText = findViewById(R.id.premiacao_edit_text);
        camp = new Campeonato();
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                camp = dataSnapshot.getValue(Campeonato.class);
                nomeEditText.setText(camp.getNome());
                cidadeEditText.setText(camp.getCidade());
                premiacaoEditText.setText(camp.getPremiacao());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(mCampListener);
        campListener = mCampListener;
        helper1 = new UsuarioHelper(userRef);
        usuarios = helper1.retrive();
        helper = new TimeHelper(timesReference);
        times = helper.retrive();
        final ArrayAdapter<Time> adapter = new ArrayAdapter<>(this, R.layout.personalizado_list_item, times);
        lstTimes.setAdapter(adapter);
        lstTimes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(TelaEditarCamp.this, TelaEditarTime.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra("timeKey", times.get(position).getId());
                it.putExtra("campKey", campKey);
                it.putExtra("user", user);
                startActivity(it);
                finish();
            }
        });
        final ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ImageButton btnCopiar = findViewById(R.id.btn_copiar);
        btnCopiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clip = ClipData.newPlainText(getString(R.string.codigo), campKey);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(TelaEditarCamp.this, R.string.codigoCopiado, Toast.LENGTH_SHORT).show();
            }
        });
        createDrawer();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (campListener != null) {
            campReference.removeEventListener(campListener);
        }
        if (campListener2 != null) {
            campReference.removeEventListener(campListener2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ftc_menu_salvar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                criarActivity();
                break;
            case R.id.btn_salvar:
                nome = nomeEditText.getText().toString();
                cidade = cidadeEditText.getText().toString();
                premiacao = premiacaoEditText.getText().toString();
                if (!valida(nome)) {
                    nomeTextInput.setError(getString(R.string.ftc_aviso_vazio));
                } else {
                    nomeTextInput.setError(null);
                    if (!valida(cidade)) {
                        cidadeTextInput.setError(getString(R.string.ftc_aviso_vazio));
                    } else {
                        cidadeTextInput.setError(null);
                        alterar();
                        Intent it = new Intent(TelaEditarCamp.this, TelaCarregarCamp.class);
                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        it.putExtra("user", user);
                        startActivity(it);
                    }
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        criarActivity();
    }

    private void criarActivity() {
        startActivity(new Intent(TelaEditarCamp.this, TelaCarregarCamp.class)
                .putExtra("user", user)
        );
        finish();
    }

    private void alterar() {
        camp.setNome(nome);
        camp.setLocal(cidade);
        camp.setPremiacao(premiacao);
        campDAO.alterar(camp, usuarios);
        Toast.makeText(this, R.string.campeonatoAlterado, Toast.LENGTH_SHORT).show();
    }

    private boolean valida(String s) {
        return !s.equals("");
    }


    private void createDrawer() {
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                camp = dataSnapshot.getValue(Campeonato.class);
                headerNavigation = new AccountHeaderBuilder()
                        .withActivity(TelaEditarCamp.this)
                        .withHeaderBackground(R.color.colorPrimaryDark)
                        .withProfileImagesVisible(false)
                        .addProfiles(new ProfileDrawerItem().withName(user.getDisplayName()).withEmail(user.getEmail())
                        )
                        .build();
                PrimaryDrawerItem item2;
                if (camp.isFaseDeGrupos() || camp.isOitavas() || camp.isQuartas() || camp.isIniciado()) {
                    item2 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.ftc_atualizar).withIcon(R.drawable.leaderboard_32px);
                } else if (camp.isFinalizado()) {
                    item2 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.ftc_resultados).withIcon(R.drawable.medal_first_place_32px);
                } else {
                    item2 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.ftc_continuar_cadast).withIcon(R.drawable.refresh_shield_32px);
                }
                PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.ftc_excluir_campeonato).withIcon(R.drawable.trash_32px);
                PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.ftc_voltar).withIcon(R.drawable.back_32px);
                Drawer drawer = new DrawerBuilder()
                        .withActivity(TelaEditarCamp.this)
                        .withSliderBackgroundDrawableRes(R.drawable.gradient)
                        .withToolbar(toolbar)
                        .withAccountHeader(headerNavigation)
                        .addDrawerItems(
                                item2,
                                new DividerDrawerItem(),
                                item3,
                                new DividerDrawerItem(),
                                item4
                        )
                        .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                if (drawerItem.getIdentifier() == 1) {
                                    if (camp.isFinalizado()) {
                                        Intent it = new Intent(TelaEditarCamp.this, TelaPremios.class);
                                        it.putExtra("campKey", camp.getId());
                                        it.putExtra("user", user);
                                        startActivity(it);
                                        finish();
                                    } else {
                                        if (times.size() != camp.getNumTimes()) {
                                            Intent it = new Intent(TelaEditarCamp.this, TelaCadastroTime.class);
                                            it.putExtra("campKey", camp.getId());
                                            it.putExtra("user", user);
                                            startActivity(it);
                                            finish();
                                        } else {
                                            if (camp.isFaseDeGrupos()) {
                                                if (camp.getNumTimes() == times.size()) {
                                                    Intent it = new Intent(TelaEditarCamp.this, TelaQuatroGrupos.class);
                                                    it.putExtra("campKey", campKey);
                                                    it.putExtra("user", user);
                                                    startActivity(it);
                                                    finish();
                                                } else {
                                                    Intent it = new Intent(TelaEditarCamp.this, TelaCadastroTime.class);
                                                    it.putExtra("campKey", campKey);
                                                    it.putExtra("user", user);
                                                    startActivity(it);
                                                    finish();
                                                }
                                            } else {
                                                if (camp.isOitavas()) {
                                                    Intent it = new Intent(TelaEditarCamp.this, TelaOitavas.class);
                                                    it.putExtra("campKey", campKey);
                                                    it.putExtra("user", user);
                                                    startActivity(it);
                                                    finish();
                                                }
                                                if (camp.isQuartas()) {
                                                    Intent it = new Intent(TelaEditarCamp.this, TelaQuartas.class);
                                                    it.putExtra("campKey", campKey);
                                                    it.putExtra("user", user);
                                                    startActivity(it);
                                                    finish();
                                                }
                                                if (camp.isSemi()) {
                                                    Intent it = new Intent(TelaEditarCamp.this, TelaSemi.class);
                                                    it.putExtra("campKey", campKey);
                                                    it.putExtra("user", user);
                                                    startActivity(it);
                                                    finish();
                                                }
                                                if (camp.isFinal()) {
                                                    Intent it = new Intent(TelaEditarCamp.this, TelaFinal.class);
                                                    it.putExtra("campKey", campKey);
                                                    it.putExtra("user", user);
                                                    startActivity(it);
                                                    finish();

                                                }
                                            }
                                        }
                                    }
                                } else if (drawerItem.getIdentifier() == 2) {
                                    AlertDialog.Builder dlg = new AlertDialog.Builder(TelaEditarCamp.this);
                                    dlg.setMessage(R.string.ftc_certeza).setPositiveButton(R.string.ftc_excluir, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            campDAO.excluir(camp, user.getUid(), usuarios);
                                            criarActivity();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(mCampListener);
        campListener2 = mCampListener;
    }
}
