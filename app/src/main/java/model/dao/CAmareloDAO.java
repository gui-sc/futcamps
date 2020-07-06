package model.dao;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import model.bean.CAmarelo;

public class CAmareloDAO {
    private DatabaseReference amareloReference = FirebaseDatabase.getInstance().getReference();
    public CAmareloDAO(){

    }
    public void inserir(CAmarelo ca, String partidaKey){
        amareloReference.child("partida-amarelos").child(partidaKey).push().setValue(ca);
    }

}
