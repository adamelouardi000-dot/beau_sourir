package ma.dentalTech.service.modules.caisse.impl;

import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo;
import ma.dentalTech.service.modules.caisse.api.FactureService;
import ma.dentalTech.service.modules.caisse.api.RevenusService;
import ma.dentalTech.service.modules.caisse.api.SituationFinanciereService;

import java.time.LocalDateTime;

public class FactureServiceImpl implements FactureService {

    private final ConsultationRepo consultationRepo;
    private final RevenusService revenusService;
    private final SituationFinanciereService situationService;

    public FactureServiceImpl(ConsultationRepo consultationRepo,
                              RevenusService revenusService,
                              SituationFinanciereService situationService) {
        this.consultationRepo = consultationRepo;
        this.revenusService = revenusService;
        this.situationService = situationService;
    }

    @Override
    public void genererFacture(Consultation consultation, double montantPaye) {
        // 1. Mark consultation as Facturée
        consultation.setFacturee(true);
        consultationRepo.update(consultation);

        // 2. Create Revenue Entry (Money entering the Caisse)
        Revenues rev = new Revenues();
        rev.setCabinetId(1L); // Default or extract from context
        rev.setMontant(montantPaye);
        rev.setLibelle("Facture Consultation #" + consultation.getId());
        rev.setDateRevenue(LocalDateTime.now());
        revenusService.addRecette(rev);

        // 3. Update Patient's Financial Situation (Reduce debt)
        if (consultation.getDossierMedical() != null) {
            situationService.updatePaiement(consultation.getDossierMedical().getId(), montantPaye);
        }
    }
}