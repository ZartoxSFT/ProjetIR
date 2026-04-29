package modele;

import java.sql.Timestamp;

public class Fanfaron {
    private int id;
    private String nomFanfaron;
    private String email;
    private String motDePasse;
    private String prenom;
    private String nom;
    private String genre;
    private String contraintesAlimentaires;
    private String role; // "utilisateur" ou "admin"
    private Timestamp dateCreation;
    private Timestamp derniereConnexion;

    // Constructeur vide
    public Fanfaron() {
    }

    // Constructeur complet
    public Fanfaron(int id, String nomFanfaron, String email, String motDePasse,
            String prenom, String nom, String genre, String contraintesAlimentaires,
            String role, Timestamp dateCreation, Timestamp derniereConnexion) {
        this.id = id;
        this.nomFanfaron = nomFanfaron;
        this.email = email;
        this.motDePasse = motDePasse;
        this.prenom = prenom;
        this.nom = nom;
        this.genre = genre;
        this.contraintesAlimentaires = contraintesAlimentaires;
        this.role = role;
        this.dateCreation = dateCreation;
        this.derniereConnexion = derniereConnexion;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomFanfaron() {
        return nomFanfaron;
    }

    public void setNomFanfaron(String nomFanfaron) {
        this.nomFanfaron = nomFanfaron;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getContraintesAlimentaires() {
        return contraintesAlimentaires;
    }

    public void setContraintesAlimentaires(String contraintesAlimentaires) {
        this.contraintesAlimentaires = contraintesAlimentaires;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Timestamp getDerniereConnexion() {
        return derniereConnexion;
    }

    public void setDerniereConnexion(Timestamp derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }

    public boolean isAdmin() {
        return "admin".equals(this.role);
    }

    @Override
    public String toString() {
        return "Fanfaron{" +
                "id=" + id +
                ", nomFanfaron='" + nomFanfaron + '\'' +
                ", email='" + email + '\'' +
                ", prenom='" + prenom + '\'' +
                ", nom='" + nom + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
