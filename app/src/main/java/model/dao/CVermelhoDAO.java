package model.dao;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import model.bean.CVermelho;

public class CVermelhoDAO {
    private DatabaseReference vermelhoReference = FirebaseDatabase.getInstance().getReference();
    public CVermelhoDAO(){

    }
    public void inserir(CVermelho cv,String partidaKey){
        vermelhoReference.child("partida-vermelhos").child(partidaKey).push().setValue(cv);
    }


}

