package dao;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import modele.Fanfaron;

public class FanfaronDAO {

    private Connection getConnection() throws SQLException {
        try {
            Properties props = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties");

            if (input == null) {
                throw new SQLException("Fichier db.properties introuvable");
            }

            props.load(input);
            Class.forName("org.postgresql.Driver");

            return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password")
            );
        } catch (Exception e) {
            if (e instanceof SQLException) {
                throw (SQLException) e;
            }
            throw new SQLException("Impossible d'ouvrir la connexion a la base", e);
        }
    }

    public Fanfaron getById(int id) throws SQLException {
        String sql = "SELECT * FROM fanfaron WHERE id = ?";

        try (Connection connexion = getConnection();
             PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapFanfaron(rs) : null;
            }
        }
    }

    public Fanfaron getByNomFanfaron(String nomFanfaron) throws SQLException {
        String sql = "SELECT * FROM fanfaron WHERE nom_fanfaron = ?";

        try (Connection connexion = getConnection();
             PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, nomFanfaron);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapFanfaron(rs) : null;
            }
        }
    }

    public Fanfaron getByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM fanfaron WHERE email = ?";

        try (Connection connexion = getConnection();
             PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapFanfaron(rs) : null;
            }
        }
    }

    public List<Fanfaron> getAll() throws SQLException {
        String sql = "SELECT * FROM fanfaron ORDER BY id";
        List<Fanfaron> fanfarons = new ArrayList<>();

        try (Connection connexion = getConnection();
             PreparedStatement ps = connexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                fanfarons.add(mapFanfaron(rs));
            }
        }

        return fanfarons;
    }

    public void create(Fanfaron fanfaron) throws SQLException {
        String sql = """
            INSERT INTO fanfaron
            (nom_fanfaron, email, mot_de_passe, prenom, nom, genre, contraintes_alimentaires, admin)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection connexion = getConnection();
             PreparedStatement ps = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, fanfaron.getNomFanfaron());
            ps.setString(2, fanfaron.getEmail());
            ps.setString(3, fanfaron.getMotDePasse());
            ps.setString(4, fanfaron.getPrenom());
            ps.setString(5, fanfaron.getNom());
            ps.setString(6, fanfaron.getGenre());
            ps.setString(7, fanfaron.getContraintesAlimentaires());
            ps.setBoolean(8, fanfaron.getAdmin());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    fanfaron.setId(keys.getLong(1));
                }
            }
        }
    }

    public void update(Fanfaron fanfaron) throws SQLException {
        String sql = """
            UPDATE fanfaron
            SET nom_fanfaron = ?, email = ?, prenom = ?, nom = ?, genre = ?,
                contraintes_alimentaires = ?, admin = ?
            WHERE id = ?
        """;

        try (Connection connexion = getConnection();
             PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, fanfaron.getNomFanfaron());
            ps.setString(2, fanfaron.getEmail());
            ps.setString(3, fanfaron.getPrenom());
            ps.setString(4, fanfaron.getNom());
            ps.setString(5, fanfaron.getGenre());
            ps.setString(6, fanfaron.getContraintesAlimentaires());
            ps.setBoolean(7, fanfaron.getAdmin());
            ps.setLong(8, fanfaron.getId());

            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM fanfaron WHERE id = ?";

        try (Connection connexion = getConnection();
             PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void updateDerniereConnexion(Long id) throws SQLException {
        String sql = "UPDATE fanfaron SET derniere_connexion = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection connexion = getConnection();
             PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Fanfaron mapFanfaron(ResultSet rs) throws SQLException {
        Fanfaron fanfaron = new Fanfaron();
        fanfaron.setId(rs.getLong("id"));
        fanfaron.setNomFanfaron(rs.getString("nom_fanfaron"));
        fanfaron.setEmail(rs.getString("email"));
        fanfaron.setMotDePasse(rs.getString("mot_de_passe"));
        fanfaron.setPrenom(rs.getString("prenom"));
        fanfaron.setNom(rs.getString("nom"));
        fanfaron.setGenre(rs.getString("genre"));
        fanfaron.setContraintesAlimentaires(rs.getString("contraintes_alimentaires"));
        fanfaron.setAdmin(rs.getBoolean("admin"));
        fanfaron.setDateCreation(rs.getTimestamp("date_creation"));
        fanfaron.setDerniereConnexion(rs.getTimestamp("derniere_connexion"));
        return fanfaron;
    }
}
