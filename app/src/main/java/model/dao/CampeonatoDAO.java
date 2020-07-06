package model.dao;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.bean.Campeonato;
import model.bean.Notification;
import model.bean.Usuario;

public class CampeonatoDAO {

    private DatabaseReference campReference = FirebaseDatabase.getInstance().getReference();

    public CampeonatoDAO() {
    }

    public String inserir(Campeonato camp) {
        String key = campReference.child("campeonatos").push().getKey();
        camp.setId(key);
        campReference.child("campeonatos").child(key).setValue(camp);
        campReference.child("user-campeonatos").child(camp.getUid()).child(key).setValue(camp);
        return key;
    }

    public void excluir(Campeonato camp, String uid, List<Usuario> usuarios) {
        campReference.child("campeonatos").child(camp.getId()).removeValue();
        campReference.child("user-campeonatos").child(uid).child(camp.getId()).removeValue();
        campReference.child("campeonato-times").child(camp.getId()).removeValue();
        Notification notification = new Notification();
        notification.setTitulo("Campeonato Apagado");
        notification.setMensagem(camp.getNome()+" foi apagado!");
        for (Usuario usuario : usuarios) {
            FirebaseDatabase.getInstance().getReference().child("notificacao").child(usuario.getToken()).removeValue();
            FirebaseDatabase.getInstance().getReference().child("notificacao").child(usuario.getToken()).setValue(notification);
            campReference.child("user-segue").child(usuario.getId()).child(camp.getId()).removeValue();
        }
        campReference.child("campeonato-seguidores").child(camp.getId()).removeValue();

    }

    public void alterar(Campeonato camp, List<Usuario> usuarios) {
        Map<String, Object> campValues = camp.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/campeonatos/" + camp.getId() + "/", campValues);
        childUpdates.put("/user-campeonatos/" + camp.getUid() + "/" + camp.getId() + "/", campValues);
        for (Usuario usuario : usuarios) {
            childUpdates.put("/user-segue/" + usuario.getId() + "/" + camp.getId() + "/", campValues);
        }
        campReference.updateChildren(childUpdates);
    }

    public void cabecaDeChaveMais(Campeonato camp, List<Usuario> usuarios) {
        camp.setCabecasDeChave(camp.getCabecasDeChave() + 1);
        Map<String, Object> campValues = camp.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/campeonatos/" + camp.getId() + "/", campValues);
        childUpdates.put("/user-campeonatos/" + camp.getUid() + "/" + camp.getId() + "/", campValues);
        for (Usuario usuario : usuarios) {
            childUpdates.put("/user-segue/" + usuario.getId() + "/" + camp.getId() + "/", campValues);
        }
        campReference.updateChildren(childUpdates);
    }
    public void cabecaDeChaveMenos(Campeonato camp, List<Usuario> usuarios) {
        camp.setCabecasDeChave(camp.getCabecasDeChave() - 1);
        Map<String, Object> campValues = camp.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/campeonatos/" + camp.getId() + "/", campValues);
        childUpdates.put("/user-campeonatos/" + camp.getUid() + "/" + camp.getId() + "/", campValues);
        for (Usuario usuario : usuarios) {
            childUpdates.put("/user-segue/" + usuario.getId() + "/" + camp.getId() + "/", campValues);
        }
        campReference.updateChildren(childUpdates);
    }

    public void finalizar(Campeonato camp, List<Usuario> usuarios,String nomeCamp) {
        camp.setFinalizado(true);
        camp.setFinal(false);
        camp.setIniciado(false);
        Map<String, Object> campValues = camp.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/campeonatos/" + camp.getId() + "/", campValues);
        childUpdates.put("/user-campeonatos/" + camp.getUid() + "/" + camp.getId() + "/", campValues);
        Notification notification = new Notification();
        notification.setTitulo("Campeonato Finalizado");
        notification.setMensagem(nomeCamp+" foi finalizado, veja o resultado!");
        Map<String,Object> values = notification.toMap();
        for (Usuario usuario : usuarios) {
            childUpdates.put("/user-segue/" + usuario.getId() + "/" + camp.getId() + "/", campValues);
            FirebaseDatabase.getInstance().getReference().child("notificacao").child(usuario.getToken()).removeValue();
            childUpdates.put("/notificacao/"+usuario.getToken()+"/",values);
        }
        campReference.updateChildren(childUpdates);
    }

    public void iniciar(Campeonato camp, List<Usuario> usuarios,String nomeCamp) {
        camp.setIniciado(true);
        Map<String, Object> campValues = camp.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/campeonatos/" + camp.getId() + "/", campValues);
        childUpdates.put("/user-campeonatos/" + camp.getUid() + "/" + camp.getId() + "/", campValues);
        Notification notification = new Notification();
        notification.setTitulo("Campeonato Iniciado");
        notification.setMensagem(nomeCamp+" foi iniciado, veja a primeira partida!");
        Map<String,Object> values = notification.toMap();
        for (Usuario usuario : usuarios) {
            childUpdates.put("/user-segue/" + usuario.getId() + "/" + camp.getId() + "/", campValues);
            FirebaseDatabase.getInstance().getReference().child("notificacao").child(usuario.getToken()).removeValue();
            childUpdates.put("/notificacao/"+usuario.getToken()+"/",values);
        }
        campReference.updateChildren(childUpdates);
    }

    public void passarDeFase(Campeonato camp, List<Usuario> usuarios) {
        Map<String, Object> campValues = camp.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/campeonatos/" + camp.getId() + "/", campValues);
        childUpdates.put("/user-campeonatos/" + camp.getUid() + "/" + camp.getId() + "/", campValues);
        for (Usuario usuario : usuarios) {
            childUpdates.put("/user-segue/" + usuario.getId() + "/" + camp.getId() + "/", campValues);
        }
        campReference.updateChildren(childUpdates);
    }

    public List<Campeonato> pesquisa(List<Campeonato> listGeral, List<Campeonato> listSegue, String filtro, String uid) {
        List<Campeonato> result = new ArrayList<>();
        for (Campeonato campeonato : listGeral) {
            if (campeonato.getId().equals(filtro) && !campeonato.getUid().equals(uid)) {
                result.add(campeonato);
            }
        }
        for (Campeonato campeonato : listSegue){
            for (Campeonato camp:result
                 ) {
                if (campeonato.getId().equals(camp.getId())){
                    result.remove(camp);
                }
            }
        }
        return result;
    }
}
