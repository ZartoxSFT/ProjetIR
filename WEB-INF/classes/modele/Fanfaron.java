package modele;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * CLASSE MODELE - FANFARON
 * 
 * Représente un fanfaron (membre de la fanfare) dans l'application
 * C'est une POJO (Plain Old Java Object) utilisée par la couche DAO
 * 
 * Attributs importants :
 * - id : Identifiant unique dans la base de données
 * - nomFanfaron : Nom d'utilisateur (unique, utilisé pour la connexion)
 * - prenom, nom : Identité civile
 * - email : Adresse email unique
 * - motDePasseHash : Mot de passe hashé (SHA-256 + Base64)
 * - genre : Sexe du fanfaron (M ou F)
 * - contraintesAlimentaires : Informations sur les régimes/allergies
 * - admin : Statut administrateur (true = admin, false = utilisateur normal)
 * - dateCreation, derniereConnexion : Métadonnées temporelles
 * 
 * Architecture MVC :
 * - Cette classe représente le modèle dans l'architecture MVC
 * - Elle est utilisée par les DAO pour mapper les lignes de la base de données
 * - Elle est transmise aux JSP pour l'affichage
 */
public class Fanfaron {
    // Identifiant unique en base de données
    private Long id;
    
    // Données d'identité civile
    private String nom;
    private String nomFanfaron;    // Nom d'utilisateur unique
    private String prenom;
    
    // Informations de contact et d'authentification
    private String email;
    private String motDePasseHash; // Mot de passe hashé SHA-256
    
    // Informations personnelles
    private String genre;                    // M ou F
    private String contraintesAlimentaires;   // Régimes, allergies, etc.
    
    // Métadonnées temporelles
    private Date dateCreation;
    private Timestamp derniereConnexion;
    
    // Statut dans l'application
    private boolean admin; // true si administrateur, false sinon

    /**
     * Constructeur par défaut
     * Utilisé par les DAO pour créer des instances via réflexion
     */
    public Fanfaron() {
    }

    /**
     * Constructeur avec les paramètres d'inscription
     * Utilisé lors de l'enregistrement d'un nouveau fanfaron
     * 
     * @param nomFanfaron Nom d'utilisateur unique
     * @param prenom Prénom du fanfaron
     * @param nom Nom de famille du fanfaron
     * @param email Adresse email
     * @param motDePasseHash Mot de passe hashé
     * @param genre Genre du fanfaron (M ou F)
     * @param contraintesAlimentaires Contraintes alimentaires/allergies
     */
    public Fanfaron(String nomFanfaron, String prenom, String nom, String email, String motDePasseHash, String genre, String contraintesAlimentaires) {
        this.nomFanfaron = nomFanfaron;
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.motDePasseHash = motDePasseHash;
        this.genre = genre;
        this.contraintesAlimentaires = contraintesAlimentaires;
        this.admin = false; // Par défaut, un nouveau fanfaron n'est pas admin
    }

    // ========== GETTERS ET SETTERS ==========
    // Accesseurs pour tous les attributs de la classe
    
    /**
     * Obtient l'identifiant unique du fanfaron
     * @return L'ID en base de données
     */
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

    /**
     * Obtient le nom d'utilisateur du fanfaron
     * C'est l'identifiant utilisé pour la connexion
     * @return Le nom d'utilisateur unique
     */
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

    /**
     * Obtient l'adresse email du fanfaron
     * @return L'email unique
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtient le mot de passe hashé du fanfaron
     * @return Le hash SHA-256 encodé en Base64
     */
    public String getMotDePasseHash() {
        return motDePasseHash;
    }

    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
    }

    /**
     * Obtient le mot de passe (alias pour getMotDePasseHash)
     * @return Le hash SHA-256 du mot de passe
     */
    public String getMotDePasse() {
        return motDePasseHash;
    }

    /**
     * Définit le mot de passe (alias pour setMotDePasseHash)
     * @param motDePasse Le hash du mot de passe
     */
    public void setMotDePasse(String motDePasse) {
        this.motDePasseHash = motDePasse;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Obtient les contraintes alimentaires du fanfaron
     * Peut être null, vide, ou contenir des informations comme "végétarien", "allergies ..."
     * @return Les contraintes alimentaires
     */
    public String getContraintesAlimentaires() {
        return contraintesAlimentaires;
    }

    public void setContraintesAlimentaires(String contraintesAlimentaires) {
        this.contraintesAlimentaires = contraintesAlimentaires;
    }

    /**
     * Vérifie si le fanfaron a le statut administrateur
     * @return true si administrateur, false sinon
     */
    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * Obtient la date de création du compte
     * @return La date de création
     */
    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    /**
     * Obtient le timestamp de la dernière connexion
     * Utilisé pour suivre l'activité
     * @return Le timestamp de la dernière connexion
     */
    public Timestamp getDerniereConnexion() {
        return derniereConnexion;
    }

    public void setDerniereConnexion(Timestamp derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }
}
