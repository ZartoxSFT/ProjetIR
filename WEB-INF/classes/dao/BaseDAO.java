package dao;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseDAO {
    private final DbConnectionManager dbManager;

    protected BaseDAO(DbConnectionManager dbManager) {
        this.dbManager = dbManager;
    }

    protected Connection getConnection() throws SQLException {
        return dbManager.getConnection();
    }
}
