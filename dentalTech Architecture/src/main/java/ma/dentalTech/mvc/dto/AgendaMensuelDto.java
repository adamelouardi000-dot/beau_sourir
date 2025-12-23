package ma.dentalTech.mvc.dto;

import ma.dentalTech.entities.enums.Mois;

import java.time.LocalDate;
import java.util.List;

public record AgendaMensuelDto(
        Long id,
        Long medecinId,
        Mois mois,
        Integer annee,
        List<LocalDate> joursNonDisponibles
) {}
