package modele;

import java.sql.Timestamp;

/**
 * CLASSE MODELE - EVENEMENT
 *
 * Represente un evenement organise par la fanfare.
 * Le champ nom correspond au type/titre choisi dans la liste :
 * atelier, repetition ou prestation.
 */
public class Evenement {
    // Identifiant unique
    private int id;

    // Informations de base
    private String nom;              // Type/titre de l'evenement
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
     * @param nom Type/titre de l'evenement
     * @param horodatage Date et heure de l'evenement
     * @param duree Duree en minutes
     * @param lieu Lieu de l'evenement
     * @param description Description de l'evenement
     */
    public Evenement(String nom, Timestamp horodatage, int duree, String lieu,
            String description) {
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
