package model.dao;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.bean.Notification;
import model.bean.Partida;
import model.bean.Time;
import model.bean.Usuario;

public class PartidaDAO {
    private DatabaseReference partidaReference = FirebaseDatabase.getInstance().getReference();

    public PartidaDAO() {

    }

    public List<List<Partida>> dividirRodadas(int grupos, int times, List<Partida> partidas) {
        List<List<Partida>> rodadas = new ArrayList<>();
        if (grupos == 2) {
            if (times == 6) {
                List<Partida> rodada1 = new ArrayList<>(), rodada2 = new ArrayList<>(), rodada3 = new ArrayList<>();
                for (int i = 0; i < 6; i++) {
                    if (i < 2) {
                        rodada1.add(partidas.get(i));
                    } else if (i < 4) {
                        rodada2.add(partidas.get(i));
                    } else {
                        rodada3.add(partidas.get(i));
                    }
                }
                rodadas.add(rodada1);
                rodadas.add(rodada2);
                rodadas.add(rodada3);
            } else if (times == 8) {
                List<Partida> rodada1 = new ArrayList<>(), rodada2 = new ArrayList<>(), rodada3 = new ArrayList<>();
                for (int i = 0; i < 12; i++) {
                    if (i < 4) {
                        rodada1.add(partidas.get(i));
                    } else if (i < 8) {
                        rodada2.add(partidas.get(i));
                    } else {
                        rodada3.add(partidas.get(i));
                    }
                }
                rodadas.add(rodada1);
                rodadas.add(rodada2);
                rodadas.add(rodada3);
            } else if (times == 10) {
                List<Partida> rodada1 = new ArrayList<>();
                List<Partida> rodada2 = new ArrayList<>();
                List<Partida> rodada3 = new ArrayList<>();
                List<Partida> rodada4 = new ArrayList<>();
                List<Partida> rodada5 = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    if (i < 4) {
                        rodada1.add(partidas.get(i));
                    } else if (i < 8) {
                        rodada2.add(partidas.get(i));
                    } else if (i < 12) {
                        rodada3.add(partidas.get(i));
                    } else if (i < 16) {
                        rodada4.add(partidas.get(i));
                    } else {
                        rodada5.add(partidas.get(i));
                    }
                }
                rodadas.add(rodada1);
                rodadas.add(rodada2);
                rodadas.add(rodada3);
                rodadas.add(rodada4);
                rodadas.add(rodada5);
            } else if (times == 12) {
                List<Partida> rodada1 = new ArrayList<>();
                List<Partida> rodada2 = new ArrayList<>();
                List<Partida> rodada3 = new ArrayList<>();
                List<Partida> rodada4 = new ArrayList<>();
                List<Partida> rodada5 = new ArrayList<>();
                for (int i = 0; i < 30; i++) {
                    if (i < 6) {
                        rodada1.add(partidas.get(i));
                    } else if (i < 12) {
                        rodada2.add(partidas.get(i));
                    } else if (i < 18) {
                        rodada3.add(partidas.get(i));
                    } else if (i < 24) {
                        rodada4.add(partidas.get(i));
                    } else {
                        rodada5.add(partidas.get(i));
                    }
                }
                rodadas.add(rodada1);
                rodadas.add(rodada2);
                rodadas.add(rodada3);
                rodadas.add(rodada4);
                rodadas.add(rodada5);
            } else if (times == 16) {
                List<Partida> rodada1 = new ArrayList<>();
                List<Partida> rodada2 = new ArrayList<>();
                List<Partida> rodada3 = new ArrayList<>();
                List<Partida> rodada4 = new ArrayList<>();
                List<Partida> rodada5 = new ArrayList<>();
                List<Partida> rodada6 = new ArrayList<>();
                List<Partida> rodada7 = new ArrayList<>();
                for (int i = 0; i < 56; i++) {
                    if (i < 8) {
                        rodada1.add(partidas.get(i));
                    } else if (i < 16) {
                        rodada2.add(partidas.get(i));
                    } else if (i < 24) {
                        rodada3.add(partidas.get(i));
                    } else if (i < 32) {
                        rodada4.add(partidas.get(i));
                    } else if (i < 40) {
                        rodada5.add(partidas.get(i));
                    } else if (i < 48) {
                        rodada6.add(partidas.get(i));
                    } else {
                        rodada7.add(partidas.get(i));
                    }
                }
                rodadas.add(rodada1);
                rodadas.add(rodada2);
                rodadas.add(rodada3);
                rodadas.add(rodada4);
                rodadas.add(rodada5);
                rodadas.add(rodada6);
                rodadas.add(rodada7);
            }
        } else {
            if (times == 12) {
                List<Partida> rodada1 = new ArrayList<>();
                List<Partida> rodada2 = new ArrayList<>();
                List<Partida> rodada3 = new ArrayList<>();
                for (int i = 0; i < 12; i++) {
                    if (i < 4) {
                        rodada1.add(partidas.get(i));
                    } else if (i < 8) {
                        rodada2.add(partidas.get(i));
                    } else {
                        rodada3.add(partidas.get(i));
                    }
                }
                rodadas.add(rodada1);
                rodadas.add(rodada2);
                rodadas.add(rodada3);
            } else if (times == 16) {
                List<Partida> rodada1 = new ArrayList<>();
                List<Partida> rodada2 = new ArrayList<>();
                List<Partida> rodada3 = new ArrayList<>();
                for (int i = 0; i < 24; i++) {
                    if (i < 8) {
                        rodada1.add(partidas.get(i));
                    } else if (i < 16) {
                        rodada2.add(partidas.get(i));
                    } else {
                        rodada3.add(partidas.get(i));
                    }
                }
                rodadas.add(rodada1);
                rodadas.add(rodada2);
                rodadas.add(rodada3);
            }
        }
        return rodadas;
    }

    public Partida configurar(List<Time> times, Partida partida) {
        for (Time time : times) {
            if (time.getId().equals(partida.getIdMandante())) {
                partida.setMandante(time);
            } else if (time.getId().equals(partida.getIdVisitante())) {
                partida.setVisitante(time);
            }
        }
        return partida;
    }

    public void cadastrarPrevia(Partida partida, String campKey) {
        String key = partidaReference.child("partidas").push().getKey();
        partida.setId(key);
        partida.setIdMandante(partida.getMandante().getId());
        partida.setIdVisitante(partida.getVisitante().getId());
        partida.setNomeMandante(partida.getMandante().getNome());
        partida.setNomeVisitante(partida.getVisitante().getNome());
        partida.setMandante(null);
        partida.setVisitante(null);
        partidaReference.child("campeonato-partidas").child(campKey).child(key).setValue(partida);
    }

    public void cadastrarCompleto(Partida partida, String campKey, List<Usuario> usuarios, String nomeCamp) {
        Map<String, Object> partidaValues = partida.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/campeonato-partidas/" + campKey + "/" + partida.getId() + "/", partidaValues);
        Notification notification = new Notification();
        notification.setTitulo("Nova Partida");
        notification.setMensagem("Uma nova partida foi cadastrada em " + nomeCamp);
        Map<String, Object> values = notification.toMap();
        for (Usuario user : usuarios) {
            FirebaseDatabase.getInstance().getReference().child("notificacao").child(user.getToken()).removeValue();
            childUpdates.put("/notificacao/" + user.getToken() + "/", values);
        }
        partidaReference.updateChildren(childUpdates);
    }

    public List<Partida> listarFase(List<Partida> partidas, String fase) {
        List<Partida> novaLista = new ArrayList<>();
        for (Partida partida : partidas) {
            if (partida.getFase().contains(fase)) {
                novaLista.add(partida);
            }
        }
        return novaLista;
    }


}
