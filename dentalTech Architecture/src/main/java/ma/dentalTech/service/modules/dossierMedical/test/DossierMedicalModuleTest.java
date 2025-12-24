package ma.dentalTech.service.modules.dossierMedical.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.dto.*;
import ma.dentalTech.service.modules.dossierMedical.api.ConsultationService;
import ma.dentalTech.service.modules.dossierMedical.api.DossierMedicalService;
import ma.dentalTech.service.modules.dossierMedical.api.OrdonnanceService;
import ma.dentalTech.service.modules.dossierMedical.api.PrescriptionService;

import java.util.List;

public class DossierMedicalModuleTest {

    public static void main(String[] args) {

        System.out.println("========== TEST MODULE DOSSIER MEDICAL ==========");

        // 1) Charger services via ApplicationContext
        DossierMedicalService dossierService = ApplicationContext.getBean(DossierMedicalService.class);
        ConsultationService consultationService = ApplicationContext.getBean(ConsultationService.class);
        OrdonnanceService ordonnanceService = ApplicationContext.getBean(OrdonnanceService.class);
        PrescriptionService prescriptionService = ApplicationContext.getBean(PrescriptionService.class);

        if (dossierService == null || consultationService == null || ordonnanceService == null || prescriptionService == null) {
            System.err.println("❌ Un ou plusieurs services DM introuvables dans ApplicationContext.");
            System.err.println("➡️ Vérifie beans.properties: dossierMedicalService, consultationService, ordonnanceService, prescriptionService");
            return;
        }

        System.out.println("✅ DossierMedicalService: " + dossierService.getClass().getSimpleName());
        System.out.println("✅ ConsultationService  : " + consultationService.getClass().getSimpleName());
        System.out.println("✅ OrdonnanceService    : " + ordonnanceService.getClass().getSimpleName());
        System.out.println("✅ PrescriptionService  : " + prescriptionService.getClass().getSimpleName());

        // 2) Choisir un patientId existant dans ta BD (modifie si besoin)
        Long patientId = 1L;

        // 3) CREATE DOSSIER MEDICAL
        DossierMedicalDto dm;
        try {
            dm = dossierService.create(new DossierMedicalCreateRequest(patientId));
            System.out.println("✅ DOSSIER CREATED -> id=" + dm.id() + " patientId=" + dm.patientId());
        } catch (Exception e) {
            System.out.println("⚠️ DOSSIER CREATE -> " + e.getMessage());
            // Si déjà existant, on le récupère
            try {
                dm = dossierService.getByPatientId(patientId);
                System.out.println("✅ DOSSIER FOUND -> id=" + dm.id() + " patientId=" + dm.patientId());
            } catch (Exception ex) {
                System.err.println("❌ DOSSIER NOT FOUND -> " + ex.getMessage());
                System.err.println("➡️ Mets un patientId qui existe dans ta BD.");
                return;
            }
        }

        // 4) CREATE CONSULTATION
        ConsultationDto consultation;
        try {
            ConsultationCreateRequest cReq = new ConsultationCreateRequest(
                    dm.id(),
                    "Controle",                 // motif (si ton entity n’a pas motif, le service mettra null)
                    "Douleur légère molaire"     // observation
            );

            consultation = consultationService.create(cReq);
            System.out.println("✅ CONSULTATION CREATED -> id=" + consultation.id() + " date=" + consultation.date());

        } catch (Exception e) {
            System.err.println("❌ CONSULTATION CREATE FAILED -> " + e.getMessage());
            return;
        }

        // 5) CREATE ORDONNANCE
        OrdonnanceDto ordonnance;
        try {
            OrdonnanceCreateRequest oReq = new OrdonnanceCreateRequest(consultation.id());
            ordonnance = ordonnanceService.create(oReq);
            System.out.println("✅ ORDONNANCE CREATED -> id=" + ordonnance.id() + " date=" + ordonnance.date());

        } catch (Exception e) {
            System.err.println("❌ ORDONNANCE CREATE FAILED -> " + e.getMessage());
            return;
        }

        // 6) CREATE PRESCRIPTION
        PrescriptionDto prescription;
        try {
            PrescriptionCreateRequest pReq = new PrescriptionCreateRequest(
                    ordonnance.id(),
                    "AMOXICILLINE",
                    "2 fois / jour",
                    7
            );

            prescription = prescriptionService.create(pReq);
            System.out.println("✅ PRESCRIPTION CREATED -> id=" + prescription.id()
                    + " med=" + prescription.medicament()
                    + " duree=" + prescription.duree());

        } catch (Exception e) {
            System.err.println("❌ PRESCRIPTION CREATE FAILED -> " + e.getMessage());
            return;
        }

        // 7) READ LISTS
        try {
            List<ConsultationDto> consultations = consultationService.getByDossierMedical(dm.id());
            System.out.println("✅ CONSULTATIONS COUNT -> " + consultations.size());

            List<PrescriptionDto> prescriptions = prescriptionService.getByOrdonnance(ordonnance.id());
            System.out.println("✅ PRESCRIPTIONS COUNT -> " + prescriptions.size());

        } catch (Exception e) {
            System.err.println("❌ READ LISTS FAILED -> " + e.getMessage());
        }

        System.out.println("========== FIN TEST DOSSIER MEDICAL ==========");
    }
}
