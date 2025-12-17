package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ActeRepository extends CrudRepository<Acte, Long> {

    /**
     * Retourne tous les actes triés par libellé.
     */
    List<Acte> findAllOrderByLibelle();

    /**
     * Recherche d’un acte par son libellé exact.
     */
    Optional<Acte> findByLibelle(String libelle);

    /**
     * Recherche des actes dont le prix est compris entre min et max.
     */
    List<Acte> findByPrixBetween(Double min, Double max);

    /**
     * Recherche des actes dont le libellé contient un mot-clé.
     */
    List<Acte> searchByLibelle(String keyword);

    /**
     * Pagination simple.
     */
    List<Acte> findPage(int limit, int offset);
}
