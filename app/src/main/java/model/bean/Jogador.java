package model.bean;

import java.util.HashMap;
import java.util.Map;

public class Jogador {
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


}
