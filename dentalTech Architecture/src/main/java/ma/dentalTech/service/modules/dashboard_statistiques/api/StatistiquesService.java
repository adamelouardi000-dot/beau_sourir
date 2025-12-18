package ma.dentalTech.service.modules.dashboard_statistiques.api;

import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.CategorieStatistique;

import java.time.LocalDate;
import java.util.List;

public interface StatistiquesService {
    // --- CRUD for Statistics Records ---
    Statistiques saveStatistic(Statistiques stat);
    List<Statistiques> getAllStatistics();
    List<Statistiques> getStatisticsByCabinet(Long cabinetId);

    // --- Filters ---
    List<Statistiques> getByCategory(CategorieStatistique category);
    List<Statistiques> getByDate(LocalDate date);
}