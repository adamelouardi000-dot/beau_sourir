package ma.dentalTech.service.modules.cabinet.test;

import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.service.modules.cabinet.api.CabinetMedicalService;
import ma.dentalTech.service.modules.cabinet.api.ParametrageService;
import ma.dentalTech.configuration.ApplicationContext;

import java.time.LocalDateTime;

public class TestCabinet {

    public static void main(String[] args) {
        System.out.println("--- STARTING CABINET MODULE TEST ---");

        // ✅ Services via ApplicationContext (comme le prof)
        ParametrageService parametrageService = ApplicationContext.getBean(ParametrageService.class);
        CabinetMedicalService operationalService = ApplicationContext.getBean(CabinetMedicalService.class);

        // 1) Create Cabinet
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
            return;
        }

        // 2) Add Revenue
        System.out.println("\n--- TEST 2: Adding Revenue ---");
        Revenues rev = new Revenues();
        rev.setMontant(2000.0);
        rev.setTitre("Teeth Whitening");
        rev.setDate(LocalDateTime.now());
        // si ton entity a cabinetId:
        try { rev.getClass().getMethod("setCabinetId", Long.class).invoke(rev, myCabinet.getId()); } catch (Exception ignored) {}

        operationalService.addRevenue(rev);
        System.out.println("✅ Success: Revenue added: " + rev.getMontant());

        // 3) Add Charge
        System.out.println("\n--- TEST 3: Adding Charge ---");
        Charges charge = new Charges();
        charge.setMontant(500.0);
        charge.setTitre("Supplies Purchase");
        charge.setDate(LocalDateTime.now());
        // si ton entity a cabinetId:
        try { charge.getClass().getMethod("setCabinetId", Long.class).invoke(charge, myCabinet.getId()); } catch (Exception ignored) {}

        operationalService.addCharge(charge);
        System.out.println("✅ Success: Charge added: " + charge.getMontant());

        // 4) Balance
        System.out.println("\n--- TEST 4: Checking Balance ---");
        double balance = operationalService.calculateBalance(myCabinet.getId());
        System.out.println("💰 Final Balance: " + balance);
    }
}
