package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import model.bean.Usuario;

public class TelaSeguindo extends AppCompatActivity {
    AccountHeader headerNavigation;
    DatabaseReference campReference;
    FirebaseUser user;
    Toolbar toolbar;
    Campeonato camp;
    FirebaseDatabase db;
    CampeonatosAdapter2 adapter;
    ValueEventListener campListener;
    List<Campeonato> list;
    ListView lv;
    CampeonatoHelper helperSegue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_seguindo);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.ftc_seguindo);
        db = FirebaseDatabase.getInstance();
        lv = findViewById(R.id.lst_camp);
        user = getIntent().getParcelableExtra("user");
        campReference = FirebaseDatabase.getInstance().getReference()
                .child("user-segue").child(user.getUid());
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter = new CampeonatosAdapter2(TelaSeguindo.this,list,true,user.getUid());
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        campReference.addListenerForSingleValueEvent(mCampListener);
        campListener = mCampListener;
        helperSegue = new CampeonatoHelper(campReference);
        list= helperSegue.retrive();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                camp = list.get(position);
                if (camp.isFaseDeGrupos()){
                    Intent it = new Intent(TelaSeguindo.this,TelaQuatroGrupos.class);
                    it.putExtra("user",user);
                    it.putExtra("campKey",camp.getId());
                    startActivity(it);
                    finish();
                }else if(camp.isOitavas()){
                    Intent it = new Intent(TelaSeguindo.this,TelaOitavas.class);
                    it.putExtra("user",user);
                    it.putExtra("campKey",camp.getId());
                    startActivity(it);
                    finish();
                }else if(camp.isQuartas()){
                    Intent it = new Intent(TelaSeguindo.this,TelaQuartas.class);
                    it.putExtra("user",user);
                    it.putExtra("campKey",camp.getId());
                    startActivity(it);
                    finish();
                }else if(camp.isSemi()){
                    Intent it = new Intent(TelaSeguindo.this,TelaSemi.class);
                    it.putExtra("user",user);
                    it.putExtra("campKey",camp.getId());
                    startActivity(it);
                    finish();
                }else if(camp.isFinal()){
                    Intent it = new Intent(TelaSeguindo.this,TelaFinal.class);
                    it.putExtra("user",user);
                    it.putExtra("campKey",camp.getId());
                    startActivity(it);
                    finish();
                }else if(camp.isFinalizado()){
                    Intent it = new Intent(TelaSeguindo.this,TelaPremios.class);
                    it.putExtra("user",user);
                    it.putExtra("campKey",camp.getId());
                    startActivity(it);
                    finish();
                }else{
                    Toast.makeText(TelaSeguindo.this, R.string.avisoCampeonato, Toast.LENGTH_SHORT).show();
                }
            }
        });
        createDrawer();
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
    @Override
    public void onBackPressed() {
        criarActivity();
    }


    private void criarActivity() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(TelaSeguindo.this);
        dlg.setMessage(R.string.ftc_perg_sessao).setPositiveButton(R.string.ftc_sim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(TelaSeguindo.this, MainActivity.class));
                finish();
            }
        }).setNeutralButton(R.string.ftc_cancelar, null);
        dlg.show();
    }
    private void createDrawer() {
        ProfileDrawerItem profile = new ProfileDrawerItem().withName(user.getDisplayName())
                .withEmail(user.getEmail());
        headerNavigation = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimaryDark)
                .addProfiles(profile)
                .withHeightPx(40)
                .withProfileImagesVisible(false)
                .build();
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.ftc_camp_salvos).withIcon(R.drawable.home_32px);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.ftc_criar_camp).withIcon(R.drawable.plus_32px);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.ftc_seguindo).withIcon(R.drawable.estrela_vazia);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.ftc_pesquisar).withIcon(R.drawable.search_48px);
        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(5).withName(R.string.ftc_encerrar_sessao).withIcon(R.drawable.shutdown_32px);
        Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withSliderBackgroundDrawableRes(R.drawable.gradient)
                .withToolbar(toolbar)
                .withAccountHeader(headerNavigation)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        new DividerDrawerItem(),
                        item3,
                        new DividerDrawerItem(),
                        item4,
                        new DividerDrawerItem(),
                        item5
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier()==2) {
                            Intent it = new Intent(TelaSeguindo.this, TelaNovoCamp.class);
                            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            it.putExtra("user", user);
                            startActivity(it);
                        }else if(drawerItem.getIdentifier()==1){
                            Intent it = new Intent(TelaSeguindo.this, TelaCarregarCamp.class);
                            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            it.putExtra("user", user);
                            startActivity(it);
                        } else if(drawerItem.getIdentifier()==4){
                            Intent it = new Intent(TelaSeguindo.this, TelaBuscar.class);
                            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            it.putExtra("user", user);
                            startActivity(it);
                        }else if (drawerItem.getIdentifier()==5) {
                            criarActivity();
                        }

                        return false;
                    }
                })
                .withSelectedItem(3)
                .build();
        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer.getActionBarDrawerToggle().setHomeAsUpIndicator(R.drawable.menu_32px);
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
