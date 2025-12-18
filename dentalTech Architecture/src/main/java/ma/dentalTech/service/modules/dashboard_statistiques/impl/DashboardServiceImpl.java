package ma.dentalTech.service.modules.dashboard_statistiques.impl;

import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.repository.modules.cabinet.api.ChargesRepository;
import ma.dentalTech.repository.modules.cabinet.api.RevenuesRepository;
import ma.dentalTech.service.modules.dashboard_statistiques.api.DashboardService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardServiceImpl implements DashboardService {

    private final RevenuesRepository revenuesRepository;
    private final ChargesRepository chargesRepository;

    // Constructor Injection: Needs access to financial data
    public DashboardServiceImpl(RevenuesRepository revenuesRepo, ChargesRepository chargesRepo) {
        this.revenuesRepository = revenuesRepo;
        this.chargesRepository = chargesRepo;
    }

    @Override
    public double calculateTurnover(Long cabinetId) {
        List<Revenues> revenues = revenuesRepository.findByCabinet(cabinetId);
        return revenues.stream().mapToDouble(Revenues::getMontant).sum();
    }

    @Override
    public double calculateTotalExpenses(Long cabinetId) {
        List<Charges> charges = chargesRepository.findByCabinet(cabinetId);
        return charges.stream().mapToDouble(Charges::getMontant).sum();
    }

    @Override
    public double calculateNetProfit(Long cabinetId) {
        return calculateTurnover(cabinetId) - calculateTotalExpenses(cabinetId);
    }

    @Override
    public Map<String, Double> getFinancialSummary(Long cabinetId) {
        Map<String, Double> summary = new HashMap<>();
        summary.put("Turnover", calculateTurnover(cabinetId));
        summary.put("Expenses", calculateTotalExpenses(cabinetId));
        summary.put("Profit", calculateNetProfit(cabinetId));
        return summary;
    }
}