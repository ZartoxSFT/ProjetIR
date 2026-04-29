package dao;

import modele.Fanfaron;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FanfaronDAO {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "ayoub";
    private static final String DB_PASSWORD = ".Zenkai040103";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Récupérer un fanfaron par nom d'utilisateur
    public Fanfaron getByNomFanfaron(String nomFanfaron) throws SQLException {
        String sql = "SELECT * FROM fanfaron WHERE nom_fanfaron = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nomFanfaron);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFanfaron(rs);
                }
            }
        }
        return null;
    }

    // Récupérer un fanfaron par email
    public Fanfaron getByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM fanfaron WHERE email = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFanfaron(rs);
                }
            }
        }
        return null;
    }

    // Récupérer un fanfaron par ID
    public Fanfaron getById(int id) throws SQLException {
        String sql = "SELECT * FROM fanfaron WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFanfaron(rs);
                }
            }
        }
        return null;
    }

    // Lister tous les fanfarons
    public List<Fanfaron> getAll() throws SQLException {
        List<Fanfaron> fanfarons = new ArrayList<>();
        String sql = "SELECT * FROM fanfaron ORDER BY date_creation DESC";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                fanfarons.add(mapResultSetToFanfaron(rs));
            }
        }
        return fanfarons;
    }

    // Créer un nouveau fanfaron
    public void create(Fanfaron fanfaron) throws SQLException {
        String sql = "INSERT INTO fanfaron (nom_fanfaron, email, mot_de_passe, prenom, nom, genre, contraintes_alimentaires, role) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, fanfaron.getNomFanfaron());
            pstmt.setString(2, fanfaron.getEmail());
            pstmt.setString(3, fanfaron.getMotDePasse());
            pstmt.setString(4, fanfaron.getPrenom());
            pstmt.setString(5, fanfaron.getNom());
            pstmt.setString(6, fanfaron.getGenre());
            pstmt.setString(7, fanfaron.getContraintesAlimentaires());
            pstmt.setString(8, fanfaron.getRole() != null ? fanfaron.getRole() : "utilisateur");
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    fanfaron.setId(rs.getInt(1));
                }
            }
        }
    }

    // Mettre à jour un fanfaron
    public void update(Fanfaron fanfaron) throws SQLException {
        String sql = "UPDATE fanfaron SET nom_fanfaron = ?, email = ?, prenom = ?, nom = ?, genre = ?, contraintes_alimentaires = ?, role = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fanfaron.getNomFanfaron());
            pstmt.setString(2, fanfaron.getEmail());
            pstmt.setString(3, fanfaron.getPrenom());
            pstmt.setString(4, fanfaron.getNom());
            pstmt.setString(5, fanfaron.getGenre());
            pstmt.setString(6, fanfaron.getContraintesAlimentaires());
            pstmt.setString(7, fanfaron.getRole());
            pstmt.setInt(8, fanfaron.getId());
            pstmt.executeUpdate();
        }
    }

    // Mettre à jour le mot de passe
    public void updateMotDePasse(int id, String motDePasse) throws SQLException {
        String sql = "UPDATE fanfaron SET mot_de_passe = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, motDePasse);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }

    // Changer le rôle d'un utilisateur
    public void updateRole(int id, String role) throws SQLException {
        String sql = "UPDATE fanfaron SET role = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }

    // Supprimer un fanfaron
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM fanfaron WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // Mettre à jour la dernière connexion
    public void updateDerniereConnexion(int id) throws SQLException {
        String sql = "UPDATE fanfaron SET derniere_connexion = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // Mapper un ResultSet à un objet Fanfaron
    private Fanfaron mapResultSetToFanfaron(ResultSet rs) throws SQLException {
        return new Fanfaron(
                rs.getInt("id"),
                rs.getString("nom_fanfaron"),
                rs.getString("email"),
                rs.getString("mot_de_passe"),
                rs.getString("prenom"),
                rs.getString("nom"),
                rs.getString("genre"),
                rs.getString("contraintes_alimentaires"),
                rs.getString("role"),
                rs.getTimestamp("date_creation"),
                rs.getTimestamp("derniere_connexion"));
    }
}
