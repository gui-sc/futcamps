package com.example.ehComplicado.FirebaseHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.List;
import model.bean.Campeonato;

public class CampeonatoHelper {

    private DatabaseReference db;
    private List<Campeonato> campeonatos = new ArrayList<>();

    public CampeonatoHelper(DatabaseReference db) {
        this.db = db;
    }

    public List<Campeonato> retrive() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Campeonato camp = dataSnapshot.getValue(Campeonato.class);
                campeonatos.add(camp);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        db.addChildEventListener(childEventListener);
        return campeonatos;
    }
}
