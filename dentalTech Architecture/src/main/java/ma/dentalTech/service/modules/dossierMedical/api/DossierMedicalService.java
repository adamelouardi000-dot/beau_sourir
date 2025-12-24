package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.mvc.dto.DossierMedicalCreateRequest;
import ma.dentalTech.mvc.dto.DossierMedicalDto;

public interface DossierMedicalService {

    DossierMedicalDto create(DossierMedicalCreateRequest request);

    DossierMedicalDto getByPatientId(Long patientId);
}
