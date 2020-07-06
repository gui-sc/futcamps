package model.bean;

public class CVermelho {
    private String jogador;
    private String time;


    public String getJogador() {
        return jogador;
    }

    public void setJogador(String jogador) {
        this.jogador = jogador;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return jogador + " - " + time;
    }

}
