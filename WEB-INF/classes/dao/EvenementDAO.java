package dao;

import java.sql.SQLException;
import java.util.List;

import modele.Evenement;
import modele.EvenementInscrit;

public interface EvenementDAO {
    List<Evenement> getAllEvenements();

    boolean insertAvecOrganisateur(Evenement evenement, long idFanfaron);

    List<EvenementInscrit> getEvenementsInscritsByFanfaron(long idFanfaron);

    Evenement getById(int id) throws SQLException;

    boolean deleteEvenement(int id);
}
