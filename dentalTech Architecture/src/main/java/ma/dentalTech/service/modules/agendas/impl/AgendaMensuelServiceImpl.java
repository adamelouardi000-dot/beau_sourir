package ma.dentalTech.service.modules.agendas.impl;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.service.modules.agendas.api.AgendaMensuelService;
import ma.dentalTech.mvc.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AgendaMensuelServiceImpl implements AgendaMensuelService {

    private final AgendaMensuelRepository repo;

    public AgendaMensuelServiceImpl(AgendaMensuelRepository repo) {
        this.repo = repo;
    }

    // ---------------- CRUD ----------------

    @Override
    public List<AgendaMensuelDto> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public AgendaMensuelDto findById(Long id) {
        AgendaMensuel a = repo.findById(id);
        if (a == null) throw new RuntimeException("AgendaMensuel introuvable id=" + id);
        return toDto(a);
    }

    @Override
    public AgendaMensuelDto create(AgendaMensuelCreateRequest request) {
        validateCreate(request);

        AgendaMensuel a = new AgendaMensuel();
        a.setMedecinId(request.medecinId());
        a.setMois(request.mois());
        a.setAnnee(request.annee());

        repo.create(a);

        // jours non dispo stockés séparément (table d’association)
        if (request.medecinId() != null) {
            // rien ici
        }

        // recharger jours non dispo pour retour DTO
        a.setJoursNonDisponibles(repo.getJoursNonDisponibles(a.getId()));
        return toDto(a);
    }

    @Override
    public AgendaMensuelDto update(AgendaMensuelUpdateRequest request) {
        validateUpdate(request);

        AgendaMensuel existing = repo.findById(request.id());
        if (existing == null) throw new RuntimeException("AgendaMensuel introuvable id=" + request.id());

        existing.setMedecinId(request.medecinId());
        existing.setMois(request.mois());
        existing.setAnnee(request.annee());

        repo.update(existing);

        existing.setJoursNonDisponibles(repo.getJoursNonDisponibles(existing.getId()));
        return toDto(existing);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    // ---------------- Recherche ----------------

    @Override
    public Optional<AgendaMensuelDto> findByMedecinAndMoisAndAnnee(AgendaMensuelSearchRequest request) {
        if (request == null) throw new IllegalArgumentException("SearchRequest null");
        if (request.medecinId() == null) throw new IllegalArgumentException("medecinId obligatoire");
        if (request.mois() == null) throw new IllegalArgumentException("mois obligatoire");
        if (request.annee() == null) throw new IllegalArgumentException("annee obligatoire");

        return repo.findByMedecinAndMoisAndAnnee(
                request.medecinId(),
                request.mois().name(),
                request.annee()
        ).map(a -> {
            a.setJoursNonDisponibles(repo.getJoursNonDisponibles(a.getId()));
            return toDto(a);
        });
    }

    @Override
    public List<AgendaMensuelDto> findByMedecin(Long medecinId) {
        if (medecinId == null) throw new IllegalArgumentException("medecinId obligatoire");
        return repo.findByMedecin(medecinId).stream().map(a -> {
            a.setJoursNonDisponibles(repo.getJoursNonDisponibles(a.getId()));
            return toDto(a);
        }).toList();
    }

    // ---------------- Jours non disponibles ----------------

    @Override
    public List<LocalDate> getJoursNonDisponibles(Long agendaId) {
        if (agendaId == null) throw new IllegalArgumentException("agendaId obligatoire");
        return repo.getJoursNonDisponibles(agendaId);
    }

    @Override
    public void addJourNonDisponible(Long agendaId, LocalDate date) {
        if (agendaId == null) throw new IllegalArgumentException("agendaId obligatoire");
        if (date == null) throw new IllegalArgumentException("date obligatoire");
        repo.addJourNonDisponible(agendaId, date);
    }

    @Override
    public void removeJourNonDisponible(Long agendaId, LocalDate date) {
        if (agendaId == null) throw new IllegalArgumentException("agendaId obligatoire");
        if (date == null) throw new IllegalArgumentException("date obligatoire");
        repo.removeJourNonDisponible(agendaId, date);
    }

    @Override
    public void clearJoursNonDisponibles(Long agendaId) {
        if (agendaId == null) throw new IllegalArgumentException("agendaId obligatoire");
        repo.clearJoursNonDisponibles(agendaId);
    }

    // ---------------- Mapping ----------------

    private AgendaMensuelDto toDto(AgendaMensuel a) {
        return new AgendaMensuelDto(
                a.getId(),
                a.getMedecinId(),
                a.getMois(),
                a.getAnnee(),
                a.getJoursNonDisponibles()
        );
    }

    // ---------------- Validation ----------------

    private void validateCreate(AgendaMensuelCreateRequest r) {
        if (r == null) throw new IllegalArgumentException("CreateRequest null");
        if (r.medecinId() == null) throw new IllegalArgumentException("medecinId obligatoire");
        if (r.mois() == null) throw new IllegalArgumentException("mois obligatoire");
        if (r.annee() == null) throw new IllegalArgumentException("annee obligatoire");
    }

    private void validateUpdate(AgendaMensuelUpdateRequest r) {
        if (r == null) throw new IllegalArgumentException("UpdateRequest null");
        if (r.id() == null) throw new IllegalArgumentException("id obligatoire");
        if (r.medecinId() == null) throw new IllegalArgumentException("medecinId obligatoire");
        if (r.mois() == null) throw new IllegalArgumentException("mois obligatoire");
        if (r.annee() == null) throw new IllegalArgumentException("annee obligatoire");
    }
}
