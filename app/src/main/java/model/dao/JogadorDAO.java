package model.dao;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.bean.Jogador;
import model.bean.Time;

public class JogadorDAO {

    private DatabaseReference jogadoresReference = FirebaseDatabase.getInstance().getReference();

    public JogadorDAO() {

    }

    public Jogador configurar(List<Time> times, Jogador jogador) {
        for (Time time : times) {
            if (jogador.getIdTime().equals(time.getId())) {
                jogador.setTime(time);
            }
        }
        return jogador;
    }

    public void inserir(Jogador jogador, String timeKey, String campKey) {
        String key = jogadoresReference.child("jogadores").push().getKey();
        jogador.setId(key);
        jogador.setIdTime(timeKey);
        jogadoresReference.child("jogadores").child(key).setValue(jogador);
        jogadoresReference.child("time-jogadores").child(timeKey).child(key).setValue(jogador);
        jogadoresReference.child("campeonato-jogadores").child(campKey).child(key).setValue(jogador);
    }

    public void excluir(String id, String timeKey, String campKey) {
        jogadoresReference.child("jogadores").child(id).removeValue();
        jogadoresReference.child("time-jogadores").child(timeKey).child(id).removeValue();
        jogadoresReference.child("campeonato-jogadores").child(campKey).child(id).removeValue();
    }

    public void alterar(Jogador jogador, String timeKey, String campKey) {
        jogador.setIdTime(timeKey);
        Map<String, Object> jogadorValues = jogador.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/jogadores/" + jogador.getId() + "/", jogadorValues);
        childUpdates.put("/time-jogadores/" + timeKey + "/" + jogador.getId() + "/", jogadorValues);
        childUpdates.put("/campeonato-jogadores/" + campKey + "/" + jogador.getId() + "/", jogadorValues);
        jogadoresReference.updateChildren(childUpdates);
    }

    public void atualizar(Jogador jogador, String timeKey, String campKey) {
        Map<String, Object> jogadorValues = jogador.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/jogadores/" + jogador.getId() + "/", jogadorValues);
        childUpdates.put("/time-jogadores/" + timeKey + "/" + jogador.getId() + "/", jogadorValues);
        childUpdates.put("/campeonato-jogadores/" + campKey + "/" + jogador.getId() + "/", jogadorValues);
        jogadoresReference.updateChildren(childUpdates);
    }

    public void suspenso(Jogador jogador, String timeKey, String campKey) {
        jogador.setSuspenso(true);
        jogador.setPendurado(false);
        Map<String, Object> jogadorValues = jogador.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/jogadores/" + jogador.getId() + "/", jogadorValues);
        childUpdates.put("/time-jogadores/" + timeKey + "/" + jogador.getId() + "/", jogadorValues);
        childUpdates.put("/campeonato-jogadores/" + campKey + "/" + jogador.getId() + "/", jogadorValues);
        jogadoresReference.updateChildren(childUpdates);
    }

    public void cumpriuSuspensao(Jogador jogador, String timeKey, String campKey) {
        jogador.setSuspenso(false);
        Map<String, Object> jogadorValues = jogador.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/jogadores/" + jogador.getId() + "/", jogadorValues);
        childUpdates.put("/time-jogadores/" + timeKey + "/" + jogador.getId() + "/", jogadorValues);
        childUpdates.put("/campeonato-jogadores/" + campKey + "/" + jogador.getId() + "/", jogadorValues);
        jogadoresReference.updateChildren(childUpdates);
    }

    public List<Jogador> listarArtilheiros(List<Jogador> campeonato, List<Time> times) {
        List<Jogador> result = new ArrayList<>();
        for (Jogador jogador : campeonato) {
            if (jogador.getGols() != 0) {
               result.add(jogador);
            }
        }
        Jogador maisGols;
        List<Jogador>result3 = new ArrayList<>();
        int j = result.size();
        for (int i = 0; i < j; i++) {
            maisGols = new Jogador();
            for (Jogador jogador : result) {
                if (jogador.getGols() > maisGols.getGols()) {
                    maisGols = jogador;
                } else if (jogador.getGols() == maisGols.getGols()) {
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    Date dataJogador, dataMaisGols;
                    try {
                        dataJogador = format.parse(jogador.getDataNasc());
                        dataMaisGols = format.parse(maisGols.getDataNasc());
                        if (dataJogador.before(dataMaisGols)) {
                            maisGols = jogador;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            result3.add(maisGols);
            result.remove(maisGols);
        }
        List<Jogador> result2 = new ArrayList<>();
        for (Jogador jogador : result3) {
            jogador = configurar(times, jogador);
            result2.add(jogador);
        }
        return result2;

    }

    public Jogador artilheiro(List<Jogador> campeonato,List<Time>times) {
        List<Jogador> result = new ArrayList<>();
        for (Jogador jogador : campeonato) {
            if (jogador.getGols() != 0) {
                result.add(jogador);
            }
        }
        Jogador maisGols;
        List<Jogador>result3 = new ArrayList<>();
        int j = result.size();
        for (int i = 0; i < j; i++) {
            maisGols = new Jogador();
            for (Jogador jogador : result) {
                if (jogador.getGols() > maisGols.getGols()) {
                    maisGols = jogador;
                } else if (jogador.getGols() == maisGols.getGols()) {
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    Date dataJogador, dataMaisGols;
                    try {
                        dataJogador = format.parse(jogador.getDataNasc());
                        dataMaisGols = format.parse(maisGols.getDataNasc());
                        if (dataJogador.before(dataMaisGols)) {
                            maisGols = jogador;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            result3.add(maisGols);
            result.remove(maisGols);
        }
        List<Jogador> result2 = new ArrayList<>();
        for (Jogador jogador : result3) {
            jogador = configurar(times, jogador);
            result2.add(jogador);
        }
        return result2.get(0);
    }

    public List<Jogador> listarPendurados(List<Jogador> campeonato, List<Time> times) {
        List<Jogador> result = new ArrayList<>();

        for (Jogador jogador : campeonato) {
            if (jogador.isPendurado()) {
                result.add(jogador);
            }
        }
        for (Jogador jogador:result){
            jogador = configurar(times,jogador);
        }
        return result;
    }

    public List<Jogador> listarSuspensosCamp(List<Jogador> campeonato, List<Time> times) {
        List<Jogador> result = new ArrayList<>();
        for (Jogador jogador : campeonato) {
            if (jogador.isSuspenso()) {
                result.add(jogador);
            }
        }
        for (Jogador jogador : result) {
            jogador = configurar(times, jogador);
        }
        return result;
    }

    public List<Jogador> listarSuspensosTime(List<Jogador> time) {
        List<Jogador> result = new ArrayList<>();
        for (Jogador jogador : time) {
            if (jogador.isSuspenso()) {
                result.add(jogador);
            }
        }
        return result;
    }


}
