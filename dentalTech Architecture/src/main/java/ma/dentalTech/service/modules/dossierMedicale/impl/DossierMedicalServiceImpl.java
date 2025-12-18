package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.*;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import ma.dentalTech.repository.modules.dossierMedical.api.*;
import ma.dentalTech.service.modules.dossierMedical.api.DossierMedicalService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DossierMedicalServiceImpl implements DossierMedicalService {

    private final DossierMedicalRepo dossierRepo;
    private final SituationFinanciereRepo situationRepo;
    private final CertificatRepo certificatRepo;
    private final ConsultationRepo consultationRepo;
    private final InterventionMedecinRepo interventionRepo;

    public DossierMedicalServiceImpl(DossierMedicalRepo dossierRepo,
                                     SituationFinanciereRepo situationRepo,
                                     CertificatRepo certificatRepo,
                                     ConsultationRepo consultationRepo,
                                     InterventionMedecinRepo interventionRepo) {
        this.dossierRepo = dossierRepo;
        this.situationRepo = situationRepo;
        this.certificatRepo = certificatRepo;
        this.consultationRepo = consultationRepo;
        this.interventionRepo = interventionRepo;
    }

    @Override
    public DossierMedical createDossier(DossierMedical dossier) {
        // 1. Create the Dossier
        dossierRepo.create(dossier);

        // 2. Automatically initialize an empty Financial Situation
        SituationFinanciere sf = new SituationFinanciere();
        sf.setDossierMedical(dossier);
        sf.setTotaleDesActes(0.0);
        sf.setTotalePaye(0.0);
        sf.setCredit(0.0);
        sf.setStatut(StatutSituationFinanciere.EN_REGLE);
        sf.setDateCreation(LocalDate.now());
        situationRepo.create(sf);

        return dossier;
    }

    @Override
    public Optional<DossierMedical> getDossierById(Long id) {
        return Optional.ofNullable(dossierRepo.findById(id));
    }

    @Override
    public Optional<DossierMedical> getDossierByPatientId(Long patientId) {
        return dossierRepo.findByPatient(patientId);
    }

    @Override
    public List<DossierMedical> getAllDossiers() {
        return dossierRepo.findAll();
    }

    @Override
    public SituationFinanciere getSituationFinanciere(Long dossierId) {
        return situationRepo.findByDossierMedical(dossierId)
                .orElseThrow(() -> new RuntimeException("No financial situation found for dossier " + dossierId));
    }

    @Override
    public void updateSituationFinanciere(SituationFinanciere situation) {
        // Recalculate credit before saving
        double total = situation.getTotaleDesActes() != null ? situation.getTotaleDesActes() : 0.0;
        double paid = situation.getTotalePaye() != null ? situation.getTotalePaye() : 0.0;
        situation.setCredit(total - paid);

        situationRepo.update(situation);
    }

    @Override
    public void recalculateFinancialStatus(Long dossierId) {
        // 1. Get all consultations for this dossier
        List<Consultation> consultations = consultationRepo.findByDossierMedical(dossierId);

        // 2. Sum up all interventions costs
        double totalCost = 0.0;
        for (Consultation c : consultations) {
            List<InterventionMedecin> acts = interventionRepo.findByConsultation(c.getId());
            totalCost += acts.stream().mapToDouble(InterventionMedecin::getPrixPatient).sum();
        }

        // 3. Update Situation
        SituationFinanciere sf = getSituationFinanciere(dossierId);
        sf.setTotaleDesActes(totalCost);
        updateSituationFinanciere(sf);
    }

    @Override
    public void addCertificat(Certificat certificat) {
        certificatRepo.create(certificat);
    }

    @Override
    public List<Certificat> getCertificatsByDossier(Long dossierId) {
        return certificatRepo.findByDossierMedical(dossierId);
    }
}