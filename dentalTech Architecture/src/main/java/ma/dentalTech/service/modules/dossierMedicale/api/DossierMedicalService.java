package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.dossierMedical.SituationFinanciere;

import java.util.List;
import java.util.Optional;

public interface DossierMedicalService {

    // --- Core Dossier Management ---
    DossierMedical createDossier(DossierMedical dossier);
    Optional<DossierMedical> getDossierById(Long id);
    Optional<DossierMedical> getDossierByPatientId(Long patientId);
    List<DossierMedical> getAllDossiers();

    // --- Financial Situation ---
    SituationFinanciere getSituationFinanciere(Long dossierId);
    void updateSituationFinanciere(SituationFinanciere situation);

    /**
     * Recalculates the total cost of acts and updates the SituationFinanciere.
     */
    void recalculateFinancialStatus(Long dossierId);

    // --- Documents (Certificats) ---
    void addCertificat(Certificat certificat);
    List<Certificat> getCertificatsByDossier(Long dossierId);
}