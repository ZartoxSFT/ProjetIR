package dao;

import java.sql.SQLException;
import java.util.List;

import modele.Fanfaron;

public interface FanfaronDAO {
    Fanfaron getById(int id) throws SQLException;

    Fanfaron getByNomFanfaron(String nomFanfaron) throws SQLException;

    Fanfaron getByEmail(String email) throws SQLException;

    List<Fanfaron> getAll() throws SQLException;

    void create(Fanfaron fanfaron) throws SQLException;

    void update(Fanfaron fanfaron) throws SQLException;

    void delete(int id) throws SQLException;

    void updateDerniereConnexion(Long id) throws SQLException;

    Fanfaron authenticate(String nomFanfaron, String motDePasseHash) throws SQLException;

    boolean existsByNomFanfaron(String nomFanfaron) throws SQLException;

    boolean existsByEmail(String email) throws SQLException;

    List<Fanfaron> getAllFanfarons();

    Fanfaron getFanfaronById(long id);

    boolean addFanfaron(Fanfaron fanfaron);

    boolean updateFanfaron(Fanfaron fanfaron);

    boolean deleteFanfaron(long id);

    boolean isMemberOfCommissionPrestation(long idFanfaron);
}
