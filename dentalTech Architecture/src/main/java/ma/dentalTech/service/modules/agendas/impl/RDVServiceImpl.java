package ma.dentalTech.service.modules.agendas.impl;

import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.repository.modules.agenda.api.RDVRepository;
import ma.dentalTech.service.modules.agendas.api.RDVService;

import java.time.LocalDate;
import java.util.List;

public class RDVServiceImpl implements RDVService {

    private final RDVRepository rdvRepo;

    public RDVServiceImpl(RDVRepository rdvRepo) {
        this.rdvRepo = rdvRepo;
    }

    @Override
    public List<RDV> findAll() {
        return rdvRepo.findAll();
    }

    @Override
    public RDV findById(Long id) {
        if (id == null) throw new IllegalArgumentException("id ne doit pas être null");
        RDV r = rdvRepo.findById(id);
        if (r == null) throw new IllegalArgumentException("RDV introuvable (id=" + id + ")");
        return r;
    }

    @Override
    public void create(RDV rdv) {
        if (rdv == null) throw new IllegalArgumentException("rdv ne doit pas être null");
        if (rdv.getDate() == null) throw new IllegalArgumentException("date obligatoire");
        if (rdv.getHeure() == null) throw new IllegalArgumentException("heure obligatoire");
        if (rdv.getMotif() == null || rdv.getMotif().isBlank()) throw new IllegalArgumentException("motif obligatoire");
        if (rdv.getStatut() == null) throw new IllegalArgumentException("statut obligatoire");
        rdvRepo.create(rdv);
    }

    @Override
    public void update(RDV rdv) {
        if (rdv == null) throw new IllegalArgumentException("rdv ne doit pas être null");
        if (rdv.getId() == null) throw new IllegalArgumentException("id obligatoire pour update");
        RDV old = rdvRepo.findById(rdv.getId());
        if (old == null) throw new IllegalArgumentException("RDV introuvable (id=" + rdv.getId() + ")");
        rdvRepo.update(rdv);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("id ne doit pas être null");
        rdvRepo.deleteById(id);
    }

    @Override
    public List<RDV> findByMedecinAndDate(Long medecinId, LocalDate date) {
        if (medecinId == null) throw new IllegalArgumentException("medecinId ne doit pas être null");
        if (date == null) throw new IllegalArgumentException("date ne doit pas être null");
        return rdvRepo.findByMedecinAndDate(medecinId, date);
    }

    @Override
    public List<RDV> findByMedecinAndDateRange(Long medecinId, LocalDate start, LocalDate end) {
        if (medecinId == null) throw new IllegalArgumentException("medecinId ne doit pas être null");
        if (start == null || end == null) throw new IllegalArgumentException("start/end obligatoires");
        return rdvRepo.findByMedecinAndDateRange(medecinId, start, end);
    }

    @Override
    public List<RDV> findByPatient(Long patientId) {
        if (patientId == null) throw new IllegalArgumentException("patientId ne doit pas être null");
        return rdvRepo.findByPatient(patientId);
    }

    @Override
    public List<RDV> findByAgenda(Long agendaId) {
        if (agendaId == null) throw new IllegalArgumentException("agendaId ne doit pas être null");
        return rdvRepo.findByAgenda(agendaId);
    }
}
