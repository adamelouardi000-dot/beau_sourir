package ma.dentalTech.service.modules.dashboard_statistiques.api;

import java.util.Map;

public interface DashboardService {

    /**
     * Calculates the total turnover (Total Revenues) for a specific cabinet.
     */
    double calculateTurnover(Long cabinetId);

    /**
     * Calculates the total expenses (Total Charges) for a specific cabinet.
     */
    double calculateTotalExpenses(Long cabinetId);

    /**
     * Calculates the Net Profit (Turnover - Expenses).
     */
    double calculateNetProfit(Long cabinetId);

    /**
     * Returns a summary map with key metrics (Turnover, Expenses, Profit).
     */
    Map<String, Double> getFinancialSummary(Long cabinetId);
}