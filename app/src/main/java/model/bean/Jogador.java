package model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Jogador implements Parcelable {
    private String id;
    private String idTime;
    private String nome;
    private Time time;
    private String apelido;
    private int gols;
    private int ca;
    private int cv;
    private boolean suspenso;
    private String dataNasc;
    private boolean pendurado;

    public boolean isPendurado() {
        return pendurado;
    }

    public void setPendurado(boolean pendurado) {
        this.pendurado = pendurado;
    }

    public String getIdTime() {
        return idTime;
    }

    public void setIdTime(String idTime) {
        this.idTime = idTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDataNasc() {
        return dataNasc;
    }

    public void setDataNasc(String dataNasc) {
        this.dataNasc = dataNasc;
    }

    public Jogador() {
    }

    public boolean isSuspenso() {
        return suspenso;
    }

    public void setSuspenso(boolean suspenso) {
        this.suspenso = suspenso;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public int getGols() {
        return gols;
    }

    public void setGols(int gols) {
        this.gols = gols;
    }

    public int getCa() {
        return ca;
    }

    public void setCa(int ca) {
        this.ca = ca;
    }

    public int getCv() {
        return cv;
    }

    public void setCv(int cv) {
        this.cv = cv;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id",id);
        result.put("apelido",apelido);
        result.put("ca",ca);
        result.put("cv",cv);
        result.put("gols",gols);
        result.put("dataNasc", dataNasc);
        result.put("nome",nome);
        result.put("suspenso",suspenso);
        result.put("idTime",idTime);
        result.put("pendurado",pendurado);
        return result;
    }

    @Override
    public String toString() {
        if (time == null){
            return apelido;
        }else {
            return apelido + " - "+ time.getNome();
        }
    }


    private Jogador(Parcel p){
        id = p.readString();
        apelido = p.readString();
        ca = p.readInt();
        cv = p.readInt();
        gols = p.readInt();
        dataNasc = p.readString();
        nome = p.readString();
        suspenso = p.readInt() == 1;
        idTime = p.readString();
        pendurado = p.readInt()==1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(apelido);
        dest.writeInt(ca);
        dest.writeInt(cv);
        dest.writeInt(gols);
        dest.writeString(dataNasc);
        dest.writeString(nome);
        dest.writeInt(suspenso?1:0);
        dest.writeString(idTime);
        dest.writeInt(pendurado?1:0);
    }

    public static final Parcelable.Creator
    CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new Jogador(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Jogador[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


}
