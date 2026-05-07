package dao;

import java.util.List;

import modele.GroupeFanfare;
import modele.Instrument;

public interface InstrumentDAO {
    boolean insertInstrument(Instrument instrument);

    List<Instrument> findAllInstruments();

    List<GroupeFanfare> findAllGroupes();

    List<Long> findInstrumentIdsByFanfaron(Long idFanfaron);

    List<Long> findGroupeIdsByFanfaron(Long idFanfaron);

    List<Instrument> findInstrumentsByFanfaron(Long idFanfaron);

    List<GroupeFanfare> findGroupesByFanfaron(Long idFanfaron);

    boolean updateInstrumentsFanfaron(Long idFanfaron, String[] instruments);

    boolean updateGroupesFanfaron(Long idFanfaron, String[] groupes);

    boolean deleteInstrument(Long id);

    boolean updateInstrument(Instrument instrument);

    boolean insertGroupe(GroupeFanfare groupe);

    boolean updateGroupe(GroupeFanfare groupe);

    boolean deleteGroupe(Long id);
}
