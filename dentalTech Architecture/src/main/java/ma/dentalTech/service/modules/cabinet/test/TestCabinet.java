package ma.dentalTech.service.modules.cabinet.test;

import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.repository.modules.cabinet.impl.CabinetMedicalRepositoryImpl;
import ma.dentalTech.repository.modules.cabinet.impl.ChargesRepositoryImpl;
import ma.dentalTech.repository.modules.cabinet.impl.RevenuesRepositoryImpl;
import ma.dentalTech.repository.modules.cabinet.impl.StatistiquesRepositoryImpl;
import ma.dentalTech.service.modules.cabinet.api.CabinetMedicalService;
import ma.dentalTech.service.modules.cabinet.api.ParametrageService;
import ma.dentalTech.service.modules.cabinet.impl.CabinetMedicalServiceImpl;
import ma.dentalTech.service.modules.cabinet.impl.ParametrageServiceImpl;

import java.time.LocalDateTime;

public class TestCabinet {

    public static void main(String[] args) {
        System.out.println("--- STARTING CABINET MODULE TEST ---");

        // 1. Setup Repositories (The Database Layer)
        CabinetMedicalRepositoryImpl cabinetRepo = new CabinetMedicalRepositoryImpl();
        ChargesRepositoryImpl chargesRepo = new ChargesRepositoryImpl();
        RevenuesRepositoryImpl revenuesRepo = new RevenuesRepositoryImpl();
        StatistiquesRepositoryImpl statsRepo = new StatistiquesRepositoryImpl();

        // 2. Setup Services (Injecting Repositories)
        ParametrageService parametrageService = new ParametrageServiceImpl(cabinetRepo);
        CabinetMedicalService operationalService = new CabinetMedicalServiceImpl(chargesRepo, revenuesRepo, statsRepo);

        // 3. Test: Create a Cabinet (Using ParametrageService)
        System.out.println("\n--- TEST 1: Creating Cabinet ---");
        CabinetMedical myCabinet = new CabinetMedical();
        myCabinet.setNom("Dental Care Center");
        myCabinet.setEmail("info@dentalcare.com");
        myCabinet.setAdresse("101 Medical Plaza");

        try {
            parametrageService.createCabinet(myCabinet);
            System.out.println("✅ Success: Cabinet created with ID: " + myCabinet.getId());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        // 4. Test: Add Revenue (Using CabinetMedicalService)
        System.out.println("\n--- TEST 2: Adding Revenue ---");
        Revenues rev = new Revenues();
        rev.setCabinetId(myCabinet.getId());
        rev.setMontant(2000.0);
        rev.setLibelle("Teeth Whitening");
        rev.setDateRevenue(LocalDateTime.now());

        operationalService.addRevenue(rev);
        System.out.println("✅ Success: Revenue added: " + rev.getMontant());

        // 5. Test: Add Charge (Using CabinetMedicalService)
        System.out.println("\n--- TEST 3: Adding Charge ---");
        Charges charge = new Charges();
        charge.setCabinetId(myCabinet.getId());
        charge.setMontant(500.0);
        charge.setLibelle("Supplies Purchase");
        charge.setDateCharge(LocalDateTime.now());

        operationalService.addCharge(charge);
        System.out.println("✅ Success: Charge added: " + charge.getMontant());

        // 6. Test: Check Balance
        System.out.println("\n--- TEST 4: Checking Balance ---");
        double balance = operationalService.calculateBalance(myCabinet.getId());
        System.out.println("💰 Final Balance: " + balance);

        if(balance == 1500.0) {
            System.out.println("✅ Calculation Logic Correct!");
        } else {
            System.out.println("❌ Calculation Logic Incorrect.");
        }
    }
}