package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import modele.Evenement;
import modele.EvenementInscrit;

/**
 * DAO JDBC - EVENEMENT
 *
 * Responsabilites :
 * - Lire, creer, modifier et supprimer les evenements
 * - Enregistrer l'organisateur lors de la creation d'un evenement
 * - Recuperer les evenements auxquels un fanfaron est inscrit
 * - Mapper les lignes SQL vers les objets du modele
 *
 * Tables utilisees :
 * - evenement
 * - organisation_evenement
 * - inscription
 * - instrument
 */
public class EvenementJDBCDAO implements EvenementDAO {
    // Gestionnaire centralise des connexions a la base de donnees
    private final DbConnectionManager dbManager;

    /**
     * Constructeur avec injection du gestionnaire de connexions.
     */
    public EvenementJDBCDAO(DbConnectionManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Ouvre une connexion via le gestionnaire partage.
     */
    private Connection getConnection() throws SQLException {
        return dbManager.getConnection();
    }

    /**
     * Recupere tous les evenements tries du plus recent au plus ancien.
     */
    public List<Evenement> getAllEvenements() {
        String sql = "SELECT id, type_evenement, nom, horodatage, duree, lieu, description FROM evenement ORDER BY horodatage DESC";
        List<Evenement> evenements = new ArrayList<>();

        try (Connection connexion = getConnection();
                Statement ps = connexion.createStatement();
                ResultSet rs = ps.executeQuery(sql)) {

            while (rs.next()) {
                evenements.add(mapEvenement(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return evenements;
    }

    /**
     * Cree un evenement et ajoute le lien avec le fanfaron organisateur.
     *
     * La transaction garantit que l'evenement et son organisation sont enregistres ensemble.
     */
    public boolean insertAvecOrganisateur(Evenement evenement, long idFanfaron) {
        String sqlEvenement = "INSERT INTO evenement (type_evenement, nom, horodatage, duree, lieu, description) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlOrganisation = "INSERT INTO organisation_evenement (id_fanfaron, id_evenement) VALUES (?, ?)";

        try (Connection connexion = getConnection()) {
            // Debut de transaction manuelle : deux INSERT doivent reussir ensemble
            connexion.setAutoCommit(false);

            try (PreparedStatement ps = connexion.prepareStatement(sqlEvenement, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, evenement.getTypeEvenement());
                ps.setString(2, evenement.getNom());
                ps.setTimestamp(3, evenement.getHorodatage());
                ps.setInt(4, evenement.getDuree());
                ps.setString(5, evenement.getLieu());
                ps.setString(6, evenement.getDescription());

                int count = ps.executeUpdate();
                if (count == 0) {
                    // Aucun evenement cree : annulation de la transaction
                    connexion.rollback();
                    return false;
                }

                // Recuperation de l'identifiant genere pour l'utiliser dans organisation_evenement
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        evenement.setId(keys.getInt(1));
                    }
                }
            }

            try (PreparedStatement ps = connexion.prepareStatement(sqlOrganisation)) {
                ps.setLong(1, idFanfaron);
                ps.setInt(2, evenement.getId());
                ps.executeUpdate();
            }

            // Validation finale de la transaction
            connexion.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupere les evenements auxquels un fanfaron participe deja.
     *
     * Le resultat combine les donnees de l'evenement avec l'instrument et le statut.
     */
    public List<EvenementInscrit> getEvenementsInscritsByFanfaron(long idFanfaron) {
        String sql = "SELECT e.id, e.type_evenement, e.nom, e.horodatage, e.duree, e.lieu, e.description, "
                + "i.nom AS instrument, ins.statut "
                + "FROM inscription ins "
                + "JOIN evenement e ON e.id = ins.id_evenement "
                + "JOIN instrument i ON i.id = ins.id_instrument "
                + "WHERE ins.id_fanfaron = ? "
                + "ORDER BY e.horodatage ASC, e.nom";
        List<EvenementInscrit> evenements = new ArrayList<>();

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setLong(1, idFanfaron);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EvenementInscrit evenement = new EvenementInscrit();
                    evenement.setId(rs.getInt("id"));
                    evenement.setTypeEvenement(rs.getString("type_evenement"));
                    evenement.setNom(rs.getString("nom"));
                    evenement.setHorodatage(rs.getTimestamp("horodatage"));
                    evenement.setDuree(rs.getInt("duree"));
                    evenement.setLieu(rs.getString("lieu"));
                    evenement.setDescription(rs.getString("description"));
                    evenement.setInstrument(rs.getString("instrument"));
                    evenement.setStatut(rs.getString("statut"));
                    evenements.add(evenement);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return evenements;
    }

    /**
     * Recupere un evenement par son identifiant.
     */
    public Evenement getById(int id) throws SQLException {
        String sql = "SELECT id, type_evenement, nom, horodatage, duree, lieu, description FROM evenement WHERE id = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapEvenement(rs) : null;
            }
        } catch (Exception e) {
            if (e instanceof SQLException) {
                throw (SQLException) e;
            }
            throw new SQLException("Erreur lors de la recuperation de l'evenement", e);
        }
    }

    /**
     * Met a jour les champs modifiables d'un evenement.
     */
    public boolean updateEvenement(Evenement evenement) {
        String sql = "UPDATE evenement SET type_evenement = ?, nom = ?, horodatage = ?, duree = ?, lieu = ?, description = ? WHERE id = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, evenement.getTypeEvenement());
            ps.setString(2, evenement.getNom());
            ps.setTimestamp(3, evenement.getHorodatage());
            ps.setInt(4, evenement.getDuree());
            ps.setString(5, evenement.getLieu());
            ps.setString(6, evenement.getDescription());
            ps.setInt(7, evenement.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime un evenement par son identifiant.
     */
    public boolean deleteEvenement(int id) {
        String sql = "DELETE FROM evenement WHERE id = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Convertit la ligne courante du ResultSet en objet Evenement.
     */
    private Evenement mapEvenement(ResultSet rs) throws SQLException {
        Evenement evenement = new Evenement();
        evenement.setId(rs.getInt("id"));
        evenement.setTypeEvenement(rs.getString("type_evenement"));
        evenement.setNom(rs.getString("nom"));
        evenement.setHorodatage(rs.getTimestamp("horodatage"));
        evenement.setDuree(rs.getInt("duree"));
        evenement.setLieu(rs.getString("lieu"));
        evenement.setDescription(rs.getString("description"));
        return evenement;
    }
}
