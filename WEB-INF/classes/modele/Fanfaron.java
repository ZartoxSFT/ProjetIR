package modele;

import java.sql.Timestamp;

public class Fanfaron {
    private Long id;
    private String nom;
    private String nomFanfaron;
    private String prenom;
    private String email;
    private String motDePasseHash;
    private String genre;
    private String contraintesAlimentaires;
    private String role;
    private Timestamp dateCreation;
    private Timestamp derniereConnexion;
    private boolean admin;

    public Fanfaron() {
    }

    public Fanfaron(String nomFanfaron, String prenom, String nom, String email, String motDePasseHash, String genre, String contraintesAlimentaires) {
        this.nomFanfaron = nomFanfaron;
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.motDePasseHash = motDePasseHash;
        this.genre = genre;
        this.contraintesAlimentaires = contraintesAlimentaires;
        this.role = "utilisateur";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getnomFanfaron() {
        return nomFanfaron;
    }

    public void setnomFanfaron(String nomFanfaron) {
        this.nomFanfaron = nomFanfaron;
    }

    public String getNomFanfaron() {
        return nomFanfaron;
    }

    public void setNomFanfaron(String nomFanfaron) {
        this.nomFanfaron = nomFanfaron;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasseHash() {
        return motDePasseHash;
    }

    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
    }

    public String getMotDePasse() {
        return motDePasseHash;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasseHash = motDePasse;
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

    public boolean isAdmin() {
        return "admin".equals(role);
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

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
