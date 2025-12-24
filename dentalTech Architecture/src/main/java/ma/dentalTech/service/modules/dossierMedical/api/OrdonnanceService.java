package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.mvc.dto.OrdonnanceCreateRequest;
import ma.dentalTech.mvc.dto.OrdonnanceDto;

import java.util.List;

public interface OrdonnanceService {
    OrdonnanceDto create(OrdonnanceCreateRequest request);

    List<OrdonnanceDto> getByConsultation(Long consultationId);
}
