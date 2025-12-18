package ma.dentalTech.service.modules.cabinet.impl;

import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.repository.modules.cabinet.api.ChargesRepository;
import ma.dentalTech.repository.modules.cabinet.api.RevenuesRepository;
import ma.dentalTech.repository.modules.cabinet.api.StatistiquesRepository;
import ma.dentalTech.service.modules.cabinet.api.CabinetMedicalService;

import java.util.List;

public class CabinetMedicalServiceImpl implements CabinetMedicalService {

    private final ChargesRepository chargesRepository;
    private final RevenuesRepository revenuesRepository;
    private final StatistiquesRepository statistiquesRepository;

    // Constructor Injection
    public CabinetMedicalServiceImpl(ChargesRepository chargesRepo,
                                     RevenuesRepository revenuesRepo,
                                     StatistiquesRepository statsRepo) {
        this.chargesRepository = chargesRepo;
        this.revenuesRepository = revenuesRepo;
        this.statistiquesRepository = statsRepo;
    }

    @Override
    public void addRevenue(Revenues revenue) {
        if (revenue.getMontant() < 0) {
            throw new IllegalArgumentException("Revenue cannot be negative");
        }
        revenuesRepository.create(revenue);
    }

    @Override
    public List<Revenues> getRevenues(Long cabinetId) {
        return revenuesRepository.findByCabinet(cabinetId);
    }

    @Override
    public void addCharge(Charges charge) {
        if (charge.getMontant() < 0) {
            throw new IllegalArgumentException("Charge cannot be negative");
        }
        chargesRepository.create(charge);
    }

    @Override
    public List<Charges> getCharges(Long cabinetId) {
        return chargesRepository.findByCabinet(cabinetId);
    }

    @Override
    public double calculateBalance(Long cabinetId) {
        double totalRevenues = getRevenues(cabinetId).stream().mapToDouble(Revenues::getMontant).sum();
        double totalCharges = getCharges(cabinetId).stream().mapToDouble(Charges::getMontant).sum();
        return totalRevenues - totalCharges;
    }

    @Override
    public List<Statistiques> getStatistics(Long cabinetId) {
        return statistiquesRepository.findByCabinet(cabinetId);
    }
}