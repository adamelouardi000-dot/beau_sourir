package ma.dentalTech.mvc.dto;

import java.time.LocalDate;

public record OrdonnanceDto(
        Long id,
        LocalDate date
) {}

