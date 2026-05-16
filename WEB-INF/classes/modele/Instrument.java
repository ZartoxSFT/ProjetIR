package modele;

/**
 * CLASSE MODELE - INSTRUMENT
 * 
 * Représente un instrument de musique que peut jouer un fanfaron
 * C'est une POJO utilisée pour mapper les données de la table instrument
 * 
 * Exemples d'instruments :
 * - Trompette
 * - Trombone
 * - Tuba
 * - Clarinette
 * - Flûte
 * - Tambour
 * - etc.
 * 
 * Relation N:N avec Fanfaron :
 * - Un fanfaron peut jouer plusieurs instruments
 * - Un instrument peut être joué par plusieurs fanfarons
 * - Cette relation est stockée dans la table fanfaron_instrument
 */
public class Instrument {

    // Identifiant unique de l'instrument
    private Long id;
    
    // Nom de l'instrument
    private String nom;

    /**
     * Constructeur par défaut
     * Utilisé par les DAO pour créer des instances
     */
    public Instrument() {
    }

    /**
     * Constructeur avec paramètres
     * Utilisé lors de la création d'un nouvel instrument
     * 
     * @param id Identifiant unique
     * @param nom Nom de l'instrument
     */
    public Instrument(Long id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    /**
     * Obtient l'identifiant unique de l'instrument
     * @return L'ID en base de données
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtient le nom de l'instrument
     * @return Le nom (ex: "Trompette")
     */
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}