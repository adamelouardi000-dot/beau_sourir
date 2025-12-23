package ma.dentalTech.service.modules.agendas.impl;

import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.repository.modules.agenda.api.RDVRepository;
import ma.dentalTech.service.modules.agendas.api.RDVService;
import ma.dentalTech.mvc.dto.*;

import java.time.LocalDate;
import java.util.List;

public class RDVServiceImpl implements RDVService {

    private final RDVRepository repo;

    public RDVServiceImpl(RDVRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<RdvDto> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public RdvDto findById(Long id) {
        RDV r = repo.findById(id);
        if (r == null) throw new RuntimeException("RDV introuvable id=" + id);
        return toDto(r);
    }

    @Override
    public RdvDto create(RdvCreateRequest request) {
        validateCreate(request);

        RDV r = new RDV();
        r.setDate(request.date());
        r.setHeure(request.heure());
        r.setMotif(request.motif());
        r.setStatut(request.statut());
        r.setNoteMedecin(request.noteMedecin());

        // Important: repo attend des IDs via champs (agendaMensuelId) + objets patient/medecin/dossier (selon ton repo)
        r.setAgendaMensuelId(request.agendaId());

        repo.create(r);
        return toDto(r);
    }

    @Override
    public RdvDto update(RdvUpdateRequest request) {
        validateUpdate(request);

        RDV existing = repo.findById(request.id());
        if (existing == null) throw new RuntimeException("RDV introuvable id=" + request.id());

        existing.setDate(request.date());
        existing.setHeure(request.heure());
        existing.setMotif(request.motif());
        existing.setStatut(request.statut());
        existing.setNoteMedecin(request.noteMedecin());
        existing.setAgendaMensuelId(request.agendaId());

        repo.update(existing);
        return toDto(existing);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<RdvDto> findByMedecinAndDate(Long medecinId, LocalDate date) {
        return repo.findByMedecinAndDate(medecinId, date).stream().map(this::toDto).toList();
    }

    @Override
    public List<RdvDto> findByMedecinAndDateRange(Long medecinId, LocalDate start, LocalDate end) {
        return repo.findByMedecinAndDateRange(medecinId, start, end).stream().map(this::toDto).toList();
    }

    @Override
    public List<RdvDto> findByPatient(Long patientId) {
        return repo.findByPatient(patientId).stream().map(this::toDto).toList();
    }

    @Override
    public List<RdvDto> findByAgenda(Long agendaId) {
        return repo.findByAgenda(agendaId).stream().map(this::toDto).toList();
    }

    private RdvDto toDto(RDV r) {
        Long patientId = (r.getPatient() != null) ? r.getPatient().getId() : null;
        Long medecinId = (r.getMedecin() != null) ? r.getMedecin().getId() : null;
        Long dossierId = (r.getDossierMedical() != null) ? r.getDossierMedical().getId() : null;

        return new RdvDto(
                r.getId(),
                r.getAgendaMensuelId(),
                patientId,
                medecinId,
                dossierId,
                r.getDate(),
                r.getHeure(),
                r.getMotif(),
                r.getStatut(),
                r.getNoteMedecin()
        );
    }

    private void validateCreate(RdvCreateRequest r) {
        if (r == null) throw new IllegalArgumentException("CreateRequest null");
        if (r.agendaId() == null) throw new IllegalArgumentException("agendaId obligatoire");
        if (r.patientId() == null) throw new IllegalArgumentException("patientId obligatoire");
        if (r.medecinId() == null) throw new IllegalArgumentException("medecinId obligatoire");
        if (r.date() == null) throw new IllegalArgumentException("date obligatoire");
        if (r.heure() == null) throw new IllegalArgumentException("heure obligatoire");
        if (r.statut() == null) throw new IllegalArgumentException("statut obligatoire");
    }

    private void validateUpdate(RdvUpdateRequest r) {
        if (r == null) throw new IllegalArgumentException("UpdateRequest null");
        if (r.id() == null) throw new IllegalArgumentException("id obligatoire");
        validateCreate(new RdvCreateRequest(
                r.agendaId(), r.patientId(), r.medecinId(), r.dossierId(),
                r.date(), r.heure(), r.motif(), r.statut(), r.noteMedecin()
        ));
    }
}
