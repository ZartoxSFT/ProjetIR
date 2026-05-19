package modele;

import java.sql.Timestamp;

/**
 * CLASSE MODELE - EVENEMENT
 *
 * Represente un evenement organise par la fanfare.
 * C'est une POJO utilisee pour mapper les donnees de la table evenement.
 *
 * Attributs :
 * - id : Identifiant unique
 * - typeEvenement : Type choisi dans une liste (atelier, repetition, prestation)
 * - nom : Nom libre de l'evenement (ex: "Concert printemps 2025")
 * - horodatage : Date et heure de l'evenement
 * - duree : Duree en minutes
 * - lieu : Lieu de l'evenement
 * - description : Description detaillee de l'evenement
 */
public class Evenement {
    // Identifiant unique
    private int id;

    // Informations de base
    private String typeEvenement;    // Type : atelier, repetition, prestation
    private String nom;              // Nom libre de l'evenement
    private Timestamp horodatage;    // Date et heure de l'evenement
    private int duree;               // Duree en minutes
    private String lieu;             // Lieu ou se deroule l'evenement
    private String description;      // Description detaillee

    /**
     * Constructeur par defaut.
     * Utilise par les DAO pour creer des instances.
     */
    public Evenement() {
    }

    /**
     * Constructeur avec tous les parametres.
     *
     * @param typeEvenement Type de l'evenement
     * @param nom Nom libre de l'evenement
     * @param horodatage Date et heure de l'evenement
     * @param duree Duree en minutes
     * @param lieu Lieu de l'evenement
     * @param description Description de l'evenement
     */
    public Evenement(String typeEvenement, String nom, Timestamp horodatage, int duree, String lieu,
            String description) {
        this.typeEvenement = typeEvenement;
        this.nom = nom;
        this.horodatage = horodatage;
        this.duree = duree;
        this.lieu = lieu;
        this.description = description;
    }

    // ========== GETTERS ET SETTERS ==========

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtient le type de l'evenement.
     *
     * @return Le type : atelier, repetition, ou prestation
     */
    public String getTypeEvenement() {
        return typeEvenement;
    }

    public void setTypeEvenement(String typeEvenement) {
        this.typeEvenement = typeEvenement;
    }

    /**
     * Obtient le nom libre de l'evenement.
     *
     * @return Le nom (ex: "Concert printemps")
     */
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Timestamp getHorodatage() {
        return horodatage;
    }

    public void setHorodatage(Timestamp horodatage) {
        this.horodatage = horodatage;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
