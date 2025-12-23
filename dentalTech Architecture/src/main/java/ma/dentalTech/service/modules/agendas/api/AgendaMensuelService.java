package ma.dentalTech.service.modules.agendas.api;

import ma.dentalTech.mvc.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AgendaMensuelService {

    // CRUD DTO
    List<AgendaMensuelDto> findAll();
    AgendaMensuelDto findById(Long id);
    AgendaMensuelDto create(AgendaMensuelCreateRequest request);
    AgendaMensuelDto update(AgendaMensuelUpdateRequest request);
    void deleteById(Long id);

    // Recherche DTO
    Optional<AgendaMensuelDto> findByMedecinAndMoisAndAnnee(AgendaMensuelSearchRequest request);
    List<AgendaMensuelDto> findByMedecin(Long medecinId);

    // Jours non disponibles
    List<LocalDate> getJoursNonDisponibles(Long agendaId);
    void addJourNonDisponible(Long agendaId, LocalDate date);
    void removeJourNonDisponible(Long agendaId, LocalDate date);
    void clearJoursNonDisponibles(Long agendaId);
}
