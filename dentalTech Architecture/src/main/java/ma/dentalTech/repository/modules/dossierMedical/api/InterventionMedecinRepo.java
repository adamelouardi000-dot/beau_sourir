package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface InterventionMedecinRepo extends CrudRepository<InterventionMedecin, Long> {

    List<InterventionMedecin> findByConsultation(Long consultationId);
    List<InterventionMedecin> findByActe(Long acteId);

    List<InterventionMedecin> findByNumDent(Integer numDent);
    List<InterventionMedecin> findByPrixPatientBetween(Double min, Double max);

    List<InterventionMedecin> findPage(int limit, int offset);
}
