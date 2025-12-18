package ma.dentalTech.service.modules.agendas.api;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.enums.Mois;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AgendaMensuelService {

    // CRUD
    List<AgendaMensuel> findAll();
    AgendaMensuel findById(Long id);
    void create(AgendaMensuel agenda);
    void update(AgendaMensuel agenda);
    void deleteById(Long id);

    // Recherche
    Optional<AgendaMensuel> findByMedecinAndMoisAndAnnee(Long medecinId, Mois mois, int annee);
    List<AgendaMensuel> findByMedecin(Long medecinId);

    // Jours non disponibles
    List<LocalDate> getJoursNonDisponibles(Long agendaId);
    void addJourNonDisponible(Long agendaId, LocalDate date);
    void removeJourNonDisponible(Long agendaId, LocalDate date);
    void clearJoursNonDisponibles(Long agendaId);
}
