package model.dao;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import model.bean.Resultados;

public class ResultadosDAO {

    private DatabaseReference resultadosReference = FirebaseDatabase.getInstance().getReference();

    public ResultadosDAO(){

    }
    public void inserir(Resultados resultados, String campKey){
        resultadosReference.child("resultado-campeonatos").child(campKey).setValue(resultados);
    }

}
