package model.bean;

public class Resultados {
    private String campeao;
    private String viceCampeao;
    private String artilheiro;
    private int gols;

    public Resultados(){

    }
    public String getCampeao() {
        return campeao;
    }

    public void setCampeao(String campeao) {
        this.campeao = campeao;
    }

    public String getViceCampeao() {
        return viceCampeao;
    }

    public void setViceCampeao(String viceCampeao) {
        this.viceCampeao = viceCampeao;
    }

    public String getArtilheiro() {
        return artilheiro;
    }

    public void setArtilheiro(String artilheiro) {
        this.artilheiro = artilheiro;
    }

    public int getGols() {
        return gols;
    }

    public void setGols(int gols) {
        this.gols = gols;
    }
}
