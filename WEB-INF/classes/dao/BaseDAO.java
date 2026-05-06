package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class BaseDAO {

    protected Connection getConnection() throws SQLException {

        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new SQLException("Fichier db.properties introuvable");
            }

            Properties props = new Properties();
            props.load(input);

            Class.forName("org.postgresql.Driver");

            return DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password"));

        } catch (Exception e) {

            if (e instanceof SQLException) {
                throw (SQLException) e;
            }

            throw new SQLException(
                    "Impossible d'ouvrir la connexion a la base",
                    e
            );
        }
    }
}