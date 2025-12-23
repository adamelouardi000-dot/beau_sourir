package ma.dentalTech.service.modules.agendas.utils;

import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.service.modules.agendas.exception.ValidationException;

public final class RDVValidator {

    private RDVValidator(){}

    public static void validateCreate(RDV r) {
        if (r == null) throw new ValidationException("RDV est null.");
        if (r.getDate() == null) throw new ValidationException("date obligatoire.");
        if (r.getHeure() == null) throw new ValidationException("heure obligatoire.");
        if (r.getStatut() == null) throw new ValidationException("statut obligatoire.");
        if (r.getPatient() == null || r.getPatient().getId() == null)
            throw new ValidationException("patient obligatoire (patient.id).");
        if (r.getMedecin() == null || r.getMedecin().getId() == null)
            throw new ValidationException("medecin obligatoire (medecin.id).");
        if (r.getAgendaMensuelId() == null)
            throw new ValidationException("agendaMensuelId obligatoire.");
    }

    public static void validateId(Long id) {
        if (id == null || id <= 0) throw new ValidationException("id invalide.");
    }
}
