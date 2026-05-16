package dao;

/**
 * CLASSE FACTORY DAO - Patron de conception Factory
 * 
 * Responsabilités :
 * - Centraliser la création de tous les DAO
 * - Fournir une interface unique pour accéder aux DAO
 * - Gérer les dépendances entre les DAO
 * 
 * Avantages du pattern Factory :
 * - Découplage : les servlets ne connaissent pas les implémentations concrètes
 * - Facilite la maintenance : changement d'implémentation en un seul endroit
 * - Possibilité d'utiliser un conteneur d'injection de dépendances (Spring, etc.)
 * 
 * Exemple d'utilisation :
 * FanfaronDAO dao = DAOFactory.getFanfaronDAO();
 */
public class DAOFactory {
    // Instance unique du gestionnaire de connexions
    // Initialisée une seule fois (lazy loading)
    private static final DbConnectionManager DB_MANAGER = DbConnectionManager.getInstance();

    /**
     * Constructeur privé pour empêcher l'instanciation
     * Cette classe ne doit être utilisée que pour ses méthodes statiques
     */
    private DAOFactory() {
    }

    /**
     * Factory pour obtenir une instance du DAO Fanfaron
     * 
     * @return Une instance de FanfaronJDBCDAO
     */
    public static FanfaronDAO getFanfaronDAO() {
        return new FanfaronJDBCDAO(DB_MANAGER);
    }

    /**
     * Factory pour obtenir une instance du DAO Instrument
     * 
     * @return Une instance de InstrumentJDBCDAO
     */
    public static InstrumentDAO getInstrumentDAO() {
        return new InstrumentJDBCDAO(DB_MANAGER);
    }

    /**
     * Factory pour obtenir une instance du DAO Événement
     * 
     * @return Une instance de EvenementJDBCDAO
     */
    public static EvenementDAO getEvenementDAO() {
        return new EvenementJDBCDAO(DB_MANAGER);
    }

    /**
     * Factory pour obtenir une instance du DAO Inscription à Événement
     * 
     * @return Une instance de EvenementInscriptionJDBCDAO
     */
    public static EvenementInscriptionDAO getEvenementInscriptionDAO() {
        return new EvenementInscriptionJDBCDAO(DB_MANAGER);
    }
}
