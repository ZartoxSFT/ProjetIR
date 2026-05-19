package modele;

import java.sql.Timestamp;

/**
 * CLASSE MODELE - EVENEMENT INSCRIT
 *
 * Represente les evenements auxquels un fanfaron s'est inscrit.
 * C'est une vue enrichie combinant les donnees d'Evenement avec les donnees
 * d'inscription.
 */
public class EvenementInscrit {
    // Identifiant de l'evenement
    private int id;

    // Informations de l'evenement
    private String nom;           // Type/titre de l'evenement
    private Timestamp horodatage; // Date et heure
    private int duree;            // Duree en minutes
    private String lieu;          // Lieu
    private String description;   // Description

    // Informations d'inscription
    private String instrument;    // L'instrument joue a cet evenement
    private String statut;        // Statut : present, absent, incertain

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

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
