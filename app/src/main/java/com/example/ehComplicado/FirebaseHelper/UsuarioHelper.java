package com.example.ehComplicado.FirebaseHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import model.bean.Usuario;


public class UsuarioHelper {
    private DatabaseReference db;
    private List<Usuario> usuarios = new ArrayList<>();

    public UsuarioHelper(DatabaseReference db) {
        this.db = db;
    }

    public List<Usuario> retrive(){
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                usuarios.add(usuario);
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
        return usuarios;
    }
}
