package com.example.ehComplicado;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.example.ehComplicado.FirebaseHelper.TimeHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import model.bean.Campeonato;
import model.bean.Time;

public class TimesFragment extends Fragment {
    private Campeonato camp;
    private ValueEventListener listener;
    private DatabaseReference timesReference;
    Button btnNovo;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ListView lstTimes = view.findViewById(R.id.lst_times);
        camp = getArguments().getParcelable("camp");
        timesReference = FirebaseDatabase.getInstance().getReference().child("campeonato-times").child(camp.getId());
        TimeHelper helper = new TimeHelper(timesReference);
        final List<Time> times = helper.retrive();
        btnNovo = view.findViewById(R.id.novo_button);
        btnNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelaCadastroTime t = new TelaCadastroTime();
                Bundle b = new Bundle();
                b.putString("campKey",camp.getId());
                t.setArguments(b);
                openFragment(t);
            }
        });
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final TimesAdapter adapter = new TimesAdapter(getContext(), times);
                lstTimes.setAdapter(adapter);
                if(times.size() == camp.getNumTimes()){
                    btnNovo.setClickable(false);
                    btnNovo.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        timesReference.addListenerForSingleValueEvent(eventListener);
        listener = eventListener;
        lstTimes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(getContext(), TelaPrincipalTimes.class);
                it.putExtra("timeKey", times.get(position).getId());
                it.putExtra("camp", camp);
                TelaCamps t = (TelaCamps)getActivity();
                t.startActivity(it);
                t.finish();
            }
        });
    }
    public void openFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_times, container, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (listener != null){
            timesReference.removeEventListener(listener);
        }
    }
}
