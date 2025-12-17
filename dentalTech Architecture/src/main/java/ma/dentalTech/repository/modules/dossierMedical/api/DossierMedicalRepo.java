package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DossierMedicalRepo extends CrudRepository<DossierMedical, Long> {

    /**
     * Récupérer le dossier médical d’un patient (1–1).
     */
    Optional<DossierMedical> findByPatient(Long patientId);

    /**
     * Dossiers créés après une date donnée.
     */
    List<DossierMedical> findByDateCreationAfter(LocalDate date);

    /**
     * Dossiers fermés / archivés.
     * (si ton entity possède un champ statut ou actif)
     */
    List<DossierMedical> findByActif(boolean actif);

    /**
     * Recherche par numéro ou référence du dossier.
     */
    Optional<DossierMedical> findByNumero(String numero);

    /**
     * Pagination simple.
     */
    List<DossierMedical> findPage(int limit, int offset);
}
