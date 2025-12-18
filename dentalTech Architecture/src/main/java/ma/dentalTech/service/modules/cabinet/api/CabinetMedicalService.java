package ma.dentalTech.service.modules.cabinet.api;

import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.entities.cabinet.Statistiques;

import java.util.List;

public interface CabinetMedicalService {

    // --- Financial Operations (Revenues) ---
    void addRevenue(Revenues revenue);
    List<Revenues> getRevenues(Long cabinetId);

    // --- Financial Operations (Charges) ---
    void addCharge(Charges charge);
    List<Charges> getCharges(Long cabinetId);

    // --- Analytics ---
    double calculateBalance(Long cabinetId);
    List<Statistiques> getStatistics(Long cabinetId);
}