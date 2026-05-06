package modele;

public class InscriptionDetail {
    private int idFanfaron;
    private String nomFanfaron;
    private String prenom;
    private String nom;
    private String instrument;
    private String statut;

    public int getIdFanfaron() {
        return idFanfaron;
    }

    public void setIdFanfaron(int idFanfaron) {
        this.idFanfaron = idFanfaron;
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

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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
