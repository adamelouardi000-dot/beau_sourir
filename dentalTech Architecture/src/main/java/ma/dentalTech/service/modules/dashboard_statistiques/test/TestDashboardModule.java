package ma.dentalTech.service.modules.dashboard_statistiques.test;

import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.repository.modules.cabinet.impl.ChargesRepositoryImpl;
import ma.dentalTech.repository.modules.cabinet.impl.RevenuesRepositoryImpl;
import ma.dentalTech.repository.modules.cabinet.impl.StatistiquesRepositoryImpl;
import ma.dentalTech.service.modules.dashboard_statistiques.api.DashboardService;
import ma.dentalTech.service.modules.dashboard_statistiques.api.StatistiquesService;
import ma.dentalTech.service.modules.dashboard_statistiques.impl.DashboardServiceImpl;
import ma.dentalTech.service.modules.dashboard_statistiques.impl.StatistiquesServiceImpl;

import java.util.Map;

public class TestDashboardModule {

    public static void main(String[] args) {
        System.out.println("--- 1. Initializing Repositories ---");
        RevenuesRepositoryImpl revenuesRepo = new RevenuesRepositoryImpl();
        ChargesRepositoryImpl chargesRepo = new ChargesRepositoryImpl();
        StatistiquesRepositoryImpl statsRepo = new StatistiquesRepositoryImpl();

        System.out.println("--- 2. Initializing Services ---");
        DashboardService dashboardService = new DashboardServiceImpl(revenuesRepo, chargesRepo);
        StatistiquesService statsService = new StatistiquesServiceImpl(statsRepo);

        // --- FIX: Create a Mock Cabinet Object ---
        // Instead of setting ID directly, we create a Cabinet object
        CabinetMedical myCabinet = new CabinetMedical();
        myCabinet.setId(1L); // We assume the ID is 1 for testing

        System.out.println("--- 3. Pre-loading Dummy Data (Finance) ---");

        // Add Revenue: 10,000
        Revenues r1 = new Revenues();
        r1.setCabinet(myCabinet); // FIX: Use setCabinet() instead of setCabinetId()
        r1.setMontant(10000.0);
        revenuesRepo.create(r1);

        // Add Revenue: 5,000
        Revenues r2 = new Revenues();
        r2.setCabinet(myCabinet); // FIX: Use setCabinet()
        r2.setMontant(5000.0);
        revenuesRepo.create(r2);

        // Add Charge: 3,000
        Charges c1 = new Charges();
        c1.setCabinet(myCabinet); // FIX: Use setCabinet()
        c1.setMontant(3000.0);
        chargesRepo.create(c1);

        System.out.println("--- 4. Testing Dashboard Calculations ---");

        double turnover = dashboardService.calculateTurnover(myCabinet.getId());
        System.out.println("💰 Total Turnover (Expected 15,000): " + turnover);

        double expenses = dashboardService.calculateTotalExpenses(myCabinet.getId());
        System.out.println("💸 Total Expenses (Expected 3,000): " + expenses);

        double profit = dashboardService.calculateNetProfit(myCabinet.getId());
        System.out.println("📈 Net Profit (Expected 12,000): " + profit);

        System.out.println("\n--- 5. Testing Summary Map ---");
        Map<String, Double> summary = dashboardService.getFinancialSummary(myCabinet.getId());
        summary.forEach((key, value) -> System.out.println(key + ": " + value));
    }
}