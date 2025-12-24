package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.dto.DossierMedicalCreateRequest;
import ma.dentalTech.mvc.dto.DossierMedicalDto;
import ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo;
import ma.dentalTech.service.modules.dossierMedical.api.DossierMedicalService;

import java.time.LocalDate;
import java.util.Optional;

public class DossierMedicalServiceImpl implements DossierMedicalService {

    private final DossierMedicalRepo dossierRepo;

    public DossierMedicalServiceImpl(DossierMedicalRepo dossierRepo) {
        this.dossierRepo = dossierRepo;
    }

    @Override
    public DossierMedicalDto create(DossierMedicalCreateRequest request) {

        if (request == null || request.patientId() == null)
            throw new IllegalArgumentException("patientId obligatoire");

        // ✅ méthode EXACTE du repo
        Optional<DossierMedical> exist = dossierRepo.findByPatient(request.patientId());
        if (exist.isPresent())
            throw new IllegalStateException("Le patient a déjà un dossier médical");

        DossierMedical dossier = new DossierMedical();

        // relation Patient (pas patientId direct)
        Patient p = new Patient();
        p.setId(request.patientId());
        dossier.setPatient(p);

        dossier.setDateCreation(LocalDate.now());

        // ⚠️ pas de setNumero / getNumero dans ton entity → on n’y touche pas

        dossierRepo.create(dossier);

        return toDto(dossier);
    }

    @Override
    public DossierMedicalDto getByPatientId(Long patientId) {

        Optional<DossierMedical> opt = dossierRepo.findByPatient(patientId);
        DossierMedical dossier = opt.orElseThrow(
                () -> new IllegalArgumentException("Dossier médical introuvable")
        );

        return toDto(dossier);
    }

    private DossierMedicalDto toDto(DossierMedical d) {
        Long pid = d.getPatient() != null ? d.getPatient().getId() : null;

        return new DossierMedicalDto(
                d.getId(),
                pid,
                null,               // pas de champ numero exposé
                d.getDateCreation()
        );
    }
}
