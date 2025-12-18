package ma.dentalTech.service.modules.caisse.api;

import java.time.LocalDate;

public interface CaisseService {
    /**
     * Calculates the current balance (Total Revenues - Total Charges) for a cabinet.
     */
    double getSoldeCaisse(Long cabinetId);

    /**
     * Calculates the balance for a specific day.
     */
    double getSoldeJournalier(Long cabinetId, LocalDate date);
}