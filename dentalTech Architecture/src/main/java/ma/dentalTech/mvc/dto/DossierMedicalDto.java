package ma.dentalTech.mvc.dto;

import java.time.LocalDate;

public record DossierMedicalDto(
        Long id,
        Long patientId,
        String numero,
        LocalDate dateCreation
) {}
