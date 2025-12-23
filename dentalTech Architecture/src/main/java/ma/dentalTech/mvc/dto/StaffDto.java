package ma.dentalTech.mvc.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StaffDto(
        Long id,
        BigDecimal salaire,
        BigDecimal prime,
        LocalDate dateRecrutement,
        Integer soldeConge
) {}
