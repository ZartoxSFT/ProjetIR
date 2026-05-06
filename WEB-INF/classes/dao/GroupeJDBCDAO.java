package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class GroupeJDBCDAO {

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

    public boolean isMemberOfCommissionPrestation(long idFanfaron) {
        String sql = "SELECT 1 FROM fanfaron_groupe fg "
                + "JOIN groupe_fanfare g ON g.id = fg.id_groupe "
                + "WHERE fg.id_fanfaron = ? AND g.nom = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setLong(1, idFanfaron);
            ps.setString(2, "commission prestation");

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
