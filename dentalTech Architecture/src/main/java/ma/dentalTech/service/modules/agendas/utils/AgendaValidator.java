package ma.dentalTech.service.modules.agendas.utils;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.service.modules.agendas.exception.ValidationException;

public final class AgendaValidator {

    private AgendaValidator(){}

    public static void validateCreate(AgendaMensuel a) {
        if (a == null) throw new ValidationException("AgendaMensuel est null.");
        if (a.getMedecinId() == null) throw new ValidationException("medecinId obligatoire.");
        if (a.getMois() == null) throw new ValidationException("mois obligatoire.");
        if (a.getAnnee() == null) throw new ValidationException("annee obligatoire.");
        if (a.getAnnee() < 2000) throw new ValidationException("annee invalide.");
    }

    public static void validateId(Long id) {
        if (id == null || id <= 0) throw new ValidationException("id invalide.");
    }
}
