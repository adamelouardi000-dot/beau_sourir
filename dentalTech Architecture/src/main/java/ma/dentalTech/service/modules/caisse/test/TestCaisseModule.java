package ma.dentalTech.service.modules.caisse.test;

import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.dossierMedical.SituationFinanciere;

import ma.dentalTech.repository.modules.cabinet.impl.ChargesRepositoryImpl;
import ma.dentalTech.repository.modules.cabinet.impl.RevenuesRepositoryImpl;
import ma.dentalTech.repository.modules.dossierMedical.impl.ConsultationRepoImpl;
import ma.dentalTech.repository.modules.dossierMedical.impl.SituationFinanciereRepoImpl;

import ma.dentalTech.service.modules.caisse.api.*;
import ma.dentalTech.service.modules.caisse.impl.*;

import java.time.LocalDateTime;

public class TestCaisseModule {

    public static void main(String[] args) {
        System.out.println("--- 1. Init Repositories ---");
        ChargesRepositoryImpl chargesRepo = new ChargesRepositoryImpl();
        RevenuesRepositoryImpl revenuesRepo = new RevenuesRepositoryImpl();
        ConsultationRepoImpl consultRepo = new ConsultationRepoImpl();
        SituationFinanciereRepoImpl sitRepo = new SituationFinanciereRepoImpl();

        System.out.println("--- 2. Init Services ---");
        ChargesService chargesService = new ChargesServiceImpl(chargesRepo);
        RevenusService revenusService = new RevenusServiceImpl(revenuesRepo);
        SituationFinanciereService sitService = new SituationFinanciereServiceImpl(sitRepo);

        FactureService factureService = new FactureServiceImpl(consultRepo, revenusService, sitService);
        CaisseService caisseService = new CaisseServiceImpl(revenusService, chargesService);

        System.out.println("--- 3. Testing Flow ---");

        // A. Add Expense (Charges)
        Charges elec = new Charges();

        // ✅ champs cohérents avec ton schema SQL : titre, montant, date
        elec.setMontant(200.0);
        try { elec.getClass().getMethod("setTitre", String.class).invoke(elec, "Electricity"); } catch (Exception ignored) {}
        try { elec.getClass().getMethod("setDate", LocalDateTime.class).invoke(elec, LocalDateTime.now()); } catch (Exception ignored) {}

        // ✅ si cabinetId existe dans ton entity, on l’assigne, sinon on ignore
        try { elec.getClass().getMethod("setCabinetId", Long.class).invoke(elec, 1L); } catch (Exception ignored) {}

        chargesService.addDepense(elec);
        System.out.println("✅ Expense Added: -200.0");

        // B. Simulate Patient Payment (Facture)
        DossierMedical dossier = new DossierMedical();
        dossier.setId(10L); // Mock ID

        SituationFinanciere sf = new SituationFinanciere();
        sf.setDossierMedical(dossier);
        sf.setTotaleDesActes(1000.0); // Patient owes 1000
        sf.setTotalePaye(0.0);
        sitRepo.create(sf);

        Consultation consult = new Consultation();
        consult.setId(50L);
        consult.setDossierMedical(dossier);

        factureService.genererFacture(consult, 500.0);
        System.out.println("✅ Facture Generated: +500.0 (Patient Paid)");

        // C. Check Global Balance
        double solde = caisseService.getSoldeCaisse(1L);
        System.out.println("\n💰 FINAL CAISSE BALANCE: " + solde);

        if (solde == 300.0) { // 500 (Revenue) - 200 (Expense)
            System.out.println("✅ Caisse Logic is Correct!");
        } else {
            System.out.println("❌ Error in calculation.");
        }
    }
}
