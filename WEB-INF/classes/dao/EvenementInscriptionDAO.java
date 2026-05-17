package dao;

import java.util.List;

import modele.InscriptionDetail;

/**
 * INTERFACE DAO - INSCRIPTION À ÉVÉNEMENT
 * 
 * Contrat pour la gestion des inscriptions aux événements
 * Gère la table "inscription" qui lie les fanfarons aux événements
 * 
 * Relation N:N enrichie :
 * - Un fanfaron peut s'inscrire à plusieurs événements
 * - Un événement peut avoir plusieurs inscriptions
 * - Chaque inscription contient :
 *   * idFanfaron : Le fanfaron qui s'inscrit
 *   * idEvenement : L'événement auquel il s'inscrit
 *   * idInstrument : L'instrument qu'il joue à cet événement
 *   * statut : Sa participation (present, absent, incertain)
 * 
 * Implémentation : EvenementInscriptionJDBCDAO
 */
public interface EvenementInscriptionDAO {
    // ========== OPÉRATIONS SUR LES INSCRIPTIONS ==========
    
    /**
     * Crée ou met à jour une inscription
     * 
     * Utilise UPSERT (Update if exists, Insert if not)
     * Permet de gérer le cas où une inscription existe déjà
     * 
     * Cas d'utilisation :
     * - Fanfaron qui s'inscrit à un événement
     * - Fanfaron qui change son instrument pour un événement
     * - Fanfaron qui change son statut de participation
     * 
     * @param idFanfaron L'ID du fanfaron
     * @param idEvenement L'ID de l'événement
     * @param idInstrument L'ID de l'instrument joué
     * @param statut Le statut : present, absent, ou incertain
     * @return true si succès, false sinon
     */
    boolean upsertInscription(long idFanfaron, int idEvenement, int idInstrument, String statut);

    /**
     * Supprime une inscription
     * Le fanfaron annule son participation à un événement
     * 
     * @param idFanfaron L'ID du fanfaron
     * @param idEvenement L'ID de l'événement
     * @return true si succès, false sinon
     */
    boolean deleteInscription(long idFanfaron, int idEvenement);

    /**
     * Récupère toutes les inscriptions pour un événement
     * 
     * Retourne les détails complets de tous les fanfarons inscrits :
     * - Informations du fanfaron (nom, prénom)
     * - Instrument qu'il joue
     * - Statut de participation
     * 
     * Utilisé pour afficher la liste des participants lors du clic sur un événement
     * 
     * @param idEvenement L'ID de l'événement
     * @return Liste des détails des fanfarons inscrits
     */
    List<InscriptionDetail> getInscriptionsByEvenement(int idEvenement);
}
