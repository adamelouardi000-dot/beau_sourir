package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.entities.dossierMedical.SituationFinanciere;

public interface SituationFinanciereService {
    SituationFinanciere getSituationByDossier(Long dossierId);
    void updatePaiement(Long dossierId, double montantPaye);
}