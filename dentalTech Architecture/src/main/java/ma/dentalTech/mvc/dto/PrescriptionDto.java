package ma.dentalTech.mvc.dto;

public record PrescriptionDto(
        Long id,
        Long ordonnanceId, String medicament,
        String posologie,
        int duree
) {}

