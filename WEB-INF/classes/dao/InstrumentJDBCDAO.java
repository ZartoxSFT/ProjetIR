package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import modele.Instrument;

public class InstrumentJDBCDAO {

    private Connection getConnection() throws Exception {
        Properties props = new Properties();

        InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties");
        if (input == null) {
            throw new Exception("Fichier db.properties introuvable");
        }

        props.load(input);

        Class.forName("org.postgresql.Driver");

        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password"));
    }

    public List<Instrument> getAllInstruments() {
        String sql = "SELECT id, nom FROM instrument ORDER BY nom";
        List<Instrument> instruments = new ArrayList<>();

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                instruments.add(new Instrument(rs.getInt("id"), rs.getString("nom")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instruments;
    }
}
