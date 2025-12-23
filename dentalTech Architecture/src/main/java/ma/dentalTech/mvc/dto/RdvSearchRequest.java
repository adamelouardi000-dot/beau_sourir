package ma.dentalTech.mvc.dto;

import java.time.LocalDate;

public record RdvSearchRequest(
        Long medecinId,
        LocalDate start,
        LocalDate end
) {}
