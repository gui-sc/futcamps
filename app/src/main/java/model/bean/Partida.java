package model.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties ({"mandante","visitante","campeonato"})
public class Partida implements Parcelable {
    private String id;
    private int placarMandante;
    private int placarVisitante;
    private String local;
    private String data;
    private Time mandante;
    private Time visitante;
    private String idMandante;
    private String idVisitante;
    private Campeonato campeonato;
    private String observacoes;
    private String fase;
    private boolean cadastrada;
    private int penaltisMandante;
    private int penaltisVisitante;
    private String nomeMandante;
    private String nomeVisitante;


    private Partida(Parcel p){
        id = p.readString();
        placarMandante = p.readInt();
        placarVisitante = p.readInt();
        nomeMandante = p.readString();
        nomeVisitante = p.readString();
        local = p.readString();
        data = p.readString();
        idMandante = p.readString();
        idVisitante = p.readString();
        observacoes = p.readString();
        fase = p.readString();
        penaltisMandante = p.readInt();
        penaltisVisitante = p.readInt();
        cadastrada = p.readInt()==1;
    }

    public String getNomeMandante() {
        return nomeMandante;
    }

    public void setNomeMandante(String nomeMandante) {
        this.nomeMandante = nomeMandante;
    }

    public String getNomeVisitante() {
        return nomeVisitante;
    }

    public void setNomeVisitante(String nomeVisitante) {
        this.nomeVisitante = nomeVisitante;
    }

    public String getIdMandante() {
        return idMandante;
    }

    public void setIdMandante(String idMandante) {
        this.idMandante = idMandante;
    }

    public String getIdVisitante() {
        return idVisitante;
    }

    public void setIdVisitante(String idVisitante) {
        this.idVisitante = idVisitante;
    }

    public int getPenaltisMandante() {
        return penaltisMandante;
    }

    public void setPenaltisMandante(int penaltisMandante) {
        this.penaltisMandante = penaltisMandante;
    }

    public int getPenaltisVisitante() {
        return penaltisVisitante;
    }

    public void setPenaltisVisitante(int penaltisVisitante) {
        this.penaltisVisitante = penaltisVisitante;
    }


    public boolean isCadastrada() {
        return cadastrada;
    }

    public void setCadastrada(boolean cadastrada) {
        this.cadastrada = cadastrada;
    }


    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public Partida() {
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Time getMandante() {
        return mandante;
    }

    public void setMandante(Time mandante) {
        this.mandante = mandante;
    }

    public Time getVisitante() {
        return visitante;
    }

    public void setVisitante(Time visitante) {
        this.visitante = visitante;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato camp) {
        this.campeonato = camp;
    }

    public int getPlacarMandante() {
        return placarMandante;
    }

    public void setPlacarMandante(int placarMandante) {
        this.placarMandante = placarMandante;
    }

    public int getPlacarVisitante() {
        return placarVisitante;
    }

    public void setPlacarVisitante(int placarVisitante) {
        this.placarVisitante = placarVisitante;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> result = new HashMap<>();

        result.put("id",id);
        result.put("idMandante",mandante.getId());
        result.put("idVisitante",visitante.getId());
        result.put("local",local);
        result.put("placarMandante",placarMandante);
        result.put("placarVisitante",placarVisitante);
        result.put("penaltisMandante",penaltisMandante);
        result.put("penaltisVisitante",penaltisVisitante);
        result.put("fase",fase);
        result.put("data",data);
        result.put("observacoes",observacoes);
        result.put("cadastrada",cadastrada);
        result.put("nomeMandante",nomeMandante);
        result.put("nomeVisitante",nomeVisitante);
        return result;
    }

    @Override
    public String toString() {
        if (cadastrada) {
            return nomeMandante+" "+placarMandante+" X "+placarVisitante+" "+nomeVisitante;
        } else {
            return nomeMandante + " X " + nomeVisitante;
        }
    }
    public static final Parcelable.Creator
        CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new Partida(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Partida[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(placarMandante);
        dest.writeInt(placarVisitante);
        dest.writeString(nomeMandante);
        dest.writeString(nomeVisitante);
        dest.writeString(local);
        dest.writeString(data);
        dest.writeString(idMandante);
        dest.writeString(idVisitante);
        dest.writeString(observacoes);
        dest.writeString(fase);
        dest.writeInt(penaltisMandante);
        dest.writeInt(penaltisVisitante);
        dest.writeInt(cadastrada?1:0);
    }
}
