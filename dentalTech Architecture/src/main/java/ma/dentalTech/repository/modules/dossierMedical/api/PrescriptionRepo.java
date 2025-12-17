package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface PrescriptionRepo extends CrudRepository<Prescription, Long> {

    List<Prescription> findByOrdonnance(Long ordonnanceId);

    List<Prescription> findByMedicament(Long medicamentId);

    List<Prescription> findByOrdonnanceAndMedicament(Long ordonnanceId, Long medicamentId);

    List<Prescription> findPage(int limit, int offset);
}
