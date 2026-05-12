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

public class EvenementJDBCDAO implements EvenementDAO {
    private final DbConnectionManager dbManager;

    public EvenementJDBCDAO(DbConnectionManager dbManager) {
        this.dbManager = dbManager;
    }

    private Connection getConnection() throws SQLException {
        return dbManager.getConnection();
    }

    public List<Evenement> getAllEvenements() {
        String sql = "SELECT id, nom, horodatage, duree, lieu, description FROM evenement ORDER BY horodatage DESC";
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

    public boolean insertAvecOrganisateur(Evenement evenement, long idFanfaron) {
        String sqlEvenement = "INSERT INTO evenement (nom, horodatage, duree, lieu, description) VALUES (?, ?, ?, ?, ?)";
        String sqlOrganisation = "INSERT INTO organisation_evenement (id_fanfaron, id_evenement) VALUES (?, ?)";

        try (Connection connexion = getConnection()) {
            connexion.setAutoCommit(false);

            try (PreparedStatement ps = connexion.prepareStatement(sqlEvenement, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, evenement.getNom());
                ps.setTimestamp(2, evenement.getHorodatage());
                ps.setInt(3, evenement.getDuree());
                ps.setString(4, evenement.getLieu());
                ps.setString(5, evenement.getDescription());

                int count = ps.executeUpdate();
                if (count == 0) {
                    connexion.rollback();
                    return false;
                }

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

            connexion.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<EvenementInscrit> getEvenementsInscritsByFanfaron(long idFanfaron) {
        String sql = "SELECT e.id, e.nom, e.horodatage, e.duree, e.lieu, e.description, "
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

    public Evenement getById(int id) throws SQLException {
        String sql = "SELECT id, nom, horodatage, duree, lieu, description FROM evenement WHERE id = ?";

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

    public boolean updateEvenement(Evenement evenement) {
        String sql = "UPDATE evenement SET nom = ?, horodatage = ?, duree = ?, lieu = ?, description = ? WHERE id = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, evenement.getNom());
            ps.setTimestamp(2, evenement.getHorodatage());
            ps.setInt(3, evenement.getDuree());
            ps.setString(4, evenement.getLieu());
            ps.setString(5, evenement.getDescription());
            ps.setInt(6, evenement.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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

    private Evenement mapEvenement(ResultSet rs) throws SQLException {
        Evenement evenement = new Evenement();
        evenement.setId(rs.getInt("id"));
        evenement.setNom(rs.getString("nom"));
        evenement.setHorodatage(rs.getTimestamp("horodatage"));
        evenement.setDuree(rs.getInt("duree"));
        evenement.setLieu(rs.getString("lieu"));
        evenement.setDescription(rs.getString("description"));
        return evenement;
    }
}
