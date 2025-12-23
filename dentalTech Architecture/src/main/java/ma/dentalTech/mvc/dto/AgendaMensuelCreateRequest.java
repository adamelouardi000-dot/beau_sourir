package ma.dentalTech.mvc.dto;

import ma.dentalTech.entities.enums.Mois;

public record AgendaMensuelCreateRequest(
        Long medecinId,
        Mois mois,
        Integer annee
) {}
