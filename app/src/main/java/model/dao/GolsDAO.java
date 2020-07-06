package model.dao;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import model.bean.Gols;

public class GolsDAO {
    private DatabaseReference golsReference = FirebaseDatabase.getInstance().getReference();
    public  GolsDAO(){

    }
    public void inserir(Gols gol, String partidaKey){
        golsReference.child("partida-gols").child(partidaKey).push().setValue(gol);
    }

}
