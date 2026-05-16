package modele;

/**
 * CLASSE MODELE - INSCRIPTION DETAIL
 * 
 * Représente les détails d'une inscription d'un fanfaron à un événement
 * C'est une vue enrichie qui combine les données du fanfaron avec ses données d'inscription
 * 
 * Utilisée pour afficher les inscriptions à un événement spécifique
 * 
 * Attributs :
 * - Informations du fanfaron : idFanfaron, nomFanfaron, prenom, nom
 * - Informations d'inscription : instrument, statut
 * 
 * Exemple d'utilisation :
 * Lors du clic sur un événement, afficher la liste de tous les fanfarons inscrits
 * avec leurs détails personnels et leur statut de participation
 * 
 * Statuts possibles :
 * - "present" : Confirmé présent
 * - "absent" : Confirmé absent / Annulé
 * - "incertain" : En attente de confirmation
 */
public class InscriptionDetail {
    // Identifiant du fanfaron inscrit
    private int idFanfaron;
    
    // Informations du fanfaron
    private String nomFanfaron; // Nom d'utilisateur
    private String prenom;      // Prénom du fanfaron
    private String nom;         // Nom de famille du fanfaron
    
    // Informations d'inscription
    private String instrument;  // L'instrument joué pour cet événement
    private String statut;      // Statut de participation : present, absent, incertain

    /**
     * Obtient l'identifiant du fanfaron inscrit
     * @return L'ID du fanfaron
     */
    public int getIdFanfaron() {
        return idFanfaron;
    }

    public void setIdFanfaron(int idFanfaron) {
        this.idFanfaron = idFanfaron;
    }

    /**
     * Obtient le nom d'utilisateur du fanfaron
     * @return Le nom d'utilisateur
     */
    public String getNomFanfaron() {
        return nomFanfaron;
    }

    public void setNomFanfaron(String nomFanfaron) {
        this.nomFanfaron = nomFanfaron;
    }

    /**
     * Obtient le prénom du fanfaron
     * @return Le prénom
     */
    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * Obtient le nom de famille du fanfaron
     * @return Le nom
     */
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Obtient l'instrument que le fanfaron joue à cet événement
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
