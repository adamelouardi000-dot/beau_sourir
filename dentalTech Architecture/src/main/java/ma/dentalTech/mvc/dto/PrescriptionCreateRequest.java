package ma.dentalTech.mvc.dto;

public record PrescriptionCreateRequest(

        Long ordonnanceId,
        String medicament,
        String posologie,
        int duree

) {}


