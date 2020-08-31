package com.example.ehComplicado;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.ehComplicado.FirebaseHelper.CampeonatoHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.List;
import model.bean.Campeonato;

public class HomeFragment extends Fragment {

  private ListView lv;
  private FirebaseUser user;
  private List<Campeonato> list;
  private Campeonato camp;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.activity_tela_carregar_camp,container,false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    lv = view.findViewById(R.id.lst_camp);
    user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference db = FirebaseDatabase.getInstance().getReference()
            .child("user-campeonatos").child(user.getUid());
    DatabaseReference campReference = FirebaseDatabase.getInstance().getReference()
            .child("user-segue").child(user.getUid());
    CampeonatoHelper helper1 = new CampeonatoHelper(campReference);
    CampeonatoHelper helper = new CampeonatoHelper(db);
    final List<Campeonato> camps = helper1.retrive();
    list= helper.retrive();
    ValueEventListener mCampListener = new ValueEventListener() {

      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        CampeonatosAdapter adapter = new CampeonatosAdapter(getContext(), list);
        lv.setAdapter(adapter);
        updateToken(camps);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }

    };
    db.addListenerForSingleValueEvent(mCampListener);
    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        camp = list.get(position);
        Intent it = new Intent(getActivity(), TelaCamps.class);
        it.putExtra("camp",camp);
        TelaCarregarCamp t = (TelaCarregarCamp)getActivity();
        t.startActivity(it);
      }
    });
  }

  @Override
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  public static HomeFragment newInstance() {
    return new HomeFragment();
  }
  private void updateToken(List<Campeonato> usuarioSegue){
    String token = FirebaseInstanceId.getInstance().getToken();
    String uid = user.getUid();
    FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("token").setValue(token);
    for (Campeonato campeonato : usuarioSegue){
      FirebaseDatabase.getInstance().getReference().child("campeonato-seguidores").child(campeonato.getId()).child(uid)
              .child("token").setValue(token);
    }
  }
}
