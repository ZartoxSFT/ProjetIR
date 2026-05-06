package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modele.Instrument;
import modele.GroupeFanfare;

public class InstrumentDAO extends BaseDAO {

    public boolean insert(Instrument instrument) {
        String sql = "INSERT INTO instrument (nom) VALUES (?)";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, instrument.getNom());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Instrument> findAllInstruments() {
        List<Instrument> instruments = new ArrayList<>();
        String sql = "SELECT id, nom FROM instrument";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                String nom = rs.getString("nom");
                instruments.add(new Instrument(id, nom));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return instruments;
        }
        return instruments;
    }

    public List<GroupeFanfare> findAllGroupes() {
        List<GroupeFanfare> groupes = new ArrayList<>();
        String sql = "SELECT id, nom FROM groupe_fanfare";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                String nom = rs.getString("nom");
                groupes.add(new GroupeFanfare(id, nom));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return groupes;
        }
        return groupes;
    }

    public List<Long> findInstrumentIdsByFanfaron(Long idFanfaron){
        List<Long> instrumentIds = new ArrayList<>();
        String sql = "SELECT id_instrument FROM fanfaron_instrument WHERE id_fanfaron = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setLong(1, idFanfaron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    instrumentIds.add(rs.getLong("id_instrument"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return instrumentIds;
        }
        return instrumentIds;
    }

    public List<Long> findGroupeIdsByFanfaron(Long idFanfaron){
        List<Long> groupeIds = new ArrayList<>();
        String sql = "SELECT id_groupe FROM fanfaron_groupe WHERE id_fanfaron = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setLong(1, idFanfaron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    groupeIds.add(rs.getLong("id_groupe"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return groupeIds;
        }
        return groupeIds;
    }
}
