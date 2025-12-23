package ma.dentalTech.mvc.dto;

import java.math.BigDecimal;

public record SecretaireDto(
        Long id,
        String numCNSS,
        BigDecimal commission
) {}
