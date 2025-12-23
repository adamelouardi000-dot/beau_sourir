package ma.dentalTech.mvc.dto;

import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.TypeNotification;

import java.time.LocalDate;
import java.time.LocalTime;

public record NotificationDto(
        Long id,
        TitreNotification titre,
        String message,
        LocalDate date,
        LocalTime time,
        TypeNotification type,
        PrioriteNotification priorite,
        boolean lue,
        Long utilisateurId
) {}
