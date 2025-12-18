package ma.dentalTech.service.modules.agendas.api;

import ma.dentalTech.entities.agenda.RDV;

import java.time.LocalDate;
import java.util.List;

public interface RDVService {

    // CRUD
    List<RDV> findAll();
    RDV findById(Long id);
    void create(RDV rdv);
    void update(RDV rdv);
    void deleteById(Long id);

    // Recherche
    List<RDV> findByMedecinAndDate(Long medecinId, LocalDate date);
    List<RDV> findByMedecinAndDateRange(Long medecinId, LocalDate start, LocalDate end);
    List<RDV> findByPatient(Long patientId);
    List<RDV> findByAgenda(Long agendaId);
}
