package ma.dentalTech.service.modules.patient.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;

import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;

import ma.dentalTech.service.modules.patient.api.AntecedentService;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.time.LocalDate;

public class TestPatientService {

    public static void main(String[] args) {

        PatientService patientService = ApplicationContext.getBean(PatientService.class);
        AntecedentService antecedentService = ApplicationContext.getBean(AntecedentService.class);

        Long patientId = null;
        Long antecedentId = null;

        try {
            Patient p = Patient.builder()
                    .nom("ServiceTest")
                    .prenom("Patient")
                    .adresse("Adresse service")
                    .telephone("0600000000")
                    .email("service_" + System.currentTimeMillis() + "@mail.com")
                    .dateNaissance(LocalDate.of(2000, 1, 1))
                    .sexe(Sexe.Homme)
                    .assurance(Assurance.Aucune)
                    .build();

            patientService.create(p);
            patientId = p.getId();
            System.out.println("✅ create patient id=" + patientId);

            Antecedent a = Antecedent.builder()
                    .nom("Antecedent service")
                    .categorie(CategorieAntecedent.ALLERGIE)
                    .niveauRisque(NiveauRisque.FAIBLE)
                    .build();

            antecedentService.create(a);
            antecedentId = a.getId();
            System.out.println("✅ create antecedent id=" + antecedentId);

            patientService.addAntecedentToPatient(patientId, antecedentId);
            System.out.println("✅ link patient-antecedent");

            p.setAdresse("Adresse modifiée service");
            patientService.update(p);
            System.out.println("✅ update patient");

            System.out.println("Antecedents of patient = " + patientService.getAntecedentsOfPatient(patientId).size());

            // cleanup
            patientService.removeAntecedentFromPatient(patientId, antecedentId);
            antecedentService.deleteById(antecedentId);
            antecedentId = null;

            patientService.deleteById(patientId);
            patientId = null;

            System.out.println("✅ delete patient + cleanup");

        } finally {
            if (antecedentId != null) { try { antecedentService.deleteById(antecedentId); } catch (Exception ignored) {} }
            if (patientId != null) { try { patientService.deleteById(patientId); } catch (Exception ignored) {} }
            SessionFactory.getInstance().closeConnection();
        }
    }
}
