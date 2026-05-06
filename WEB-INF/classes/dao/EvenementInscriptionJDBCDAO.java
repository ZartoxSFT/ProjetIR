package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import modele.InscriptionDetail;

public class EvenementInscriptionJDBCDAO {

    private Connection getConnection() throws Exception {
        Properties props = new Properties();

        InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties");
        if (input == null) {
            throw new Exception("Fichier db.properties introuvable");
        }

        props.load(input);

        Class.forName("org.postgresql.Driver");

        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password"));
    }

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
