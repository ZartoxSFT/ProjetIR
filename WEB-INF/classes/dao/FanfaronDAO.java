package dao;

import java.sql.SQLException;
import java.util.List;

import modele.Fanfaron;

/**
 * INTERFACE DAO - FANFARON
 * 
 * Contrat pour l'accès aux données des fanfarons
 * Définit toutes les opérations CRUD (Create, Read, Update, Delete)
 * et les opérations métier spécifiques aux fanfarons
 * 
 * Pattern DAO (Data Access Object) :
 * - Isole la logique d'accès à la base de données
 * - Facilite les tests unitaires
 * - Permet de changer d'implémentation (JDBC, JPA, etc.)
 * 
 * Implémentation : FanfaronJDBCDAO
 */
public interface FanfaronDAO {
    // ========== OPÉRATIONS CRUD DE BASE ==========
    
    /**
     * Récupère un fanfaron par son identifiant
     * @param id L'ID du fanfaron
     * @return Le fanfaron ou null si non trouvé
     */
    Fanfaron getById(int id) throws SQLException;

    /**
     * Récupère un fanfaron par son nom d'utilisateur
     * @param nomFanfaron Le nom d'utilisateur unique
     * @return Le fanfaron ou null si non trouvé
     */
    Fanfaron getByNomFanfaron(String nomFanfaron) throws SQLException;

    /**
     * Récupère un fanfaron par son email
     * @param email L'adresse email unique
     * @return Le fanfaron ou null si non trouvé
     */
    Fanfaron getByEmail(String email) throws SQLException;

    /**
     * Récupère tous les fanfarons
     * @return Liste de tous les fanfarons
     */
    List<Fanfaron> getAll() throws SQLException;

    /**
     * Crée un nouveau fanfaron en base de données
     * @param fanfaron L'objet fanfaron à insérer
     */
    void create(Fanfaron fanfaron) throws SQLException;

    /**
     * Met à jour les données d'un fanfaron
     * @param fanfaron L'objet fanfaron avec les modifications
     */
    void update(Fanfaron fanfaron) throws SQLException;

    /**
     * Supprime un fanfaron
     * @param id L'ID du fanfaron à supprimer
     */
    void delete(int id) throws SQLException;

    // ========== OPÉRATIONS MÉTIER SPÉCIFIQUES ==========
    
    /**
     * Met à jour le timestamp de dernière connexion
     * Utilisé pour suivre la dernière activité du fanfaron
     * @param id L'ID du fanfaron
     */
    void updateDerniereConnexion(Long id) throws SQLException;

    /**
     * Authentifie un fanfaron en vérifiant ses identifiants
     * @param nomFanfaron Le nom d'utilisateur
     * @param motDePasseHash Le mot de passe hashé
     * @return Le fanfaron si les identifiants sont corrects, null sinon
     */
    Fanfaron authenticate(String nomFanfaron, String motDePasseHash) throws SQLException;

    /**
     * Vérifie si un nom d'utilisateur existe déjà
     * Utilisé lors de l'inscription pour éviter les doublons
     * @param nomFanfaron Le nom d'utilisateur à vérifier
     * @return true si le nom existe, false sinon
     */
    boolean existsByNomFanfaron(String nomFanfaron) throws SQLException;

    /**
     * Vérifie si un email existe déjà
     * Utilisé lors de l'inscription pour éviter les doublons
     * @param email L'email à vérifier
     * @return true si l'email existe, false sinon
     */
    boolean existsByEmail(String email) throws SQLException;

    // ========== OPÉRATIONS VARIANTES (SANS CHECKED EXCEPTION) ==========
    // Version alternative des opérations CRUD sans lever SQLException
    
    /**
     * Récupère tous les fanfarons (version sans exception)
     * @return Liste de tous les fanfarons
     */
    List<Fanfaron> getAllFanfarons();

    /**
     * Récupère un fanfaron par ID (version sans exception)
     * @param id L'ID du fanfaron
     * @return Le fanfaron ou null
     */
    Fanfaron getFanfaronById(long id);

    /**
     * Ajoute un nouveau fanfaron (version sans exception)
     * @param fanfaron Le fanfaron à ajouter
     * @return true si succès, false sinon
     */
    boolean addFanfaron(Fanfaron fanfaron);

    /**
     * Met à jour un fanfaron (version sans exception)
     * @param fanfaron Le fanfaron modifié
     * @return true si succès, false sinon
     */
    boolean updateFanfaron(Fanfaron fanfaron);

    /**
     * Supprime un fanfaron (version sans exception)
     * @param id L'ID du fanfaron à supprimer
     * @return true si succès, false sinon
     */
    boolean deleteFanfaron(long id);

    /**
     * Vérifie si un fanfaron est membre de la commission "Prestation"
     * La commission Prestation gère les événements
     * @param idFanfaron L'ID du fanfaron
     * @return true si membre de la commission, false sinon
     */
    boolean isMemberOfCommissionPrestation(long idFanfaron);
}
