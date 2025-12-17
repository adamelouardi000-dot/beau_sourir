package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.SituationFinanciere;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SituationFinanciereRepo extends CrudRepository<SituationFinanciere, Long> {

    Optional<SituationFinanciere> findByDossierMedical(Long dossierMedicalId);

    List<SituationFinanciere> findByStatut(StatutSituationFinanciere statut);

    List<SituationFinanciere> findByEnPromo(boolean enPromo);

    List<SituationFinanciere> findByCreditBetween(Double min, Double max);

    List<SituationFinanciere> findPage(int limit, int offset);
}
