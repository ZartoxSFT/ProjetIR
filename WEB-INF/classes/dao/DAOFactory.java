package dao;

public class DAOFactory {
    private static final DbConnectionManager DB_MANAGER = DbConnectionManager.getInstance();

    private DAOFactory() {
    }

    public static FanfaronDAO getFanfaronDAO() {
        return new FanfaronJDBCDAO(DB_MANAGER);
    }

    public static InstrumentDAO getInstrumentDAO() {
        return new InstrumentJDBCDAO(DB_MANAGER);
    }

    public static EvenementDAO getEvenementDAO() {
        return new EvenementJDBCDAO(DB_MANAGER);
    }

    public static EvenementInscriptionDAO getEvenementInscriptionDAO() {
        return new EvenementInscriptionJDBCDAO(DB_MANAGER);
    }
}
