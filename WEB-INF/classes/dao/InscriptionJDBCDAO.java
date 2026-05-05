package dao;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
import modele.Fanfaron;

public class InscriptionJDBCDAO {

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
            props.getProperty("db.password")
        );
    }

    public boolean insert(Fanfaron f) {
        String sql = """
            INSERT INTO fanfaron
            (nom_fanfaron, prenom, nom, email, mot_de_passe, genre, contraintes_alimentaires)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection connexion = getConnection();
             PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, f.getnomFanfaron());
            ps.setString(2, f.getPrenom());
            ps.setString(3, f.getNom());
            ps.setString(4, f.getEmail());
            ps.setString(5, f.getMotDePasseHash());
            ps.setString(6, f.getGenre());
            ps.setString(7, f.getContraintesAlimentaires());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsByNomFanfaron(String nomFanfaron) {
        String sql = "SELECT 1 FROM fanfaron WHERE nom_fanfaron = ?";

        try (Connection connexion = getConnection();
            PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, nomFanfaron);
            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsByEmail(String email){
        String sql = "SELECT 1 FROM fanfaron WHERE email = ?";

        try (Connection connexion = getConnection();
             PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }
}
