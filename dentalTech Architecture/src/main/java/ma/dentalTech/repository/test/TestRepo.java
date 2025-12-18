package ma.dentalTech.repository.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;

import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;
import ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.facturation.api.FactureRepository;

public class TestRepo {

    // ===== Repositories injectés via ApplicationContext =====
    private static final PatientRepository patientRepo =
            ApplicationContext.getBean(PatientRepository.class);

    private static final AntecedentRepository antecedentRepo =
            ApplicationContext.getBean(AntecedentRepository.class);

    private static final CabinetMedicalRepository cabinetRepo =
            ApplicationContext.getBean(CabinetMedicalRepository.class);

    private static final AgendaMensuelRepository agendaRepo =
            ApplicationContext.getBean(AgendaMensuelRepository.class);

    private static final FactureRepository factureRepo =
            ApplicationContext.getBean(FactureRepository.class);

    // =======================================================

    static void insertProcess() {
        System.out.println("=== INSERT ===");

        System.out.println("PatientRepo = " + patientRepo);
        System.out.println("AntecedentRepo = " + antecedentRepo);
        System.out.println("CabinetRepo = " + cabinetRepo);
        System.out.println("AgendaRepo = " + agendaRepo);
        System.out.println("FactureRepo = " + factureRepo);
    }

    static void selectProcess() {
        System.out.println("=== SELECT ===");

        System.out.println("Patients count = " + patientRepo.count());
        System.out.println("Antecedents count = " + antecedentRepo.count());
        System.out.println("Cabinets count = " + cabinetRepo.count());
    }

    static void updateProcess() {
        System.out.println("=== UPDATE ===");
        // ici tu modifies un objet déjà créé
    }

    static void deleteProcess() {
        System.out.println("=== DELETE ===");
        // suppression logique (ordre important)
    }

    public static void main(String[] args) {

        try {
            insertProcess();
            selectProcess();
            updateProcess();
            deleteProcess();
        } finally {
            // TOUJOURS fermer la connexion JDBC
            SessionFactory.getInstance().closeConnection();
        }
    }
}
