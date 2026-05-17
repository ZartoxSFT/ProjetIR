package modele;

import java.sql.Timestamp;

/**
 * CLASSE MODELE - EVENEMENT INSCRIT
 * 
 * Représente les événements auxquels un fanfaron s'est inscrit
 * C'est une vue enrichie combinant les données d'Événement avec les données d'inscription
 * 
 * Attributs :
 * - Tous les attributs d'un événement (id, nom, lieu, date, etc.)
 * - instrument : L'instrument que le fanfaron joue à cet événement
 * - statut : Le statut de participation du fanfaron (present, absent, incertain)
 * 
 * Statuts possibles :
 * - "present" : Le fanfaron sera présent
 * - "absent" : Le fanfaron a annulé sa participation
 * - "incertain" : Le fanfaron n'a pas confirmé
 * 
 * Utilisation :
 * - Affichage des événements auxquels un fanfaron s'est inscrit
 * - Page d'accueil : afficher les événements futurs de l'utilisateur
 * - Suivi des participations
 */
public class EvenementInscrit {
    // Identifiant de l'événement
    private int id;
    
    // Informations de l'événement
    private String nom;           // Nom de l'événement
    private Timestamp horodatage; // Date et heure
    private int duree;            // Durée en minutes
    private String lieu;          // Lieu
    private String description;   // Description
    
    // Informations d'inscription
    private String instrument;    // L'instrument joué à cet événement
    private String statut;        // Statut : present, absent, incertain

    /**
     * Obtient l'identifiant de l'événement
     * @return L'ID
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtient le nom de l'événement
     * @return Le nom
     */
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Obtient la date et heure de l'événement
     * @return Le timestamp
     */
    public Timestamp getHorodatage() {
        return horodatage;
    }

    public void setHorodatage(Timestamp horodatage) {
        this.horodatage = horodatage;
    }

    /**
     * Obtient la durée de l'événement
     * @return Durée en minutes
     */
    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    /**
     * Obtient le lieu de l'événement
     * @return Le lieu
     */
    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    /**
     * Obtient la description de l'événement
     * @return La description
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Obtient l'instrument joué à cet événement
     * @return Le nom de l'instrument
     */
    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    /**
     * Obtient le statut de participation du fanfaron
     * @return Le statut : present, absent, ou incertain
     */
    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
