package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.mvc.dto.PrescriptionCreateRequest;
import ma.dentalTech.mvc.dto.PrescriptionDto;

import java.util.List;

public interface PrescriptionService {
    PrescriptionDto create(PrescriptionCreateRequest request);

    List<PrescriptionDto> getByOrdonnance(Long ordonnanceId);
}
