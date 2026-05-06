package modele;

import java.sql.Timestamp;

public class Evenement {
    private int id;
    private String typeEvenement;
    private String nom;
    private Timestamp horodatage;
    private int duree;
    private String lieu;
    private String description;

    public Evenement() {
    }

    public Evenement(String typeEvenement, String nom, Timestamp horodatage, int duree, String lieu,
            String description) {
        this.typeEvenement = typeEvenement;
        this.nom = nom;
        this.horodatage = horodatage;
        this.duree = duree;
        this.lieu = lieu;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeEvenement() {
        return typeEvenement;
    }

    public void setTypeEvenement(String typeEvenement) {
        this.typeEvenement = typeEvenement;
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
