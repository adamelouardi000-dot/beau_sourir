package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.entities.dossierMedical.Consultation;

public interface FactureService {
    /**
     * Generates a bill for a consultation and marks it as "Facturée".
     * Automatically creates a Revenue entry.
     */
    void genererFacture(Consultation consultation, double montantPaye);
}