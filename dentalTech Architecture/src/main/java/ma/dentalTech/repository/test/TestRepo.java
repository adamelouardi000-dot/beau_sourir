package ma.dentalTech.repository.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.dossierMedical.*;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.repository.modules.agenda.api.RDVRepository;
import ma.dentalTech.repository.modules.dossierMedical.api.*;
import ma.dentalTech.repository.modules.facturation.api.FactureRepository;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

public class TestRepo {

    private final PatientRepository patientRepo = ApplicationContext.getBean(PatientRepository.class);
    private final DossierMedicalRepo dmRepo = ApplicationContext.getBean(DossierMedicalRepo.class);
    private final RDVRepository rdvRepo = ApplicationContext.getBean(RDVRepository.class);
    private final ConsultationRepo consultationRepo = ApplicationContext.getBean(ConsultationRepo.class);
    private final ActeRepository acteRepo = ApplicationContext.getBean(ActeRepository.class);
    private final FactureRepository factureRepo = ApplicationContext.getBean(FactureRepository.class);
    private final SituationFinanciereRepo sfRepo = ApplicationContext.getBean(SituationFinanciereRepo.class);
    private final OrdonnanceRepo ordonnanceRepo = ApplicationContext.getBean(OrdonnanceRepo.class);
    private final CertificatRepo certificatRepo = ApplicationContext.getBean(CertificatRepo.class);

    private Long pId, dmId, rdvId, consId;

    void insertProcess() {
        System.out.println("=== START INSERT PROCESS ===");

        // 1. Patient [cite: 28]
        Patient p = Patient.builder()
                .nom("TEST_NOM").prenom("TEST_PRENOM")
                .sexe(Sexe.Homme).assurance(Assurance.CNOPS).build();
        patientRepo.create(p);
        pId = p.getId();

        // 2. Dossier Médical [cite: 12]
        DossierMedical dm = new DossierMedical();
        dm.setPatient(p);
        dmRepo.create(dm);
        dmId = dm.getId();

        // 3. RDV (Correction type LocalDate)
        RDV rdv = new RDV();
        rdv.setDate(LocalDate.now().plusDays(1));
        rdvRepo.create(rdv);
        rdvId = rdv.getId();

        // 4. Consultation [cite: 11]
        Consultation cons = new Consultation();
        cons.setDossierMedical(dm);
        consultationRepo.create(cons);
        consId = cons.getId();

        // 5. Actes [cite: 9]
        Acte acte = new Acte();
        acte.setLibelle("Détartrage");
        acteRepo.create(acte);

        // 6. Situation Financière d'abord [cite: 19]
        SituationFinanciere sf = new SituationFinanciere();
        sf.setDossierMedical(dm);
        sfRepo.create(sf);

        // 7. Facture liée à la Situation Financière
        Facture f = new Facture();
        f.setSituationFinanciere(sf);
        factureRepo.create(f);

        // 8. Ordonnance & Certificat [cite: 10, 17]
        Ordonnance ord = new Ordonnance();
        ord.setDossierMedical(dm);
        ordonnanceRepo.create(ord);

        Certificat cert = new Certificat();
        cert.setDossierMedical(dm);
        certificatRepo.create(cert);

        System.out.println("✅ Insertion terminée avec succès.");
    }

    public static void main(String[] args) {
        TestRepo test = new TestRepo();
        try {
            test.insertProcess();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (SessionFactory.getInstance() != null) {
                SessionFactory.getInstance().closeConnection();
            }
        }
    }
}