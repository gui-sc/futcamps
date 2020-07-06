package model.dao;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.bean.Time;

public class TimeDAO {

    private DatabaseReference timeReference = FirebaseDatabase.getInstance().getReference();

    public TimeDAO() {

    }

    public String inserir(Time time, String campKey) {
        String key = timeReference.child("times").push().getKey();
        time.setId(key);
        timeReference.child("times").child(key).setValue(time);
        timeReference.child("campeonato-times").child(campKey).child(key).setValue(time);
        return key;
    }

    public void excluir(String id, String campKey) {
        timeReference.child("campeonato-times").child(campKey).child(id).removeValue();
        timeReference.child("time-jogadores").child(id).removeValue();
    }

    public void alterar(Time time, String campKey) {
        Map<String, Object> timeValues = time.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/times/" + time.getId() + "/", timeValues);
        childUpdates.put("/campeonato-times/" + campKey + "/" + time.getId() + "/", timeValues);
        timeReference.updateChildren(childUpdates);
    }

    public void numGrupo(Time time, String campKey) {
        Map<String, Object> timeValues = time.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/times/" + time.getId() + "/", timeValues);
        childUpdates.put("/campeonato-times/" + campKey + "/" + time.getId() + "/", timeValues);
        timeReference.updateChildren(childUpdates);
    }

    public void novaPartida(Time time, String campKey) {
        Map<String, Object> timeValues = time.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/times/" + time.getId() + "/", timeValues);
        childUpdates.put("/campeonato-times/" + campKey + "/" + time.getId() + "/", timeValues);
        timeReference.updateChildren(childUpdates);
    }

    public void eliminar(Time time, String campKey) {
        time.setEliminado(true);
        Map<String, Object> timeValues = time.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/times/" + time.getId() + "/", timeValues);
        childUpdates.put("/campeonato-times/" + campKey + "/" + time.getId() + "/", timeValues);
        timeReference.updateChildren(childUpdates);
    }

    public void primeiro(Time time, String campKey) {
        time.setPrimeiro(true);
        Map<String, Object> timeValues = time.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/times/" + time.getId() + "/", timeValues);
        childUpdates.put("/campeonato-times/" + campKey + "/" + time.getId() + "/", timeValues);
        timeReference.updateChildren(childUpdates);
    }

    public List<Time> listarNaoCabecasDeChave(List<Time> campeonato) {
        List<Time> result = new ArrayList<>();
        for (Time time : campeonato) {
            if (!time.isCabecaDeChave()) {
                result.add(time);
            }
        }
        return result;
    }

    public List<Time> listarCabecasDeChave(List<Time> campeonato) {
        List<Time> result = new ArrayList<>();
        for (Time time : campeonato) {
            if (time.isCabecaDeChave()) {
                result.add(time);
            }
        }
        return result;
    }

    public List<Time> listarGrupoSemCC(int grupo, List<Time> campeonato) {
        List<Time> result = new ArrayList<>();
        for (Time time : campeonato) {
            if (time.getGrupo() == grupo && !time.isCabecaDeChave()) {
                result.add(time);
            }
        }
        return result;
    }

    public List<Time> listarGrupoComCC(int grupo, List<Time> campeonato) {
        List<Time> result = new ArrayList<>();
        List<Time> result2 = new ArrayList<>();
        for (Time time : campeonato) {
            if (time.getGrupo() == grupo) {
                result.add(time);
            }
        }
        if (result.size()==4) {
            Time time1, time2, time3, time4;
            Time maisPontos1 = new Time();
            maisPontos1.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos1.getPontos()) {
                    maisPontos1 = time;
                } else if (time.getPontos() == maisPontos1.getPontos()) {
                    if (time.getSaldo() > maisPontos1.getSaldo()) {
                        maisPontos1 = time;
                    } else if (time.getSaldo() == maisPontos1.getSaldo()) {
                        if (time.getGolsPro() > maisPontos1.getGolsPro()) {
                            maisPontos1 = time;
                        }
                    }
                }
            }
            time1 = maisPontos1;
            result.remove(maisPontos1);
            Time maisPontos2 = new Time();
            maisPontos2.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos2.getPontos()) {
                    maisPontos2 = time;
                } else if (time.getPontos() == maisPontos2.getPontos()) {
                    if (time.getSaldo() > maisPontos2.getSaldo()) {
                        maisPontos2 = time;
                    } else if (time.getSaldo() == maisPontos2.getSaldo()) {
                        if (time.getGolsPro() > maisPontos2.getGolsPro()) {
                            maisPontos2 = time;
                        }
                    }
                }
            }
            time2 = maisPontos2;
            result.remove(maisPontos2);
            Time maisPontos3 = new Time();
            maisPontos3.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos3.getPontos()) {
                    maisPontos3 = time;
                } else if (time.getPontos() == maisPontos3.getPontos()) {
                    if (time.getSaldo() > maisPontos3.getSaldo()) {
                        maisPontos3 = time;
                    } else if (time.getSaldo() == maisPontos3.getSaldo()) {
                        if (time.getGolsPro() > maisPontos3.getGolsPro()) {
                            maisPontos3 = time;
                        }
                    }
                }
            }
            time3 = maisPontos3;
            result.remove(maisPontos3);
            Time maisPontos4 = new Time();
            maisPontos4.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos4.getPontos()) {
                    maisPontos4 = time;
                } else if (time.getPontos() == maisPontos4.getPontos()) {
                    if (time.getSaldo() > maisPontos4.getSaldo()) {
                        maisPontos4 = time;
                    } else if (time.getSaldo() == maisPontos4.getSaldo()) {
                        if (time.getGolsPro() > maisPontos4.getGolsPro()) {
                            maisPontos4 = time;
                        }
                    }
                }
            }
            time4 = maisPontos4;
            result.remove(maisPontos4);
            result2.add(time1);
            result2.add(time2);
            result2.add(time3);
            result2.add(time4);
        }else if (result.size()==3){
            Time time1, time2, time3;
            Time maisPontos1 = new Time();
            maisPontos1.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos1.getPontos()) {
                    maisPontos1 = time;
                } else if (time.getPontos() == maisPontos1.getPontos()) {
                    if (time.getSaldo() > maisPontos1.getSaldo()) {
                        maisPontos1 = time;
                    } else if (time.getSaldo() == maisPontos1.getSaldo()) {
                        if (time.getGolsPro() > maisPontos1.getGolsPro()) {
                            maisPontos1 = time;
                        }
                    }
                }
            }
            time1 = maisPontos1;
            result.remove(maisPontos1);
            Time maisPontos2 = new Time();
            maisPontos2.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos2.getPontos()) {
                    maisPontos2 = time;
                } else if (time.getPontos() == maisPontos2.getPontos()) {
                    if (time.getSaldo() > maisPontos2.getSaldo()) {
                        maisPontos2 = time;
                    } else if (time.getSaldo() == maisPontos2.getSaldo()) {
                        if (time.getGolsPro() > maisPontos2.getGolsPro()) {
                            maisPontos2 = time;
                        }
                    }
                }
            }
            time2 = maisPontos2;
            result.remove(maisPontos2);
            Time maisPontos3 = new Time();
            maisPontos3.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos3.getPontos()) {
                    maisPontos3 = time;
                } else if (time.getPontos() == maisPontos3.getPontos()) {
                    if (time.getSaldo() > maisPontos3.getSaldo()) {
                        maisPontos3 = time;
                    } else if (time.getSaldo() == maisPontos3.getSaldo()) {
                        if (time.getGolsPro() > maisPontos3.getGolsPro()) {
                            maisPontos3 = time;
                        }
                    }
                }
            }
            time3 = maisPontos3;
            result.remove(maisPontos3);
            result2.add(time1);
            result2.add(time2);
            result2.add(time3);
        }else if (result.size() == 5){
            Time time1, time2, time3, time4,time5;
            Time maisPontos1 = new Time();
            maisPontos1.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos1.getPontos()) {
                    maisPontos1 = time;
                } else if (time.getPontos() == maisPontos1.getPontos()) {
                    if (time.getSaldo() > maisPontos1.getSaldo()) {
                        maisPontos1 = time;
                    } else if (time.getSaldo() == maisPontos1.getSaldo()) {
                        if (time.getGolsPro() > maisPontos1.getGolsPro()) {
                            maisPontos1 = time;
                        }
                    }
                }
            }
            time1 = maisPontos1;
            result.remove(maisPontos1);
            Time maisPontos2 = new Time();
            maisPontos2.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos2.getPontos()) {
                    maisPontos2 = time;
                } else if (time.getPontos() == maisPontos2.getPontos()) {
                    if (time.getSaldo() > maisPontos2.getSaldo()) {
                        maisPontos2 = time;
                    } else if (time.getSaldo() == maisPontos2.getSaldo()) {
                        if (time.getGolsPro() > maisPontos2.getGolsPro()) {
                            maisPontos2 = time;
                        }
                    }
                }
            }
            time2 = maisPontos2;
            result.remove(maisPontos2);
            Time maisPontos3 = new Time();
            maisPontos3.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos3.getPontos()) {
                    maisPontos3 = time;
                } else if (time.getPontos() == maisPontos3.getPontos()) {
                    if (time.getSaldo() > maisPontos3.getSaldo()) {
                        maisPontos3 = time;
                    } else if (time.getSaldo() == maisPontos3.getSaldo()) {
                        if (time.getGolsPro() > maisPontos3.getGolsPro()) {
                            maisPontos3 = time;
                        }
                    }
                }
            }
            time3 = maisPontos3;
            result.remove(maisPontos3);
            Time maisPontos4 = new Time();
            maisPontos4.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos4.getPontos()) {
                    maisPontos4 = time;
                } else if (time.getPontos() == maisPontos4.getPontos()) {
                    if (time.getSaldo() > maisPontos4.getSaldo()) {
                        maisPontos4 = time;
                    } else if (time.getSaldo() == maisPontos4.getSaldo()) {
                        if (time.getGolsPro() > maisPontos4.getGolsPro()) {
                            maisPontos4 = time;
                        }
                    }
                }
            }
            time4 = maisPontos4;
            result.remove(maisPontos4);
            Time maisPontos5 = new Time();
            maisPontos5.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos5.getPontos()) {
                    maisPontos5 = time;
                } else if (time.getPontos() == maisPontos5.getPontos()) {
                    if (time.getSaldo() > maisPontos5.getSaldo()) {
                        maisPontos5 = time;
                    } else if (time.getSaldo() == maisPontos5.getSaldo()) {
                        if (time.getGolsPro() > maisPontos5.getGolsPro()) {
                            maisPontos5 = time;
                        }
                    }
                }
            }
            time5 = maisPontos5;
            result.remove(maisPontos5);
            result2.add(time1);
            result2.add(time2);
            result2.add(time3);
            result2.add(time4);
            result2.add(time5);
        }else if (result.size() == 6){
            Time time1, time2, time3, time4,time5,time6;
            Time maisPontos1 = new Time();
            maisPontos1.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos1.getPontos()) {
                    maisPontos1 = time;
                } else if (time.getPontos() == maisPontos1.getPontos()) {
                    if (time.getSaldo() > maisPontos1.getSaldo()) {
                        maisPontos1 = time;
                    } else if (time.getSaldo() == maisPontos1.getSaldo()) {
                        if (time.getGolsPro() > maisPontos1.getGolsPro()) {
                            maisPontos1 = time;
                        }
                    }
                }
            }
            time1 = maisPontos1;
            result.remove(maisPontos1);
            Time maisPontos2 = new Time();
            maisPontos2.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos2.getPontos()) {
                    maisPontos2 = time;
                } else if (time.getPontos() == maisPontos2.getPontos()) {
                    if (time.getSaldo() > maisPontos2.getSaldo()) {
                        maisPontos2 = time;
                    } else if (time.getSaldo() == maisPontos2.getSaldo()) {
                        if (time.getGolsPro() > maisPontos2.getGolsPro()) {
                            maisPontos2 = time;
                        }
                    }
                }
            }
            time2 = maisPontos2;
            result.remove(maisPontos2);
            Time maisPontos3 = new Time();
            maisPontos3.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos3.getPontos()) {
                    maisPontos3 = time;
                } else if (time.getPontos() == maisPontos3.getPontos()) {
                    if (time.getSaldo() > maisPontos3.getSaldo()) {
                        maisPontos3 = time;
                    } else if (time.getSaldo() == maisPontos3.getSaldo()) {
                        if (time.getGolsPro() > maisPontos3.getGolsPro()) {
                            maisPontos3 = time;
                        }
                    }
                }
            }
            time3 = maisPontos3;
            result.remove(maisPontos3);
            Time maisPontos4 = new Time();
            maisPontos4.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos4.getPontos()) {
                    maisPontos4 = time;
                } else if (time.getPontos() == maisPontos4.getPontos()) {
                    if (time.getSaldo() > maisPontos4.getSaldo()) {
                        maisPontos4 = time;
                    } else if (time.getSaldo() == maisPontos4.getSaldo()) {
                        if (time.getGolsPro() > maisPontos4.getGolsPro()) {
                            maisPontos4 = time;
                        }
                    }
                }
            }
            time4 = maisPontos4;
            result.remove(maisPontos4);
            Time maisPontos5 = new Time();
            maisPontos5.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos5.getPontos()) {
                    maisPontos5 = time;
                } else if (time.getPontos() == maisPontos5.getPontos()) {
                    if (time.getSaldo() > maisPontos5.getSaldo()) {
                        maisPontos5 = time;
                    } else if (time.getSaldo() == maisPontos5.getSaldo()) {
                        if (time.getGolsPro() > maisPontos5.getGolsPro()) {
                            maisPontos5 = time;
                        }
                    }
                }
            }
            time5 = maisPontos5;
            result.remove(maisPontos5);
            Time maisPontos6 = new Time();
            maisPontos6.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos6.getPontos()) {
                    maisPontos6 = time;
                } else if (time.getPontos() == maisPontos6.getPontos()) {
                    if (time.getSaldo() > maisPontos6.getSaldo()) {
                        maisPontos6 = time;
                    } else if (time.getSaldo() == maisPontos6.getSaldo()) {
                        if (time.getGolsPro() > maisPontos6.getGolsPro()) {
                            maisPontos6 = time;
                        }
                    }
                }
            }
            time6 = maisPontos6;
            result.remove(maisPontos6);
            result2.add(time1);
            result2.add(time2);
            result2.add(time3);
            result2.add(time4);
            result2.add(time5);
            result2.add(time6);
        }else if (result.size() == 8){
            Time time1, time2, time3, time4,time5,time6,time7,time8;
            Time maisPontos1 = new Time();
            maisPontos1.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos1.getPontos()) {
                    maisPontos1 = time;
                } else if (time.getPontos() == maisPontos1.getPontos()) {
                    if (time.getSaldo() > maisPontos1.getSaldo()) {
                        maisPontos1 = time;
                    } else if (time.getSaldo() == maisPontos1.getSaldo()) {
                        if (time.getGolsPro() > maisPontos1.getGolsPro()) {
                            maisPontos1 = time;
                        }
                    }
                }
            }
            time1 = maisPontos1;
            result.remove(maisPontos1);
            Time maisPontos2 = new Time();
            maisPontos2.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos2.getPontos()) {
                    maisPontos2 = time;
                } else if (time.getPontos() == maisPontos2.getPontos()) {
                    if (time.getSaldo() > maisPontos2.getSaldo()) {
                        maisPontos2 = time;
                    } else if (time.getSaldo() == maisPontos2.getSaldo()) {
                        if (time.getGolsPro() > maisPontos2.getGolsPro()) {
                            maisPontos2 = time;
                        }
                    }
                }
            }
            time2 = maisPontos2;
            result.remove(maisPontos2);
            Time maisPontos3 = new Time();
            maisPontos3.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos3.getPontos()) {
                    maisPontos3 = time;
                } else if (time.getPontos() == maisPontos3.getPontos()) {
                    if (time.getSaldo() > maisPontos3.getSaldo()) {
                        maisPontos3 = time;
                    } else if (time.getSaldo() == maisPontos3.getSaldo()) {
                        if (time.getGolsPro() > maisPontos3.getGolsPro()) {
                            maisPontos3 = time;
                        }
                    }
                }
            }
            time3 = maisPontos3;
            result.remove(maisPontos3);
            Time maisPontos4 = new Time();
            maisPontos4.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos4.getPontos()) {
                    maisPontos4 = time;
                } else if (time.getPontos() == maisPontos4.getPontos()) {
                    if (time.getSaldo() > maisPontos4.getSaldo()) {
                        maisPontos4 = time;
                    } else if (time.getSaldo() == maisPontos4.getSaldo()) {
                        if (time.getGolsPro() > maisPontos4.getGolsPro()) {
                            maisPontos4 = time;
                        }
                    }
                }
            }
            time4 = maisPontos4;
            result.remove(maisPontos4);
            Time maisPontos5 = new Time();
            maisPontos5.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos5.getPontos()) {
                    maisPontos5 = time;
                } else if (time.getPontos() == maisPontos5.getPontos()) {
                    if (time.getSaldo() > maisPontos5.getSaldo()) {
                        maisPontos5 = time;
                    } else if (time.getSaldo() == maisPontos5.getSaldo()) {
                        if (time.getGolsPro() > maisPontos5.getGolsPro()) {
                            maisPontos5 = time;
                        }
                    }
                }
            }
            time5 = maisPontos5;
            result.remove(maisPontos5);
            Time maisPontos6 = new Time();
            maisPontos6.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos6.getPontos()) {
                    maisPontos6 = time;
                } else if (time.getPontos() == maisPontos6.getPontos()) {
                    if (time.getSaldo() > maisPontos6.getSaldo()) {
                        maisPontos6 = time;
                    } else if (time.getSaldo() == maisPontos6.getSaldo()) {
                        if (time.getGolsPro() > maisPontos6.getGolsPro()) {
                            maisPontos6 = time;
                        }
                    }
                }
            }
            time6 = maisPontos6;
            result.remove(maisPontos6);
            Time maisPontos7 = new Time();
            maisPontos7.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos7.getPontos()) {
                    maisPontos7 = time;
                } else if (time.getPontos() == maisPontos7.getPontos()) {
                    if (time.getSaldo() > maisPontos7.getSaldo()) {
                        maisPontos7 = time;
                    } else if (time.getSaldo() == maisPontos7.getSaldo()) {
                        if (time.getGolsPro() > maisPontos7.getGolsPro()) {
                            maisPontos7 = time;
                        }
                    }
                }
            }
            time7 = maisPontos7;
            result.remove(maisPontos7);
            Time maisPontos8 = new Time();
            maisPontos8.setPontos(-1);
            for (Time time : result) {
                if (time.getPontos() > maisPontos8.getPontos()) {
                    maisPontos8 = time;
                } else if (time.getPontos() == maisPontos8.getPontos()) {
                    if (time.getSaldo() > maisPontos8.getSaldo()) {
                        maisPontos8 = time;
                    } else if (time.getSaldo() == maisPontos8.getSaldo()) {
                        if (time.getGolsPro() > maisPontos8.getGolsPro()) {
                            maisPontos8 = time;
                        }
                    }
                }
            }
            time8 = maisPontos8;
            result.remove(maisPontos8);
            result2.add(time1);
            result2.add(time2);
            result2.add(time3);
            result2.add(time4);
            result2.add(time5);
            result2.add(time6);
            result2.add(time7);
            result.add(time8);
        }
        return result2;
    }

    public List<Time> listarClassificados(List<Time> campeonato) {
        List<Time> result = new ArrayList<>();
        for (Time time : campeonato) {
            if (!time.isEliminado()) {
                result.add(time);
            }
        }
        return result;
    }

}
