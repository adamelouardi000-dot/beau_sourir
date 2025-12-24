package ma.dentalTech.service.modules.dossierMedical.dto;

public record CreateConsultationCommand(
        Long dossierMedicalId,
        String motif,
        String observation
) {}

