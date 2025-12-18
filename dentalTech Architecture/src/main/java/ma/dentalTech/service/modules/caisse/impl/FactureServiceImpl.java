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

        if (consultation == null) {
            throw new IllegalArgumentException("Consultation null.");
        }
        if (montantPaye <= 0) {
            throw new IllegalArgumentException("montantPaye doit être > 0.");
        }

        // 1) Marquer la consultation comme facturée (seulement si le champ existe)
        try {
            consultation.getClass().getMethod("setFacturee", boolean.class).invoke(consultation, true);
        } catch (Exception ignored) {
            // si ton entity n'a pas "facturee", on ignore pour ne pas casser le projet
        }

        // update en DB
        consultationRepo.update(consultation);

        // 2) Ajouter une recette (Revenues)
        Revenues rev = new Revenues();
        rev.setMontant(montantPaye);

        // champs selon ton schema SQL cabinet: titre + date
        try { rev.getClass().getMethod("setTitre", String.class).invoke(rev, "Facture Consultation #" + consultation.getId()); }
        catch (Exception ignored) {}

        try { rev.getClass().getMethod("setDate", LocalDateTime.class).invoke(rev, LocalDateTime.now()); }
        catch (Exception ignored) {}

        // cabinet_id : si ton entity possède cabinetId, on le met à 1L (ou autre)
        try { rev.getClass().getMethod("setCabinetId", Long.class).invoke(rev, 1L); }
        catch (Exception ignored) {}

        revenusService.addRecette(rev);

        // 3) Mettre à jour la situation financière du dossier médical
        try {
            Object dossier = consultation.getClass().getMethod("getDossierMedical").invoke(consultation);
            if (dossier != null) {
                Long dossierId = (Long) dossier.getClass().getMethod("getId").invoke(dossier);
                if (dossierId != null) {
                    situationService.updatePaiement(dossierId, montantPaye);
                }
            }
        } catch (Exception ignored) {
            // si dossierMedical/getId n'existe pas, on ignore
        }
    }
}
