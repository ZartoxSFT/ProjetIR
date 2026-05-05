package dao;

import java.sql.*;
import java.util.Base64;
import java.util.Properties;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import modele.Fanfaron;

public class ConnexionJDBCDAO {
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

    public Fanfaron authenticate(String nomFanfaron, String motDePasseHash) {
        String sql = "SELECT id, nom_fanfaron, prenom, nom, email, mot_de_passe, genre, contraintes_alimentaires, admin, date_creation, derniere_connexion FROM fanfaron WHERE nom_fanfaron = ? AND mot_de_passe = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, nomFanfaron);
            ps.setString(2, motDePasseHash);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapFanfaron(rs) : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Fanfaron mapFanfaron(ResultSet rs) throws SQLException {
        Fanfaron f = new Fanfaron();
        f.setId(rs.getLong("id"));
        f.setNomFanfaron(rs.getString("nom_fanfaron"));
        f.setPrenom(rs.getString("prenom"));
        f.setNom(rs.getString("nom"));
        f.setEmail(rs.getString("email"));
        f.setMotDePasse(rs.getString("mot_de_passe"));
        f.setGenre(rs.getString("genre"));
        f.setContraintesAlimentaires(rs.getString("contraintes_alimentaires"));
        f.setAdmin(rs.getBoolean("admin"));
        f.setDateCreation(rs.getTimestamp("date_creation"));
        f.setDerniereConnexion(rs.getTimestamp("derniere_connexion"));
        return f;
    }
}
