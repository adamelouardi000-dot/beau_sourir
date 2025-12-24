package ma.dentalTech.mvc.dto;

import java.time.LocalDate;

public record ConsultationDto(
        Long id,
        LocalDate date,
        String motif,
        String observation
) {}

