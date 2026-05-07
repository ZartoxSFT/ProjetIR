package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modele.Instrument;
import modele.GroupeFanfare;

public class InstrumentJDBCDAO extends BaseDAO implements InstrumentDAO {

    public InstrumentJDBCDAO(DbConnectionManager dbManager) {
        super(dbManager);
    }

    public boolean insertInstrument(Instrument instrument) {
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

    public List<Instrument> findInstrumentsByFanfaron(Long idFanfaron) {
        List<Instrument> instruments = new ArrayList<>();
        String sql = "SELECT i.id, i.nom "
                + "FROM fanfaron_instrument fi "
                + "JOIN instrument i ON i.id = fi.id_instrument "
                + "WHERE fi.id_fanfaron = ? "
                + "ORDER BY i.nom";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setLong(1, idFanfaron);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    instruments.add(new Instrument(rs.getLong("id"), rs.getString("nom")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instruments;
    }

    public List<GroupeFanfare> findGroupesByFanfaron(Long idFanfaron) {
        List<GroupeFanfare> groupes = new ArrayList<>();
        String sql = "SELECT g.id, g.nom "
                + "FROM fanfaron_groupe fg "
                + "JOIN groupe_fanfare g ON g.id = fg.id_groupe "
                + "WHERE fg.id_fanfaron = ? "
                + "ORDER BY g.nom";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setLong(1, idFanfaron);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    groupes.add(new GroupeFanfare(rs.getLong("id"), rs.getString("nom")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return groupes;
    }

    public boolean updateInstrumentsFanfaron(Long idFanfaron, String[] instruments) {
        String sqlDelete = "DELETE FROM fanfaron_instrument WHERE id_fanfaron = ?";
        String sqlInsert = "INSERT INTO fanfaron_instrument (id_fanfaron, id_instrument) VALUES (?, ?)";

        try (Connection connexion = getConnection();
            PreparedStatement psDelete = connexion.prepareStatement(sqlDelete);
            PreparedStatement psInsert = connexion.prepareStatement(sqlInsert)) {

            psDelete.setLong(1, idFanfaron);
            psDelete.executeUpdate();

            if (instruments != null) {
                for (String idInstrumentStr : instruments) {
                    psInsert.setLong(1, idFanfaron);
                    psInsert.setLong(2, Long.parseLong(idInstrumentStr));
                    psInsert.addBatch();
                }

                psInsert.executeBatch();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateGroupesFanfaron(Long idFanfaron, String[] groupes) {
        String sqlDelete = "DELETE FROM fanfaron_groupe WHERE id_fanfaron = ?";
        String sqlInsert = "INSERT INTO fanfaron_groupe (id_fanfaron, id_groupe) VALUES (?, ?)";

        try (Connection connexion = getConnection();
            PreparedStatement psDelete = connexion.prepareStatement(sqlDelete);
            PreparedStatement psInsert = connexion.prepareStatement(sqlInsert)) {

            psDelete.setLong(1, idFanfaron);
            psDelete.executeUpdate();

            if (groupes != null) {
                for (String idGroupeStr : groupes) {
                    psInsert.setLong(1, idFanfaron);
                    psInsert.setLong(2, Long.parseLong(idGroupeStr));
                    psInsert.addBatch();
                }

                psInsert.executeBatch();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteInstrument(Long id) {
        String sqlDeleteAssociations = "DELETE FROM fanfaron_instrument WHERE id_instrument = ?";
        String sqlDeleteInstrument = "DELETE FROM instrument WHERE id = ?";

        try (Connection connexion = getConnection();
            PreparedStatement psAssoc = connexion.prepareStatement(sqlDeleteAssociations);
            PreparedStatement psInstrument = connexion.prepareStatement(sqlDeleteInstrument)) {

            psAssoc.setLong(1, id);
            psAssoc.executeUpdate();

            psInstrument.setLong(1, id);
            return psInstrument.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateInstrument(Instrument instrument) {
        String sql = "UPDATE instrument SET nom = ? WHERE id = ?";

        try (Connection connexion = getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, instrument.getNom());
            ps.setLong(2, instrument.getId());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean insertGroupe(GroupeFanfare groupe) {
        String sql = "INSERT INTO groupe_fanfare (nom) VALUES (?)";

        try (Connection connexion = getConnection();
            PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, groupe.getNom());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateGroupe(GroupeFanfare groupe) {
        String sql = "UPDATE groupe_fanfare SET nom = ? WHERE id = ?";

        try (Connection connexion = getConnection();
            PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, groupe.getNom());
            ps.setLong(2, groupe.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteGroupe(Long id) {
        String sqlDeleteAssociations = "DELETE FROM fanfaron_groupe WHERE id_groupe = ?";
        String sqlDeleteGroupe = "DELETE FROM groupe_fanfare WHERE id = ?";

        try (Connection connexion = getConnection();
            PreparedStatement psAssoc = connexion.prepareStatement(sqlDeleteAssociations);
            PreparedStatement psGroupe = connexion.prepareStatement(sqlDeleteGroupe)) {

            psAssoc.setLong(1, id);
            psAssoc.executeUpdate();

            psGroupe.setLong(1, id);
            return psGroupe.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
