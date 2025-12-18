package ma.dentalTech.service.modules.caisse.impl;

import ma.dentalTech.entities.dossierMedical.SituationFinanciere;
import ma.dentalTech.repository.modules.dossierMedical.api.SituationFinanciereRepo;
import ma.dentalTech.service.modules.caisse.api.SituationFinanciereService;

public class SituationFinanciereServiceImpl implements SituationFinanciereService {

    private final SituationFinanciereRepo situationRepo;

    public SituationFinanciereServiceImpl(SituationFinanciereRepo situationRepo) {
        this.situationRepo = situationRepo;
    }

    @Override
    public SituationFinanciere getSituationByDossier(Long dossierId) {
        return situationRepo.findByDossierMedical(dossierId)
                .orElseThrow(() -> new RuntimeException("Situation not found"));
    }

    @Override
    public void updatePaiement(Long dossierId, double montantPaye) {
        SituationFinanciere sf = getSituationByDossier(dossierId);

        double paidSoFar = sf.getTotalePaye() != null ? sf.getTotalePaye() : 0.0;
        double totalCost = sf.getTotaleDesActes() != null ? sf.getTotaleDesActes() : 0.0;

        // Update paid amount
        sf.setTotalePaye(paidSoFar + montantPaye);
        // Recalculate credit (Debt)
        sf.setCredit(totalCost - sf.getTotalePaye());

        situationRepo.update(sf);
    }
}