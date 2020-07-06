package com.example.ehComplicado.FirebaseHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import model.bean.CAmarelo;

public class AmareloHelper {

    private DatabaseReference db;
    private List<CAmarelo> amarelos = new ArrayList<>();

    public AmareloHelper(DatabaseReference db) {
        this.db = db;
    }

    public List<CAmarelo> retrive(){
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CAmarelo amarelo = dataSnapshot.getValue(CAmarelo.class);
                amarelos.add(amarelo);
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
        return amarelos;
    }
}
