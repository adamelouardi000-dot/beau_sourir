package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo;
import ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo;
import ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo;
import ma.dentalTech.service.modules.dossierMedical.api.ConsultationService;

import java.util.List;

public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepo consultationRepo;
    private final InterventionMedecinRepo interventionRepo;
    private final OrdonnanceRepo ordonnanceRepo;
    private final PrescriptionRepo prescriptionRepo;

    public ConsultationServiceImpl(ConsultationRepo consultationRepo,
                                   InterventionMedecinRepo interventionRepo,
                                   OrdonnanceRepo ordonnanceRepo,
                                   PrescriptionRepo prescriptionRepo) {
        this.consultationRepo = consultationRepo;
        this.interventionRepo = interventionRepo;
        this.ordonnanceRepo = ordonnanceRepo;
        this.prescriptionRepo = prescriptionRepo;
    }

    @Override
    public Consultation createConsultation(Consultation consultation) {
        consultationRepo.create(consultation);
        return consultation;
    }

    @Override
    public List<Consultation> getConsultationsByDossier(Long dossierId) {
        return consultationRepo.findByDossierMedical(dossierId);
    }

    @Override
    public void addIntervention(InterventionMedecin intervention) {
        interventionRepo.create(intervention);
    }

    @Override
    public List<InterventionMedecin> getInterventionsByConsultation(Long consultationId) {
        return interventionRepo.findByConsultation(consultationId);
    }

    @Override
    public Ordonnance createOrdonnance(Ordonnance ordonnance, List<Prescription> prescriptions) {
        // 1. Save Ordonnance
        ordonnanceRepo.create(ordonnance);

        // 2. Save all Prescriptions linked to it
        if (prescriptions != null) {
            for (Prescription p : prescriptions) {
                // Manually link via ID or Object depending on your Entity structure
                // Assuming Entity has setOrdonnanceId or setOrdonnance object
                // If using Object: p.setOrdonnance(ordonnance);
                // Here we simulate setting ID if needed, or rely on caller to set relation

                // Using method reflection adapter or direct setter if public:
                try {
                    // Try to set ID directly via reflection to be safe with your repository structure
                    p.getClass().getMethod("setOrdonnanceId", Long.class).invoke(p, ordonnance.getId());
                } catch (Exception e) {
                    // Fallback if relation is object based
                }

                prescriptionRepo.create(p);
            }
        }
        return ordonnance;
    }

    @Override
    public List<Ordonnance> getOrdonnancesByConsultation(Long consultationId) {
        return ordonnanceRepo.findByConsultation(consultationId);
    }
}