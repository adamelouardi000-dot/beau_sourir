package ma.dentalTech.repository.modules.facturation.api;

import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface FactureRepository extends CrudRepository<Facture, Long> {

    List<Facture> findByPatient(Long patientId);

    List<Facture> findByConsultation(Long consultationId);

    List<Facture> findByDateBetween(LocalDate start, LocalDate end);

    boolean existsById(Long id);

    long count();

    List<Facture> findPage(int limit, int offset);
}
