package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import modele.InscriptionDetail;

/**
 * DAO JDBC - INSCRIPTION AUX EVENEMENTS
 *
 * Responsabilites :
 * - Creer ou mettre a jour une inscription a un evenement
 * - Supprimer une inscription
 * - Charger les participants d'un evenement avec leurs informations detaillees
 *
 * Table principale : inscription
 */
public class EvenementInscriptionJDBCDAO implements EvenementInscriptionDAO {
    // Gestionnaire centralise des connexions a la base de donnees
    private final DbConnectionManager dbManager;

    /**
     * Constructeur avec injection du gestionnaire de connexions.
     */
    public EvenementInscriptionJDBCDAO(DbConnectionManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Ouvre une connexion via le gestionnaire partage.
     */
    private Connection getConnection() throws SQLException {
        return dbManager.getConnection();
    }

    /**
     * Cree une inscription ou met a jour l'inscription deja existante.
     *
     * Le ON CONFLICT evite les doublons pour le couple fanfaron/evenement.
     */
    public boolean upsertInscription(long idFanfaron, int idEvenement, int idInstrument, String statut) {
        String sql = "INSERT INTO inscription (id_fanfaron, id_evenement, id_instrument, statut) "
                + "VALUES (?, ?, ?, ?) "
                + "ON CONFLICT (id_fanfaron, id_evenement) "
                + "DO UPDATE SET id_instrument = EXCLUDED.id_instrument, statut = EXCLUDED.statut";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setLong(1, idFanfaron);
            ps.setInt(2, idEvenement);
            ps.setInt(3, idInstrument);
            ps.setString(4, statut);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime l'inscription d'un fanfaron a un evenement.
     */
    public boolean deleteInscription(long idFanfaron, int idEvenement) {
        String sql = "DELETE FROM inscription WHERE id_fanfaron = ? AND id_evenement = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setLong(1, idFanfaron);
            ps.setInt(2, idEvenement);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupere les inscriptions detaillees d'un evenement.
     *
     * Le tri regroupe les participants par instrument puis par statut de participation.
     */
    public List<InscriptionDetail> getInscriptionsByEvenement(int idEvenement) {
        String sql = "SELECT f.id AS id_fanfaron, f.nom_fanfaron, f.prenom, f.nom, "
                + "i.nom AS instrument, ins.statut "
                + "FROM inscription ins "
                + "JOIN fanfaron f ON f.id = ins.id_fanfaron "
                + "JOIN instrument i ON i.id = ins.id_instrument "
                + "WHERE ins.id_evenement = ? "
                + "ORDER BY i.nom, "
                + "CASE ins.statut "
                + "WHEN 'present' THEN 1 "
                + "WHEN 'incertain' THEN 2 "
                + "WHEN 'absent' THEN 3 "
                + "ELSE 4 END, "
                + "f.nom_fanfaron";

        List<InscriptionDetail> inscriptions = new ArrayList<>();

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setInt(1, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Mapping manuel car le resultat combine plusieurs tables
                    InscriptionDetail detail = new InscriptionDetail();
                    detail.setIdFanfaron(rs.getInt("id_fanfaron"));
                    detail.setNomFanfaron(rs.getString("nom_fanfaron"));
                    detail.setPrenom(rs.getString("prenom"));
                    detail.setNom(rs.getString("nom"));
                    detail.setInstrument(rs.getString("instrument"));
                    detail.setStatut(rs.getString("statut"));
                    inscriptions.add(detail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inscriptions;
    }
}
