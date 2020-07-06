package com.example.ehComplicado.FirebaseHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import model.bean.Jogador;

public class JogadorHelper {
    private DatabaseReference db;
    private List<Jogador> jogadores = new ArrayList<>();

    public JogadorHelper(DatabaseReference db) {
        this.db = db;
    }

    public List<Jogador> retrive(){
        jogadores.clear();
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Jogador jogador = dataSnapshot.getValue(Jogador.class);
                jogadores.add(jogador);
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
        return jogadores;
    }
}
