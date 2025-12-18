package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;

import java.util.List;

public interface ConsultationService {

    // --- Consultation Management ---
    Consultation createConsultation(Consultation consultation);
    List<Consultation> getConsultationsByDossier(Long dossierId);

    // --- Interventions (Acts performed during consultation) ---
    void addIntervention(InterventionMedecin intervention);
    List<InterventionMedecin> getInterventionsByConsultation(Long consultationId);

    // --- Prescriptions (Ordonnances) ---
    /**
     * Creates an Ordonnance and saves its list of Prescriptions.
     */
    Ordonnance createOrdonnance(Ordonnance ordonnance, List<Prescription> prescriptions);
    List<Ordonnance> getOrdonnancesByConsultation(Long consultationId);
}