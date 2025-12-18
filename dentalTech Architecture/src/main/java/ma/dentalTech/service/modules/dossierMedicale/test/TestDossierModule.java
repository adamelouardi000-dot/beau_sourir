package ma.dentalTech.service.modules.dossierMedicale.test;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.entities.dossierMedical.SituationFinanciere;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.repository.modules.dossierMedical.impl.*;
import ma.dentalTech.service.modules.dossierMedical.api.ConsultationService;
import ma.dentalTech.service.modules.dossierMedical.api.DossierMedicalService;
import ma.dentalTech.service.modules.dossierMedical.impl.ConsultationServiceImpl;
import ma.dentalTech.service.modules.dossierMedical.impl.DossierMedicalServiceImpl;

import java.time.LocalDate;

public class TestDossierModule {

    public static void main(String[] args) {
        System.out.println("--- 1. Initialize Repositories ---");
        DossierMedicalRepoImpl dossierRepo = new DossierMedicalRepoImpl();
        SituationFinanciereRepoImpl situationRepo = new SituationFinanciereRepoImpl();
        CertificatRepoImpl certificatRepo = new CertificatRepoImpl();
        ConsultationRepoImpl consultationRepo = new ConsultationRepoImpl();
        InterventionMedecinRepoImpl interventionRepo = new InterventionMedecinRepoImpl();
        OrdonnanceRepoImpl ordonnanceRepo = new OrdonnanceRepoImpl();
        PrescriptionRepoImpl prescriptionRepo = new PrescriptionRepoImpl();

        System.out.println("--- 2. Initialize Services ---");
        DossierMedicalService dossierService = new DossierMedicalServiceImpl(
                dossierRepo, situationRepo, certificatRepo, consultationRepo, interventionRepo
        );

        ConsultationService consultationService = new ConsultationServiceImpl(
                consultationRepo, interventionRepo, ordonnanceRepo, prescriptionRepo
        );

        System.out.println("--- 3. Create a Dossier ---");
        DossierMedical myDossier = new DossierMedical();
        // Assume Patient ID 1 exists in DB, or this might fail depending on DB constraints
        // Using reflection helper to set Patient ID if needed or just creating the obj
        // myDossier.setPatient(patientObj);

        myDossier.setCreePar("Admin");
        dossierService.createDossier(myDossier);
        System.out.println("✅ Dossier Created ID: " + myDossier.getId());

        System.out.println("--- 4. Create a Consultation ---");
        Consultation consult = new Consultation();
        consult.setDossierMedical(myDossier);
        consult.setDateConsultation(LocalDate.now());
        consult.setStatut(StatutConsultation.EN_ATTENTE); // Ensure Enum exists
        consult.setObservationMedecin("Routine Checkup");

        consultationService.createConsultation(consult);
        System.out.println("✅ Consultation Created ID: " + consult.getId());

        System.out.println("--- 5. Add Intervention (Medical Act) ---");
        InterventionMedecin act = new InterventionMedecin();
        act.setConsultationId(consult.getId());
        act.setPrixPatient(500.0); // Cost of the act
        act.setNumDent(12);

        consultationService.addIntervention(act);
        System.out.println("✅ Intervention Added: Cost 500.0");

        System.out.println("--- 6. Recalculate Financials ---");
        dossierService.recalculateFinancialStatus(myDossier.getId());

        SituationFinanciere sf = dossierService.getSituationFinanciere(myDossier.getId());
        System.out.println("💰 Total Acts Cost: " + sf.getTotaleDesActes());
        System.out.println("💰 Total Credit (Remaining): " + sf.getCredit());

        if (sf.getTotaleDesActes() == 500.0) {
            System.out.println("✅ Financial Logic Correct!");
        } else {
            System.out.println("❌ Financial Logic Error.");
        }
    }
}