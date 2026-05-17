package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modele.Fanfaron;

/**
 * DAO JDBC - FANFARON
 *
 * Responsabilites :
 * - Lire, creer, modifier et supprimer les fanfarons
 * - Authentifier un fanfaron avec son nom d'utilisateur et son mot de passe hashe
 * - Verifier les doublons pendant l'inscription
 * - Determiner certains droits metier, comme l'appartenance a la commission prestation
 *
 * Table principale : fanfaron
 */
public class FanfaronJDBCDAO implements FanfaronDAO {
    // Gestionnaire centralise des connexions a la base de donnees
    private final DbConnectionManager dbManager;

    /**
     * Constructeur avec injection du gestionnaire de connexions.
     */
    public FanfaronJDBCDAO(DbConnectionManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Ouvre une connexion via le gestionnaire partage.
     */
    private Connection getConnection() throws SQLException {
        return dbManager.getConnection();
    }

    /**
     * Recupere un fanfaron par son identifiant numerique.
     */
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

    /**
     * Recupere un fanfaron par son nom de fanfaron, utilise pour la connexion.
     */
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

    /**
     * Recupere un fanfaron par son email.
     */
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

    /**
     * Recupere tous les fanfarons tries par identifiant.
     */
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

    /**
     * Cree un fanfaron et renseigne son ID avec la cle generee par la base.
     */
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

            // Recuperation de l'ID auto-genere pour synchroniser l'objet Java
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    fanfaron.setId(keys.getLong(1));
                }
            }
        }
    }

    /**
     * Met a jour toutes les informations principales d'un fanfaron.
     */
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

    /**
     * Supprime un fanfaron par son identifiant.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM fanfaron WHERE id = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Met a jour le timestamp de derniere connexion.
     */
    public void updateDerniereConnexion(Long id) throws SQLException {
        String sql = "UPDATE fanfaron SET derniere_connexion = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Authentifie un fanfaron en comparant le nom et le mot de passe deja hashe.
     */
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

    /**
     * Verifie si un nom de fanfaron est deja utilise.
     */
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

    /**
     * Verifie si une adresse email est deja utilisee.
     */
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

    /**
     * Recupere tous les fanfarons pour l'administration.
     */
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

    /**
     * Version sans exception controlee de la recherche par ID, pratique pour les servlets.
     */
    public Fanfaron getFanfaronById(long id) {
        try {
            return getById((int) id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Version booleenne de la creation, adaptee aux messages utilisateur des servlets.
     */
    public boolean addFanfaron(Fanfaron fanfaron) {
        try {
            create(fanfaron);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Met a jour les champs administrables depuis la page admin.
     */
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

    /**
     * Version booleenne de la suppression, adaptee aux servlets.
     */
    public boolean deleteFanfaron(long id) {
        try {
            delete((int) id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifie si le fanfaron appartient au groupe "commission prestation".
     */
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

    /**
     * Convertit la ligne courante du ResultSet en objet Fanfaron.
     */
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
