package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import modele.Fanfaron;

public class AdminJDBCDAO {

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

    // Récupère tous les fanfarons
    public List<Fanfaron> getAllFanfarons() {
        String sql = "SELECT id, nom_fanfaron, prenom, nom, email, mot_de_passe, genre, contraintes_alimentaires, admin, date_creation, derniere_connexion FROM fanfaron ORDER BY nom_fanfaron";
        List<Fanfaron> fanfarons = new ArrayList<>();

        try (Connection connexion = getConnection();
                Statement ps = connexion.createStatement();
                ResultSet rs = ps.executeQuery(sql)) {

            while (rs.next()) {
                fanfarons.add(mapFanfaron(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fanfarons;
    }

    // Ajoute un nouveau fanfaron
    public boolean addFanfaron(Fanfaron f) {
        String sql = "INSERT INTO fanfaron (nom_fanfaron, prenom, nom, email, mot_de_passe, genre, contraintes_alimentaires, admin, date_creation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, f.getNomFanfaron());
            ps.setString(2, f.getPrenom());
            ps.setString(3, f.getNom());
            ps.setString(4, f.getEmail());
            ps.setString(5, f.getMotDePasse());
            ps.setString(6, f.getGenre());
            ps.setString(7, f.getContraintesAlimentaires());
            ps.setBoolean(8, f.isAdmin());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Récupère un fanfaron par ID
    public Fanfaron getFanfaronById(long id) {
        String sql = "SELECT id, nom_fanfaron, prenom, nom, email, mot_de_passe, genre, contraintes_alimentaires, admin, date_creation, derniere_connexion FROM fanfaron WHERE id = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFanfaron(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Modifie un fanfaron
    public boolean updateFanfaron(Fanfaron f) {
        String sql = "UPDATE fanfaron SET prenom = ?, nom = ?, email = ?, genre = ?, contraintes_alimentaires = ?, admin = ? WHERE id = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, f.getPrenom());
            ps.setString(2, f.getNom());
            ps.setString(3, f.getEmail());
            ps.setString(4, f.getGenre());
            ps.setString(5, f.getContraintesAlimentaires());
            ps.setBoolean(6, f.isAdmin());
            ps.setLong(7, f.getId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprime un fanfaron
    public boolean deleteFanfaron(long id) {
        String sql = "DELETE FROM fanfaron WHERE id = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
        f.setIsAdmin(rs.getBoolean("admin"));
        f.setDateCreation(rs.getTimestamp("date_creation"));
        f.setDerniereConnexion(rs.getTimestamp("derniere_connexion"));
        return f;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Impossible de hacher le mot de passe.", e);
        }
    }
}
