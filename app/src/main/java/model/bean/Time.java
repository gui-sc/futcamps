package model.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties ({"campeonato"})
public class Time {
    private String id;
    private String uid;
    private String nome;
    private String dirigente;
    private String cidade;
    private Campeonato campeonato;
    private int grupo;
    private int pontos;
    private boolean eliminado;
    private boolean primeiro;
    private boolean cabecaDeChave;
    private int saldo;
    private int golsPro;
    private int golsContra;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public int getGolsPro() {
        return golsPro;
    }

    public void setGolsPro(int golsPro) {
        this.golsPro = golsPro;
    }

    public int getGolsContra() {
        return golsContra;
    }

    public void setGolsContra(int golsContra) {
        this.golsContra = golsContra;
    }

    public boolean isCabecaDeChave() {
        return cabecaDeChave;
    }

    public void setCabecaDeChave(boolean cabecaDeChave) {
        this.cabecaDeChave = cabecaDeChave;
    }
    public Time() {
    }

    public boolean isPrimeiro() {
        return primeiro;
    }

    public void setPrimeiro(boolean primeiro) {
        this.primeiro = primeiro;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public int getGrupo() {
        return grupo;
    }

    public void setGrupo(int grupo) {
        this.grupo = grupo;
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDirigente() {
        return dirigente;
    }

    public void setDirigente(String dirigente) {
        this.dirigente = dirigente;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String Cidade) {
        this.cidade = Cidade;
    }

    public Time(String nome, String dirigente, String Cidade) {
        this.nome = nome;
        this.dirigente = dirigente;
        this.cidade = Cidade;
    }

    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    public Map<String, Object>toMap(){
        HashMap<String, Object> result =new HashMap<>();
        result.put("id",id);
        result.put("uid",uid);
        result.put("nome",nome);
        result.put("dirigente",dirigente);
        result.put("cidade",cidade);
        result.put("grupo",grupo);
        result.put("pontos",pontos);
        result.put("eliminado",eliminado);
        result.put("primeiro",primeiro);
        result.put("campeonato",null);
        result.put("cabecaDeChave",cabecaDeChave);
        result.put("saldo",saldo);
        result.put("golsPro",golsPro);
        result.put("golsContra",golsContra);
        return result;

    }

    @Override
    public String toString(){
        return nome;
    }
}
