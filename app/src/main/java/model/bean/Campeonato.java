package model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import java.util.HashMap;
import java.util.Map;

public class Campeonato implements Parcelable{
  private String id;
  private String uid;
  private String formato;
  private String nome;
  private String cidade;
  private int ano;
  private int classificados;
  private String premiacao;
  private int numTimes;
  private int numGrupos;
  private boolean faseDeGrupos;
  private boolean oitavas;
  private boolean quartas;
  private boolean semi;
  private boolean Final;
  private int cabecasDeChave;
  private boolean iniciado;
  private boolean finalizado;
  private boolean zerarCartoesOitavas;
  private boolean zerarCartoesQuartas;
  private boolean zerarCartoesSemi;
  private int cartoesPendurado;

  public Campeonato(String uid, String formato, String nome, String local, int ano, String premiacao, int numTimes, int numGrupos) {
    this.id = id;
    this.uid = uid;
    this.formato = formato;
    this.nome = nome;
    this.cidade = local;
    this.ano = ano;
    this.premiacao = premiacao;
    this.numTimes = numTimes;
    this.numGrupos = numGrupos;
  }

  public int getCartoesPendurado() {
    return cartoesPendurado;
  }

  public void setCartoesPendurado(int cartoesPendurado) {
    this.cartoesPendurado = cartoesPendurado;
  }

  public int getClassificados() {
    return classificados;
  }

  public void setClassificados(int classificados) {
    this.classificados = classificados;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getFormato() {
    return formato;
  }

  public void setFormato(String formato) {
    this.formato = formato;
  }

  public String getCidade() {
    return getLocal();
  }

  public void setCidade(String cidade) {
    setLocal(cidade);
  }

  @Exclude
  public Map<String, Object> toMap() {
    HashMap<String, Object> result =new HashMap<>();
    result.put("uid",uid);
    result.put("id",id);
    result.put("formato",formato);
    result.put("nome",nome);
    result.put("cidade",cidade);
    result.put("ano",ano);
    result.put("classificados",classificados);
    result.put("premiacao",premiacao);
    result.put("numTimes",numTimes);
    result.put("numGrupos",numGrupos);
    result.put("faseDeGrupos",faseDeGrupos);
    result.put("oitavas",oitavas);
    result.put("quartas",quartas);
    result.put("semi",semi);
    result.put("Final",Final);
    result.put("campeao",null);
    result.put("viceCampeao",null);
    result.put("cabecasDeChave",cabecasDeChave);
    result.put("iniciado",iniciado);
    result.put("finalizado",finalizado);
    result.put("artilheiro",null);
    result.put("zerarCartoesOitavas",zerarCartoesOitavas);
    result.put("zerarCartoesQuartas",zerarCartoesQuartas);
    result.put("zerarCartoesSemi",zerarCartoesSemi);
    result.put("cartoesPendurado",cartoesPendurado);
    return result;
  }


  public boolean isOitavas() {
    return oitavas;
  }

  public void setOitavas(boolean oitavas) {
    this.oitavas = oitavas;
  }

  public boolean isQuartas() {
    return quartas;
  }

  public void setQuartas(boolean quartas) {
    this.quartas = quartas;
  }

  public boolean isSemi() {
    return semi;
  }

  public void setSemi(boolean semi) {
    this.semi = semi;
  }

  public boolean isFinal() {
    return Final;
  }

  public void setFinal(boolean Final) {
    this.Final = Final;
  }


  public boolean isFinalizado() {
    return finalizado;
  }

  public void setFinalizado(boolean finalizado) {
    this.finalizado = finalizado;
  }

  public boolean isIniciado() {
    return iniciado;
  }

  public void setIniciado(boolean iniciado) {
    this.iniciado = iniciado;
  }

  public int getCabecasDeChave() {
    return cabecasDeChave;
  }

  public void setCabecasDeChave(int cabecasDeChave) {
    this.cabecasDeChave = cabecasDeChave;
  }

  public Campeonato() {
  }

  private Campeonato(Parcel p){
    id = p.readString();
    uid = p.readString();
    formato = p.readString();
    nome = p.readString();
    cidade = p.readString();
    ano = p.readInt();
    classificados = p.readInt();
    premiacao = p.readString();
    numTimes = p.readInt();
    numGrupos = p.readInt();
    zerarCartoesOitavas = p.readInt()==1;
    zerarCartoesQuartas = p.readInt()==1;
    zerarCartoesSemi = p.readInt()==1;
    cartoesPendurado = p.readInt();
    faseDeGrupos = p.readInt()==1;
    oitavas = p.readInt()==1;
    quartas = p.readInt()==1;
    semi = p.readInt()==1;
    Final = p.readInt()==1;
    finalizado= p.readInt()==1;
  }

  public boolean isZerarCartoesOitavas() {
    return zerarCartoesOitavas;
  }

  public void setZerarCartoesOitavas(boolean zerarCartoesOitavas) {
    this.zerarCartoesOitavas = zerarCartoesOitavas;
  }

  public boolean isZerarCartoesQuartas() {
    return zerarCartoesQuartas;
  }

  public void setZerarCartoesQuartas(boolean zerarCartoesQuartas) {
    this.zerarCartoesQuartas = zerarCartoesQuartas;
  }

  public boolean isZerarCartoesSemi() {
    return zerarCartoesSemi;
  }

  public void setZerarCartoesSemi(boolean zerarCartoesSemi) {
    this.zerarCartoesSemi = zerarCartoesSemi;
  }

  public boolean isFaseDeGrupos() {
    return faseDeGrupos;
  }

  public void setFaseDeGrupos(boolean faseDeGrupos) {
    this.faseDeGrupos = faseDeGrupos;
  }

  public int getNumGrupos() {
    return numGrupos;
  }

  public void setNumGrupos(int numGrupos) {
    this.numGrupos = numGrupos;
  }


  public int getNumTimes() {
    return numTimes;
  }

  public void setNumTimes(int numTimes) {
    this.numTimes = numTimes;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  private String getLocal() {
    return cidade;
  }

  public void setLocal(String cidade) {
    this.cidade = cidade;
  }

  public String getPremiacao() {
    return premiacao;
  }

  public void setPremiacao(String premiacao) {
    this.premiacao = premiacao;
  }

  public int getAno() {
    return ano;
  }

  public void setAno(int ano) {
    this.ano = ano;
  }

  @Override
  public String toString() {
    return nome + " " + ano;
  }

  public static final Parcelable.Creator
          CREATOR = new Parcelable.Creator() {
    @Override
    public Object createFromParcel(Parcel source) {
      return new Campeonato(source);
    }

    @Override
    public Object[] newArray(int size) {
      return new Campeonato[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(id);
    dest.writeString(uid);
    dest.writeString(formato);
    dest.writeString(nome);
    dest.writeString(cidade);
    dest.writeInt(ano);
    dest.writeInt(classificados);
    dest.writeString(premiacao);
    dest.writeInt(numTimes);
    dest.writeInt(numGrupos);
    dest.writeInt(zerarCartoesOitavas?1:0);
    dest.writeInt(zerarCartoesQuartas?1:0);
    dest.writeInt(zerarCartoesSemi?1:0);
    dest.writeInt(cartoesPendurado);
    dest.writeInt(faseDeGrupos?1:0);
    dest.writeInt(oitavas?1:0);
    dest.writeInt(quartas?1:0);
    dest.writeInt(semi?1:0);
    dest.writeInt(Final?1:0);
    dest.writeInt(finalizado?1:0);

  }

}