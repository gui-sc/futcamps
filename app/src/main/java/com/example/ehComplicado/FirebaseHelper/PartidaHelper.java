package com.example.ehComplicado.FirebaseHelper;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.List;

import model.bean.Partida;

public class PartidaHelper {

    private DatabaseReference db;
    private List<Partida> partidas = new ArrayList<>();

    public PartidaHelper(DatabaseReference db) {
        this.db = db;
    }

    public List<Partida> retrive(){
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Partida partida = dataSnapshot.getValue(Partida.class);
                 partidas.add(partida);
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
        });
        return partidas;
    }
}
