package ma.dentalTech.entities.dossierMedical;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.patient.Patient;

/**
 * Entité représentant le dossier médical d'un patient.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DossierMedical extends BaseEntity {


    private LocalDate dateCreation;
    private Patient patient;

    private List<Consultation> consultations = new ArrayList<>();
    private List<Ordonnance> ordonnances = new ArrayList<>();
    private List<Certificat> certificats = new ArrayList<>();
    private SituationFinanciere situationFinanciere;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DossierMedical)) return false;
        DossierMedical that = (DossierMedical) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            DossierMedical {
                id = %d,
                patient = %d,
                dateCreation = %s,
                consultationsCount = %d
            }
            """.formatted(id, patient.getNom(), dateCreation,
                consultations == null ? 0 : consultations.size());
    }
}
