package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.ehComplicado.FirebaseHelper.CampeonatoHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
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
import model.dao.CampeonatoDAO;

public class TelaCarregarCamp extends BaseActivity {
    AccountHeader headerNavigation;
    private Campeonato camp;
    Toolbar toolbar;
    CampeonatoDAO campDAO;
    DatabaseReference db;
    FirebaseUser user;
    CampeonatoHelper helper;
    List<Campeonato>list;
    ValueEventListener campListener;
    ListView lv;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_carregar_camp);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //createNotificationChannel();
        getSupportActionBar().setTitle(R.string.ftc_camp_salvos);
        campDAO = new CampeonatoDAO();
        lv = findViewById(R.id.lst_camp);
        user = getIntent().getParcelableExtra("user");
        db = FirebaseDatabase.getInstance().getReference()
                .child("user-campeonatos").child(user.getUid());
        DatabaseReference campReference = FirebaseDatabase.getInstance().getReference()
                .child("user-segue").child(user.getUid());
        CampeonatoHelper helper1 = new CampeonatoHelper(campReference);
        helper = new CampeonatoHelper(db);
        final List<Campeonato> camps = helper1.retrive();
        list= helper.retrive();
        ValueEventListener mCampListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CampeonatosAdapter adapter = new CampeonatosAdapter(TelaCarregarCamp.this, list);
                lv.setAdapter(adapter);
                updateToken(camps);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        db.addListenerForSingleValueEvent(mCampListener);
        campListener = mCampListener;
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                camp = list.get(position);
                Intent it = new Intent(TelaCarregarCamp.this, TelaEditarCamp.class);
                it.putExtra("campKey",camp.getId());
                it.putExtra("user",user);
                startActivity(it);
                finish();
                }
        });
        createDrawer();

            /* EXEMPLO DE NOTIFICAÇÃO
        Intent it = new Intent(this, MainActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, it, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"CHANNEL_ID")
                .setSmallIcon(R.drawable.soccer_ball_32px)
                .setContentTitle("FutCamps")
                .setContentText("Você está sendo notificado")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        nm.notify(1,builder.build());*/

    }

    /*private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "nome";
            String description = "descricao";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    FIM DO EXEMPLO DE NOTIFICAÇÃO*/

    private void updateToken(List<Campeonato> usuarioSegue){
        String token = FirebaseInstanceId.getInstance().getToken();
        String uid = user.getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("token").setValue(token);
        for (Campeonato campeonato : usuarioSegue){
            FirebaseDatabase.getInstance().getReference().child("campeonato-seguidores").child(campeonato.getId()).child(uid)
                    .child("token").setValue(token);
        }
    }
    @Override
    public void onStop(){
        super.onStop();
        if (campListener!=null){
            db.removeEventListener(campListener);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            criarActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        criarActivity();
    }

    private void criarActivity() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(TelaCarregarCamp.this);
        dlg.setMessage(R.string.ftc_perg_sessao).setPositiveButton(R.string.ftc_sim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(TelaCarregarCamp.this, MainActivity.class));
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
                        if (drawerItem.getIdentifier()==2){
                            Intent it = new Intent(TelaCarregarCamp.this, TelaNovoCamp.class);
                            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            it.putExtra("user", user);
                            startActivity(it);
                            finish();
                        }else if(drawerItem.getIdentifier()==3){
                            Intent it = new Intent(TelaCarregarCamp.this, TelaSeguindo.class);
                            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            it.putExtra("user", user);
                            startActivity(it);
                            finish();
                        }else if(drawerItem.getIdentifier()==4){
                            Intent it = new Intent(TelaCarregarCamp.this, TelaBuscar.class);
                            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            it.putExtra("user", user);
                            startActivity(it);
                            finish();
                        }else if(drawerItem.getIdentifier()==5){
                            criarActivity();
                        }
                        return false;
                    }
                })
                .withSelectedItem(1)
                .build();
        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer.getActionBarDrawerToggle().setHomeAsUpIndicator(R.drawable.menu_32px);
    }


}
