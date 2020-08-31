package com.example.ehComplicado;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class TelaCarregarCamp extends BaseActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener{

  Toolbar toolbar;
  DatabaseReference db;
  ValueEventListener campListener;

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tela_principal);
    BottomNavigationView navigationView = findViewById(R.id.navigationView);
    navigationView.setOnNavigationItemSelectedListener(this);
    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    Fragment homeFragment = HomeFragment.newInstance();
    openFragment(homeFragment);
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

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
    switch (menuItem.getItemId()){
      case R.id.home_button:
        Fragment homeFragment = HomeFragment.newInstance();
        Bundle dataHome = new Bundle();
        homeFragment.setArguments(dataHome);
        openFragment(homeFragment);
        break;
      case R.id.novo_button:
        Fragment novoCampFragment = NovoCampFragment.newInstance();
        Bundle dataNovo = new Bundle();
        novoCampFragment.setArguments(dataNovo);
        openFragment(novoCampFragment);
        break;
      case R.id.seguindo_button:
        Fragment seguindoFragment = TelaSeguindo.newInstance();
        Bundle dataSeguindo = new Bundle();
        seguindoFragment.setArguments(dataSeguindo);
        openFragment(seguindoFragment);
        break;
      case R.id.buscar_button:
        Fragment buscarFragment = BuscarFragment.newInstance();
        Bundle dataBuscar = new Bundle();
        buscarFragment.setArguments(dataBuscar);
        openFragment(buscarFragment);
        break;
      case R.id.deslogar_button:
        criarActivity();
        break;
    }
    return true;
  }

  public void openFragment(Fragment fragment){
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.container, fragment);
    transaction.addToBackStack(null);
    transaction.commit();
  }
}
