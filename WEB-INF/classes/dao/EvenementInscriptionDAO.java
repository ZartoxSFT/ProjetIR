package dao;

import java.util.List;

import modele.InscriptionDetail;

public interface EvenementInscriptionDAO {
    boolean upsertInscription(long idFanfaron, int idEvenement, int idInstrument, String statut);

    List<InscriptionDetail> getInscriptionsByEvenement(int idEvenement);
}
