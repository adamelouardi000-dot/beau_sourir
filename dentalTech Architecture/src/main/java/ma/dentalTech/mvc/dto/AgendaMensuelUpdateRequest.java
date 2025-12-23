package ma.dentalTech.mvc.dto;

import ma.dentalTech.entities.enums.Mois;

public record AgendaMensuelUpdateRequest(
        Long id,
        Long medecinId,
        Mois mois,
        Integer annee
) {}
