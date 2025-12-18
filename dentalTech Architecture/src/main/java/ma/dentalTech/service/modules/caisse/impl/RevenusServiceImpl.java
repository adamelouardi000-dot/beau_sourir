package ma.dentalTech.service.modules.caisse.impl;

import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.repository.modules.cabinet.api.RevenuesRepository;
import ma.dentalTech.service.modules.caisse.api.RevenusService;

import java.util.List;

public class RevenusServiceImpl implements RevenusService {

    private final RevenuesRepository revenuesRepository;

    public RevenusServiceImpl(RevenuesRepository revenuesRepository) {
        this.revenuesRepository = revenuesRepository;
    }

    @Override
    public void addRecette(Revenues revenue) {
        revenuesRepository.create(revenue);
    }

    @Override
    public List<Revenues> getRecettesByCabinet(Long cabinetId) {
        return revenuesRepository.findByCabinet(cabinetId);
    }

    @Override
    public double getTotalRecettes(Long cabinetId) {
        return getRecettesByCabinet(cabinetId).stream()
                .mapToDouble(Revenues::getMontant)
                .sum();
    }
}