package ma.dentalTech.mvc.dto;

import ma.dentalTech.entities.enums.Mois;

public record AgendaMensuelSearchRequest(
        Long medecinId,
        Mois mois,
        Integer annee
) {}
