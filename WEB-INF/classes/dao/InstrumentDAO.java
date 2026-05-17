package dao;

import java.util.List;

import modele.GroupeFanfare;
import modele.Instrument;

/**
 * INTERFACE DAO - INSTRUMENT ET GROUPE DE FANFARE
 * 
 * Contrat pour l'accès aux données des instruments et groupes
 * Cette interface gère deux entités liées :
 * - Instrument : Les instruments que jouent les fanfarons
 * - GroupeFanfare : Les groupes/sections de la fanfare
 * 
 * Gère également les relations N:N :
 * - Fanfaron ↔ Instrument (table fanfaron_instrument)
 * - Fanfaron ↔ Groupe (table fanfaron_groupe)
 * 
 * Implémentation : InstrumentJDBCDAO
 */
public interface InstrumentDAO {
    // ========== OPÉRATIONS SUR LES INSTRUMENTS ==========
    
    /**
     * Insère un nouvel instrument
     * @param instrument L'instrument à insérer
     * @return true si succès, false sinon
     */
    boolean insertInstrument(Instrument instrument);

    /**
     * Récupère tous les instruments disponibles
     * @return Liste de tous les instruments
     */
    List<Instrument> findAllInstruments();

    /**
     * Met à jour les données d'un instrument
     * @param instrument L'instrument avec les modifications
     * @return true si succès, false sinon
     */
    boolean updateInstrument(Instrument instrument);

    /**
     * Supprime un instrument
     * @param id L'ID de l'instrument à supprimer
     * @return true si succès, false sinon
     */
    boolean deleteInstrument(Long id);

    // ========== OPÉRATIONS SUR LES GROUPES ==========
    
    /**
     * Récupère tous les groupes de fanfare
     * @return Liste de tous les groupes
     */
    List<GroupeFanfare> findAllGroupes();

    /**
     * Insère un nouveau groupe
     * @param groupe Le groupe à insérer
     * @return true si succès, false sinon
     */
    boolean insertGroupe(GroupeFanfare groupe);

    /**
     * Met à jour les données d'un groupe
     * @param groupe Le groupe avec les modifications
     * @return true si succès, false sinon
     */
    boolean updateGroupe(GroupeFanfare groupe);

    /**
     * Supprime un groupe
     * @param id L'ID du groupe à supprimer
     * @return true si succès, false sinon
     */
    boolean deleteGroupe(Long id);

    // ========== OPÉRATIONS SUR LES RELATIONS N:N ==========
    // Instruments d'un fanfaron
    
    /**
     * Récupère les IDs des instruments joués par un fanfaron
     * @param idFanfaron L'ID du fanfaron
     * @return Liste des IDs des instruments
     */
    List<Long> findInstrumentIdsByFanfaron(Long idFanfaron);

    /**
     * Récupère les instruments joués par un fanfaron (objets complets)
     * @param idFanfaron L'ID du fanfaron
     * @return Liste des instruments avec tous leurs détails
     */
    List<Instrument> findInstrumentsByFanfaron(Long idFanfaron);

    /**
     * Met à jour les instruments d'un fanfaron
     * Supprime les anciens liens et en crée de nouveaux
     * @param idFanfaron L'ID du fanfaron
     * @param instruments Tableau des IDs des instruments sélectionnés
     * @return true si succès, false sinon
     */
    boolean updateInstrumentsFanfaron(Long idFanfaron, String[] instruments);

    // Groupes d'un fanfaron
    
    /**
     * Récupère les IDs des groupes auxquels appartient un fanfaron
     * @param idFanfaron L'ID du fanfaron
     * @return Liste des IDs des groupes
     */
    List<Long> findGroupeIdsByFanfaron(Long idFanfaron);

    /**
     * Récupère les groupes auxquels appartient un fanfaron (objets complets)
     * @param idFanfaron L'ID du fanfaron
     * @return Liste des groupes avec tous leurs détails
     */
    List<GroupeFanfare> findGroupesByFanfaron(Long idFanfaron);

    /**
     * Met à jour les groupes d'un fanfaron
     * Supprime les anciens liens et en crée de nouveaux
     * @param idFanfaron L'ID du fanfaron
     * @param groupes Tableau des IDs des groupes sélectionnés
     * @return true si succès, false sinon
     */
    boolean updateGroupesFanfaron(Long idFanfaron, String[] groupes);
}
