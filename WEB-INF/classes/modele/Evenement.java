package modele;

import java.sql.Timestamp;

/**
 * CLASSE MODELE - EVENEMENT
 * 
 * Représente un événement organisé par la fanfare
 * C'est une POJO utilisée pour mapper les données de la table evenement
 * 
 * Attributs :
 * - id : Identifiant unique
 * - nom : Nom de l'événement (ex: "Concert printemps 2025")
 * - horodatage : Date et heure de l'événement
 * - duree : Durée en minutes
 * - lieu : Lieu de l'événement (ex: "Salle des fêtes")
 * - description : Description détaillée de l'événement
 * 
 * Utilisation :
 * - Affichage de la liste des événements
 * - Inscription des fanfarons aux événements
 * - Gestion des inscriptions (présent/absent/incertain)
 * 
 * Lien avec Fanfaron :
 * - Un événement peut avoir plusieurs inscriptions
 * - Une inscription lie un fanfaron à un événement
 */
public class Evenement {
    // Identifiant unique
    private int id;
    
    // Informations de base
    private String nom;              // Nom de l'événement
    private Timestamp horodatage;    // Date et heure de l'événement
    private int duree;               // Durée en minutes
    private String lieu;             // Lieu où se déroule l'événement
    private String description;      // Description détaillée

    /**
     * Constructeur par défaut
     * Utilisé par les DAO pour créer des instances
     */
    public Evenement() {
    }

    /**
     * Constructeur avec tous les paramètres
     * Utilisé lors de la création d'un nouvel événement
     * 
     * @param nom Nom de l'événement
     * @param horodatage Date et heure de l'événement
     * @param duree Durée en minutes
     * @param lieu Lieu de l'événement
     * @param description Description de l'événement
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
    
    /**
     * Obtient l'identifiant unique de l'événement
     * @return L'ID en base de données
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtient le nom de l'événement
     * @return Le nom (ex: "Concert printemps")
     */
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Obtient la date et heure de l'événement
     * @return Le timestamp (date + heure + fuseau horaire)
     */
    public Timestamp getHorodatage() {
        return horodatage;
    }

    public void setHorodatage(Timestamp horodatage) {
        this.horodatage = horodatage;
    }

    /**
     * Obtient la durée de l'événement
     * @return La durée en minutes
     */
    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    /**
     * Obtient le lieu de l'événement
     * @return Le lieu (ex: "Salle des fêtes")
     */
    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    /**
     * Obtient la description de l'événement
     * @return La description détaillée
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
