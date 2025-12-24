package ma.dentalTech.service.modules.dossierMedical.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.mvc.dto.*;
import ma.dentalTech.service.modules.dossierMedical.api.ConsultationService;
import ma.dentalTech.service.modules.dossierMedical.api.DossierMedicalService;
import ma.dentalTech.service.modules.dossierMedical.api.OrdonnanceService;
import ma.dentalTech.service.modules.dossierMedical.api.PrescriptionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

        // 2) Récupérer un patientId EXISTANT (sinon FK error)
        Long patientId = pickExistingPatientId();
        if (patientId == null) {
            System.err.println("❌ Aucun patient trouvé dans la table Patients.");
            System.err.println("➡️ Ajoute d'abord un patient (seed.sql ou insertion manuelle) puis relance le test.");
            return;
        }
        System.out.println("✅ PatientId utilisé -> " + patientId);

        // 3) CREATE / GET DOSSIER MEDICAL
        DossierMedicalDto dm;
        try {
            dm = dossierService.create(new DossierMedicalCreateRequest(patientId));
            System.out.println("✅ DOSSIER CREATED -> id=" + dm.id() + " patientId=" + dm.patientId());
        } catch (Exception e) {
            System.out.println("⚠️ DOSSIER CREATE -> " + e.getMessage());
            try {
                dm = dossierService.getByPatientId(patientId);
                System.out.println("✅ DOSSIER FOUND -> id=" + dm.id() + " patientId=" + dm.patientId());
            } catch (Exception ex) {
                System.err.println("❌ DOSSIER NOT FOUND -> " + ex.getMessage());
                return;
            }
        }

        // 4) CREATE CONSULTATION
        ConsultationDto consultation;
        try {
            ConsultationCreateRequest cReq = new ConsultationCreateRequest(
                    dm.id(),
                    "Controle",
                    "Douleur légère molaire"
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
                    + " medicament=" + prescription.medicament()
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

    private static Long pickExistingPatientId() {
        String sql = "SELECT id FROM Patients ORDER BY id ASC LIMIT 1";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getLong(1);
            return null;

        } catch (Exception e) {
            System.err.println("❌ Impossible de lire Patients : " + e.getMessage());
            return null;
        }
    }
}
