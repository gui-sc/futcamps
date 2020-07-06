package model.bean;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    private String titulo;
    private String mensagem;

    public Notification(){

    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Map<String,Object> toMap(){
        Map<String, Object> values = new HashMap<>();
        values.put("titulo",titulo);
        values.put("mensagem",mensagem);
        return values;
    }
}
