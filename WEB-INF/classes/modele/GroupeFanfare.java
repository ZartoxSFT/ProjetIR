package modele;

/**
 * CLASSE MODELE - GROUPE DE FANFARE
 * 
 * Représente un groupe ou une section de la fanfare
 * C'est une POJO utilisée pour mapper les données de la table groupe_fanfare
 * 
 * Exemples de groupes :
 * - Cuivres
 * - Bois
 * - Percussion
 * - Voix (si applicable)
 * - Orchestre
 * - etc.
 * 
 * Relation N:N avec Fanfaron :
 * - Un fanfaron peut appartenir à plusieurs groupes
 * - Un groupe peut contenir plusieurs fanfarons
 * - Cette relation est stockée dans la table fanfaron_groupe
 * 
 * Utilisation :
 * - Organisation des répétitions par groupe
 * - Composition de groupes pour les événements
 * - Gestion des effectifs
 */
public class GroupeFanfare {
    // Identifiant unique du groupe
    private Long id;
    
    // Nom du groupe
    private String nom;

    /**
     * Constructeur par défaut
     * Utilisé par les DAO pour créer des instances
     */
    public GroupeFanfare() {
    }

    /**
     * Constructeur avec paramètres
     * Utilisé lors de la création d'un nouveau groupe
     * 
     * @param id Identifiant unique
     * @param nom Nom du groupe
     */
    public GroupeFanfare(Long id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    /**
     * Obtient l'identifiant unique du groupe
     * @return L'ID en base de données
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtient le nom du groupe
     * @return Le nom (ex: "Cuivres")
     */
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
