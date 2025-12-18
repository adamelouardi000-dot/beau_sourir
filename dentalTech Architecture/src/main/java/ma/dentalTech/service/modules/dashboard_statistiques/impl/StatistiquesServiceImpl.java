package ma.dentalTech.service.modules.dashboard_statistiques.impl;

import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.CategorieStatistique;
import ma.dentalTech.repository.modules.cabinet.api.StatistiquesRepository;
import ma.dentalTech.service.modules.dashboard_statistiques.api.StatistiquesService;

import java.time.LocalDate;
import java.util.List;

public class StatistiquesServiceImpl implements StatistiquesService {

    private final StatistiquesRepository statistiquesRepository;

    public StatistiquesServiceImpl(StatistiquesRepository statistiquesRepository) {
        this.statistiquesRepository = statistiquesRepository;
    }

    @Override
    public Statistiques saveStatistic(Statistiques stat) {
        if (stat == null) throw new IllegalArgumentException("Statistic cannot be null");
        statistiquesRepository.create(stat);
        return stat;
    }

    @Override
    public List<Statistiques> getAllStatistics() {
        return statistiquesRepository.findAll();
    }

    @Override
    public List<Statistiques> getStatisticsByCabinet(Long cabinetId) {
        return statistiquesRepository.findByCabinet(cabinetId);
    }

    @Override
    public List<Statistiques> getByCategory(CategorieStatistique category) {
        return statistiquesRepository.findByCategorie(category);
    }

    @Override
    public List<Statistiques> getByDate(LocalDate date) {
        return statistiquesRepository.findByDate(date);
    }
}