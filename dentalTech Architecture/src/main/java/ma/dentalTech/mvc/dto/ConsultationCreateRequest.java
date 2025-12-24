package ma.dentalTech.mvc.dto;

public record ConsultationCreateRequest(
        Long dossierMedicalId,
        String motif,
        String observation
) {}

