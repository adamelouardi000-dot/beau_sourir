package ma.dentalTech.mvc.dto;

import ma.dentalTech.entities.enums.StatutRDV;

import java.time.LocalDate;
import java.time.LocalTime;

public record RdvUpdateRequest(
        Long id,
        Long agendaId,
        Long patientId,
        Long medecinId,
        Long dossierId,      // nullable
        LocalDate date,
        LocalTime heure,
        String motif,
        StatutRDV statut,
        String noteMedecin   // nullable
) {}
