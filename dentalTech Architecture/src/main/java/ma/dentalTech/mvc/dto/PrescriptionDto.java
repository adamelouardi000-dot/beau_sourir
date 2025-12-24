package ma.dentalTech.mvc.dto;

public record PrescriptionDto(
        Long id,
        String medicament,
        String posologie,
        int duree
) {}

