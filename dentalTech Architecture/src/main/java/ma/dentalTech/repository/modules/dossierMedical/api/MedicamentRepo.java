package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.enums.FormeMedicament;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MedicamentRepo extends CrudRepository<Medicament, Long> {

    List<Medicament> findAllOrderByNom();

    Optional<Medicament> findByNom(String nom);

    List<Medicament> findByLaboratoire(String laboratoireLike);

    List<Medicament> findByType(String typeLike);

    List<Medicament> findByForme(FormeMedicament forme);

    List<Medicament> findByRemboursable(boolean remboursable);

    List<Medicament> findByPrixBetween(Double min, Double max);

    List<Medicament> findPage(int limit, int offset);
}
