package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.mvc.dto.OrdonnanceCreateRequest;
import ma.dentalTech.mvc.dto.OrdonnanceDto;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo;
import ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo;
import ma.dentalTech.service.modules.dossierMedical.api.OrdonnanceService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class OrdonnanceServiceImpl implements OrdonnanceService {

    private final OrdonnanceRepo ordonnanceRepo;
    private final ConsultationRepo consultationRepo;

    public OrdonnanceServiceImpl(OrdonnanceRepo ordonnanceRepo, ConsultationRepo consultationRepo) {
        this.ordonnanceRepo = ordonnanceRepo;
        this.consultationRepo = consultationRepo;
    }

    @Override
    public OrdonnanceDto create(OrdonnanceCreateRequest request) {
        if (request == null || request.consultationId() == null)
            throw new IllegalArgumentException("consultationId obligatoire");

        // On vérifie que la consultation existe (CrudRepository<Consultation, Long>)
        Consultation c = consultationRepo.findById(request.consultationId());
        if (c == null) throw new IllegalArgumentException("Consultation introuvable");

        Ordonnance o = new Ordonnance();
        o.setDate(LocalDate.now());

        // D’après ton entity: Ordonnance possède dossierMedical et medecin
        o.setDossierMedical(c.getDossierMedical());
        o.setMedecin(c.getMedecin());

        ordonnanceRepo.create(o);

        return new OrdonnanceDto(o.getId(), o.getDate());
    }

    @Override
    public List<OrdonnanceDto> getByConsultation(Long consultationId) {
        return ordonnanceRepo.findByConsultation(consultationId)
                .stream()
                .map(o -> new OrdonnanceDto(o.getId(), o.getDate()))
                .collect(Collectors.toList());
    }
}
