package ma.dentalTech.service.modules.caisse.impl;

import ma.dentalTech.service.modules.caisse.api.CaisseService;
import ma.dentalTech.service.modules.caisse.api.ChargesService;
import ma.dentalTech.service.modules.caisse.api.RevenusService;

import java.time.LocalDate;

public class CaisseServiceImpl implements CaisseService {

    private final RevenusService revenusService;
    private final ChargesService chargesService;

    public CaisseServiceImpl(RevenusService revenusService, ChargesService chargesService) {
        this.revenusService = revenusService;
        this.chargesService = chargesService;
    }

    @Override
    public double getSoldeCaisse(Long cabinetId) {
        return revenusService.getTotalRecettes(cabinetId) - chargesService.getTotalDepenses(cabinetId);
    }

    @Override
    public double getSoldeJournalier(Long cabinetId, LocalDate date) {
        // Logic to filter by date would go here (requires Repo support for finding by date)
        // For now, we return global as a placeholder
        return getSoldeCaisse(cabinetId);
    }
}