package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrdonnanceRepo extends CrudRepository<Ordonnance, Long> {

    List<Ordonnance> findByConsultation(Long consultationId);

    List<Ordonnance> findByDate(LocalDate date);
    List<Ordonnance> findByDateBetween(LocalDate start, LocalDate end);

    List<Ordonnance> findByMedecin(Long medecinId);

    List<Ordonnance> findPage(int limit, int offset);
}
