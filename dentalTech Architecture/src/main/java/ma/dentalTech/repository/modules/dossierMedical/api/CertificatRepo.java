package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface CertificatRepo extends CrudRepository<Certificat, Long> {

    List<Certificat> findByDossierMedical(Long dossierMedicalId);
    List<Certificat> findByMedecin(Long medecinId);

    List<Certificat> findByDateDebutBetween(LocalDate start, LocalDate end);
    List<Certificat> findByDateFinBetween(LocalDate start, LocalDate end);

    List<Certificat> findByDureeMin(Integer minDuree);

    List<Certificat> findPage(int limit, int offset);
}
