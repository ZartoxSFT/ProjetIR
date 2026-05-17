package dao;

import java.sql.SQLException;
import java.util.List;

import modele.Evenement;
import modele.EvenementInscrit;

/**
 * INTERFACE DAO - ÉVÉNEMENT
 * 
 * Contrat pour l'accès aux données des événements
 * 
 * Un événement représente une manifestation/concert/répétition commune
 * Exemples : "Concert printemps 2025", "Répétition générale", etc.
 * 
 * Gère aussi la relation avec les inscriptions :
 * - Un événement peut avoir plusieurs inscriptions
 * - Une inscription lie un fanfaron à un événement
 * - L'inscription inclut : statut (present/absent/incertain), instrument joué
 * 
 * Implémentation : EvenementJDBCDAO
 */
public interface EvenementDAO {
    // ========== OPÉRATIONS CRUD DE BASE ==========
    
    /**
     * Récupère tous les événements
     * @return Liste de tous les événements
     */
    List<Evenement> getAllEvenements();

    /**
     * Récupère un événement par son identifiant
     * @param id L'ID de l'événement
     * @return L'événement ou null si non trouvé
     */
    Evenement getById(int id) throws SQLException;

    /**
     * Met à jour un événement
     * @param evenement L'événement avec les modifications
     * @return true si succès, false sinon
     */
    boolean updateEvenement(Evenement evenement);

    /**
     * Supprime un événement
     * @param id L'ID de l'événement à supprimer
     * @return true si succès, false sinon
     */
    boolean deleteEvenement(int id);

    // ========== OPÉRATIONS MÉTIER SPÉCIFIQUES ==========
    
    /**
     * Insère un nouvel événement avec un organisateur
     * 
     * Un événement est toujours créé par un fanfaron (organisateur)
     * Ce fanfaron est enregistré dans la table organisation_evenement
     * 
     * @param evenement L'événement à créer
     * @param idFanfaron L'ID du fanfaron qui organise l'événement
     * @return true si succès, false sinon
     */
    boolean insertAvecOrganisateur(Evenement evenement, long idFanfaron);

    /**
     * Récupère tous les événements auxquels un fanfaron s'est inscrit
     * 
     * Retourne les événements enrichis avec les données d'inscription :
     * - Tous les détails de l'événement
     * - L'instrument que le fanfaron joue à cet événement
     * - Le statut de participation (present, absent, incertain)
     * 
     * Utilisé pour afficher les événements d'un fanfaron (page d'accueil)
     * 
     * @param idFanfaron L'ID du fanfaron
     * @return Liste des événements auxquels il est inscrit avec ses données d'inscription
     */
    List<EvenementInscrit> getEvenementsInscritsByFanfaron(long idFanfaron);
}
