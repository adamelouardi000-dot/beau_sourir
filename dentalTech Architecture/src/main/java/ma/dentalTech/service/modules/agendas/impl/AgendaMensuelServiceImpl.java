package ma.dentalTech.service.modules.agendas.impl;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.service.modules.agendas.api.AgendaMensuelService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AgendaMensuelServiceImpl implements AgendaMensuelService {

    private final AgendaMensuelRepository agendaRepo;

    public AgendaMensuelServiceImpl(AgendaMensuelRepository agendaRepo) {
        this.agendaRepo = agendaRepo;
    }

    @Override
    public List<AgendaMensuel> findAll() {
        return agendaRepo.findAll();
    }

    @Override
    public AgendaMensuel findById(Long id) {
        if (id == null) throw new IllegalArgumentException("id ne doit pas être null");
        AgendaMensuel a = agendaRepo.findById(id);
        if (a == null) throw new IllegalArgumentException("Agenda introuvable (id=" + id + ")");
        return a;
    }

    @Override
    public void create(AgendaMensuel agenda) {
        if (agenda == null) throw new IllegalArgumentException("agenda ne doit pas être null");
        if (agenda.getMedecinId() == null) throw new IllegalArgumentException("medecinId obligatoire");
        if (agenda.getMois() == null) throw new IllegalArgumentException("mois obligatoire");
        if (agenda.getAnnee() == null) throw new IllegalArgumentException("annee obligatoire");

        // éviter doublon (même médecin / mois / année)
        Optional<AgendaMensuel> existing =
                agendaRepo.findByMedecinAndMoisAndAnnee(agenda.getMedecinId(), agenda.getMois().name(), agenda.getAnnee());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Agenda déjà existant pour ce médecin/mois/année");
        }

        agendaRepo.create(agenda);
    }

    @Override
    public void update(AgendaMensuel agenda) {
        if (agenda == null) throw new IllegalArgumentException("agenda ne doit pas être null");
        if (agenda.getId() == null) throw new IllegalArgumentException("id obligatoire pour update");
        AgendaMensuel old = agendaRepo.findById(agenda.getId());
        if (old == null) throw new IllegalArgumentException("Agenda introuvable (id=" + agenda.getId() + ")");
        agendaRepo.update(agenda);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("id ne doit pas être null");
        agendaRepo.deleteById(id);
    }

    @Override
    public Optional<AgendaMensuel> findByMedecinAndMoisAndAnnee(Long medecinId, Mois mois, int annee) {
        if (medecinId == null) throw new IllegalArgumentException("medecinId ne doit pas être null");
        if (mois == null) throw new IllegalArgumentException("mois ne doit pas être null");
        return agendaRepo.findByMedecinAndMoisAndAnnee(medecinId, mois.name(), annee);
    }

    @Override
    public List<AgendaMensuel> findByMedecin(Long medecinId) {
        if (medecinId == null) throw new IllegalArgumentException("medecinId ne doit pas être null");
        return agendaRepo.findByMedecin(medecinId);
    }

    @Override
    public List<LocalDate> getJoursNonDisponibles(Long agendaId) {
        if (agendaId == null) throw new IllegalArgumentException("agendaId ne doit pas être null");
        return agendaRepo.getJoursNonDisponibles(agendaId);
    }

    @Override
    public void addJourNonDisponible(Long agendaId, LocalDate date) {
        if (agendaId == null) throw new IllegalArgumentException("agendaId ne doit pas être null");
        if (date == null) throw new IllegalArgumentException("date ne doit pas être null");
        agendaRepo.addJourNonDisponible(agendaId, date);
    }

    @Override
    public void removeJourNonDisponible(Long agendaId, LocalDate date) {
        if (agendaId == null) throw new IllegalArgumentException("agendaId ne doit pas être null");
        if (date == null) throw new IllegalArgumentException("date ne doit pas être null");
        agendaRepo.removeJourNonDisponible(agendaId, date);
    }

    @Override
    public void clearJoursNonDisponibles(Long agendaId) {
        if (agendaId == null) throw new IllegalArgumentException("agendaId ne doit pas être null");
        agendaRepo.clearJoursNonDisponibles(agendaId);
    }
}
