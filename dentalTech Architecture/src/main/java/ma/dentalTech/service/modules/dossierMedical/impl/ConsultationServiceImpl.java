package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.mvc.dto.ConsultationCreateRequest;
import ma.dentalTech.mvc.dto.ConsultationDto;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo;
import ma.dentalTech.service.modules.dossierMedical.api.ConsultationService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepo consultationRepo;

    public ConsultationServiceImpl(ConsultationRepo consultationRepo) {
        this.consultationRepo = consultationRepo;
    }

    @Override
    public ConsultationDto create(ConsultationCreateRequest request) {
        if (request == null || request.dossierMedicalId() == null)
            throw new IllegalArgumentException("dossierMedicalId obligatoire");

        Consultation c = new Consultation();
        c.setDateConsultation(LocalDate.now());
        c.setStatut(StatutConsultation.PLANIFIEE);
        // adapte si ton enum diffère
        c.setObservationMedecin(request.observation());

        // Relation vers dossierMedical (on met juste l’id)
        DossierMedical dm = new DossierMedical();
        dm.setId(request.dossierMedicalId());
        c.setDossierMedical(dm);

        consultationRepo.create(c);
        return toDto(c);
    }

    @Override
    public List<ConsultationDto> getByDossierMedical(Long dossierMedicalId) {
        return consultationRepo.findByDossierMedical(dossierMedicalId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ConsultationDto toDto(Consultation c) {
        return new ConsultationDto(
                c.getId(),
                c.getDateConsultation(),
                null, // ton DTO a "motif" mais ton entity n’a pas motif -> null
                c.getObservationMedecin()
        );
    }
}
