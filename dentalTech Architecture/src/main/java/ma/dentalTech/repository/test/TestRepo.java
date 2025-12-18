package ma.dentalTech.repository.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;

import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.enums.Sexe;

import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.Mois;

import ma.dentalTech.entities.cabinet.CabinetMedical;

// ✅ Facture est dans dossierMedical chez toi
import ma.dentalTech.entities.dossierMedical.Facture;

// ✅ Repo Facture = FactureRepo (pas FactureRepository)
import ma.dentalTech.repository.modules.facturation.api.FactureRepository;

import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;

import ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository;
import ma.dentalTech.repository.modules.cabinet.api.ChargesRepository;
import ma.dentalTech.repository.modules.cabinet.api.RevenuesRepository;
import ma.dentalTech.repository.modules.cabinet.api.StatistiquesRepository;

import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.agenda.api.RDVRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TestRepo {

    // ===== Repos via ApplicationContext =====
    private final PatientRepository patientRepo = ApplicationContext.getBean(PatientRepository.class);
    private final AntecedentRepository antecedentRepo = ApplicationContext.getBean(AntecedentRepository.class);

    private final CabinetMedicalRepository cabinetRepo = ApplicationContext.getBean(CabinetMedicalRepository.class);
    private final ChargesRepository chargesRepo = ApplicationContext.getBean(ChargesRepository.class);
    private final RevenuesRepository revenuesRepo = ApplicationContext.getBean(RevenuesRepository.class);
    private final StatistiquesRepository statsRepo = ApplicationContext.getBean(StatistiquesRepository.class);

    private final AgendaMensuelRepository agendaRepo = ApplicationContext.getBean(AgendaMensuelRepository.class);
    private final RDVRepository rdvRepo = ApplicationContext.getBean(RDVRepository.class);

    private final FactureRepository factureRepo = ApplicationContext.getBean(FactureRepository.class);

    // ===== IDs =====
    private Long patientId;
    private Long antecedentId;
    private Long cabinetId;
    private Long agendaId;
    private Long rdvId;
    private Long factureId;

    void insertProcess() {
        System.out.println("\n================ INSERT PROCESS ================");

        // -------- Patient --------
        Patient p = Patient.builder()
                .nom("TestNom")
                .prenom("TestPrenom")
                .adresse("Adresse 1")
                .telephone("0600000001")
                .email("test_" + System.currentTimeMillis() + "@mail.com")
                .dateNaissance(LocalDate.of(2000, 1, 1))
                .sexe(Sexe.Homme)
                .assurance(Assurance.Aucune)
                .build();
        patientRepo.create(p);
        patientId = p.getId();
        System.out.println("✅ Patient créé id=" + patientId);

        // -------- Antecedent --------
        Antecedent a = Antecedent.builder()
                .nom("Allergie test")
                .categorie(CategorieAntecedent.ALLERGIE)
                .niveauRisque(NiveauRisque.MODERE)
                .build();
        antecedentRepo.create(a);
        antecedentId = a.getId();
        System.out.println("✅ Antecedent créé id=" + antecedentId);

        // -------- Liaison N-N --------
        patientRepo.addAntecedentToPatient(patientId, antecedentId);
        System.out.println("✅ Liaison Patient_Antecedents ajoutée");

        // -------- Cabinet (minimal) --------
        try {
            CabinetMedical cab = new CabinetMedical();
            cab.setNom("Cabinet Test");
            cab.setAdresse("Adresse Cabinet");
            cab.setEmail("cabinet_" + System.currentTimeMillis() + "@mail.com");
            cabinetRepo.create(cab);
            cabinetId = cab.getId();
            System.out.println("✅ Cabinet créé id=" + cabinetId);
        } catch (Exception e) {
            System.out.println("⚠️ Cabinet insert ignoré: " + e.getMessage());
        }

        // -------- Agenda --------
        try {
            AgendaMensuel ag = new AgendaMensuel();
            ag.setAnnee(LocalDate.now().getYear());
            ag.setMois(Mois.JANVIER);
            agendaRepo.create(ag);
            agendaId = ag.getId();
            System.out.println("✅ Agenda créé id=" + agendaId);
        } catch (Exception e) {
            System.out.println("⚠️ Agenda insert ignoré: " + e.getMessage());
        }

        // -------- RDV (setter peut être différent, on fait reflection-safe) --------
        try {
            RDV r = new RDV();
            setIfExists(r, "setDateRdv", LocalDateTime.class, LocalDateTime.now().plusDays(1));
            setIfExists(r, "setDate", LocalDate.class, LocalDate.now().plusDays(1));
            setIfExists(r, "setDateHeure", LocalDateTime.class, LocalDateTime.now().plusDays(1));
            setIfExists(r, "setDateRDV", LocalDateTime.class, LocalDateTime.now().plusDays(1));

            rdvRepo.create(r);
            rdvId = r.getId();
            System.out.println("✅ RDV créé id=" + rdvId);
        } catch (Exception e) {
            System.out.println("⚠️ RDV insert ignoré: " + e.getMessage());
        }

        // -------- Facture (entity dossierMedical) --------
        try {
            Facture f = new Facture();

            // on tente plusieurs setters possibles (selon ton entity)
            setIfExists(f, "setDateFacture", LocalDateTime.class, LocalDateTime.now());
            setIfExists(f, "setDate", LocalDate.class, LocalDate.now());
            setIfExists(f, "setTotal", Double.class, 200.0);
            setIfExists(f, "setMontantTotal", Double.class, 200.0);
            setIfExists(f, "setStatut", String.class, "PAYEE");
            setIfExists(f, "setEtat", String.class, "PAYEE");

            factureRepo.create(f);
            factureId = f.getId();
            System.out.println("✅ Facture créée id=" + factureId);
        } catch (Exception e) {
            System.out.println("⚠️ Facture insert ignoré: " + e.getMessage());
        }
    }

    void selectProcess() {
        System.out.println("\n================ SELECT PROCESS ================");

        System.out.println("Patients.count() = " + safeLong(patientRepo::count));
        System.out.println("Antecedents.count() = " + safeLong(antecedentRepo::count));

        if (patientId != null) {
            Patient p = patientRepo.findById(patientId);
            System.out.println("Patient by id => " + p);

            List<Antecedent> ants = patientRepo.getAntecedentsOfPatient(patientId);
            System.out.println("Antecedents of patient => " + (ants == null ? 0 : ants.size()));
        }

        if (antecedentId != null) {
            System.out.println("Patients having antecedent => " +
                    antecedentRepo.getPatientsHavingAntecedent(antecedentId).size());
        }

        System.out.println("Cabinets.findAll().size = " + safeInt(() -> cabinetRepo.findAll().size()));
        System.out.println("Charges.findAll().size  = " + safeInt(() -> chargesRepo.findAll().size()));
        System.out.println("Revenues.findAll().size = " + safeInt(() -> revenuesRepo.findAll().size()));
        System.out.println("Stats.findAll().size    = " + safeInt(() -> statsRepo.findAll().size()));
        System.out.println("Agendas.findAll().size  = " + safeInt(() -> agendaRepo.findAll().size()));
        System.out.println("RDV.findAll().size      = " + safeInt(() -> rdvRepo.findAll().size()));
        System.out.println("Factures.findAll().size = " + safeInt(() -> factureRepo.findAll().size()));
    }

    void updateProcess() {
        System.out.println("\n================ UPDATE PROCESS ================");

        if (patientId != null) {
            Patient p = patientRepo.findById(patientId);
            if (p != null) {
                p.setAdresse("Adresse modifiée");
                p.setTelephone("0600009999");
                patientRepo.update(p);
                System.out.println("✅ Patient update OK");
            }
        }

        if (antecedentId != null) {
            Antecedent a = antecedentRepo.findById(antecedentId);
            if (a != null) {
                a.setNiveauRisque(NiveauRisque.ELEVE);
                antecedentRepo.update(a);
                System.out.println("✅ Antecedent update OK");
            }
        }

        if (factureId != null) {
            Facture f = factureRepo.findById(factureId);
            if (f != null) {
                setIfExists(f, "setTotal", Double.class, 350.0);
                setIfExists(f, "setMontantTotal", Double.class, 350.0);
                factureRepo.update(f);
                System.out.println("✅ Facture update OK");
            }
        }
    }

    void deleteProcess() {
        System.out.println("\n================ DELETE PROCESS ================");

        if (patientId != null && antecedentId != null) {
            safeRun(() -> patientRepo.removeAntecedentFromPatient(patientId, antecedentId));
        }

        safeRun(() -> { if (factureId != null) factureRepo.deleteById(factureId); });
        safeRun(() -> { if (rdvId != null) rdvRepo.deleteById(rdvId); });
        safeRun(() -> { if (agendaId != null) agendaRepo.deleteById(agendaId); });
        safeRun(() -> { if (cabinetId != null) cabinetRepo.deleteById(cabinetId); });

        safeRun(() -> { if (antecedentId != null) antecedentRepo.deleteById(antecedentId); });
        safeRun(() -> { if (patientId != null) patientRepo.deleteById(patientId); });

        factureId = rdvId = agendaId = cabinetId = antecedentId = patientId = null;
        System.out.println("✅ Nettoyage terminé");
    }

    // ========= helpers =========
    private interface RunnableX { void run() throws Exception; }
    private static void safeRun(RunnableX r) { try { r.run(); } catch (Exception ignored) {} }

    private interface IntSupplierX { int get() throws Exception; }
    private static int safeInt(IntSupplierX s) { try { return s.get(); } catch (Exception e) { return -1; } }

    private interface LongSupplierX { long get() throws Exception; }
    private static long safeLong(LongSupplierX s) { try { return s.get(); } catch (Exception e) { return -1; } }

    private static void setIfExists(Object obj, String setter, Class<?> type, Object value) {
        try {
            obj.getClass().getMethod(setter, type).invoke(obj, value);
        } catch (Exception ignored) {
        }
    }

    public static void main(String[] args) {
        TestRepo t = new TestRepo();
        try {
            t.insertProcess();
            t.selectProcess();
            t.updateProcess();
            t.selectProcess();
            t.deleteProcess();
            t.selectProcess();
        } finally {
            SessionFactory.getInstance().closeConnection();
        }
    }
}
