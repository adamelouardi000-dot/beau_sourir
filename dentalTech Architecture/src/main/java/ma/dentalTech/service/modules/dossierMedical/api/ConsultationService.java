package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.mvc.dto.ConsultationCreateRequest;
import ma.dentalTech.mvc.dto.ConsultationDto;

import java.util.List;

public interface ConsultationService {
    ConsultationDto create(ConsultationCreateRequest request);

    List<ConsultationDto> getByDossierMedical(Long dossierMedicalId);
}
