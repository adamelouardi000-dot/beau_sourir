package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface ConsultationRepo extends CrudRepository<Consultation, Long> {

    List<Consultation> findByDossierMedical(Long dossierMedicalId);
    List<Consultation> findByMedecin(Long medecinId);

    List<Consultation> findByDate(LocalDate date);
    List<Consultation> findByDateBetween(LocalDate start, LocalDate end);

    List<Consultation> findByStatut(StatutConsultation statut);

    // IMPORTANT : c’est bien boolean (pas Boolean)
    List<Consultation> findByFacturee(boolean facturee);

    List<Consultation> findPage(int limit, int offset);
}
