package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modele.Fanfaron;

public class FanfaronJDBCDAO implements FanfaronDAO {
    private final DbConnectionManager dbManager;

    public FanfaronJDBCDAO(DbConnectionManager dbManager) {
        this.dbManager = dbManager;
    }

    private Connection getConnection() throws SQLException {
        return dbManager.getConnection();
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

    public Fanfaron authenticate(String nomFanfaron, String motDePasseHash) throws SQLException {
        String sql = "SELECT * FROM fanfaron WHERE nom_fanfaron = ? AND mot_de_passe = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, nomFanfaron);
            ps.setString(2, motDePasseHash);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapFanfaron(rs) : null;
            }
        }
    }

    public boolean existsByNomFanfaron(String nomFanfaron) throws SQLException {
        String sql = "SELECT 1 FROM fanfaron WHERE nom_fanfaron = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, nomFanfaron);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM fanfaron WHERE email = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Fanfaron> getAllFanfarons() {
        String sql = "SELECT * FROM fanfaron ORDER BY nom_fanfaron";
        List<Fanfaron> fanfarons = new ArrayList<>();

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                fanfarons.add(mapFanfaron(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fanfarons;
    }

    public Fanfaron getFanfaronById(long id) {
        try {
            return getById((int) id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addFanfaron(Fanfaron fanfaron) {
        try {
            create(fanfaron);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFanfaron(Fanfaron fanfaron) {
        String sql = """
                    UPDATE fanfaron
                    SET prenom = ?, nom = ?, email = ?, genre = ?,
                        contraintes_alimentaires = ?, admin = ?
                    WHERE id = ?
                """;

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, fanfaron.getPrenom());
            ps.setString(2, fanfaron.getNom());
            ps.setString(3, fanfaron.getEmail());
            ps.setString(4, fanfaron.getGenre());
            ps.setString(5, fanfaron.getContraintesAlimentaires());
            ps.setBoolean(6, fanfaron.getAdmin());
            ps.setLong(7, fanfaron.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFanfaron(long id) {
        try {
            delete((int) id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
        fanfaron.setDateCreation(rs.getDate("date_creation"));
        fanfaron.setDerniereConnexion(rs.getTimestamp("derniere_connexion"));
        return fanfaron;
    }
}
