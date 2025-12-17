package ma.dentalTech.entities.dossierMedical;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.entities.users.Medecin;

/**
 * Entité représentant une consultation médicale.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultation  extends BaseEntity {

    private LocalDate dateConsultation;
    private StatutConsultation statut;
    private String observationMedecin;

    private DossierMedical dossierMedical;
    private Medecin medecin;

    private List<InterventionMedecin> interventions = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Consultation)) return false;
        Consultation that = (Consultation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Consultation {
                id = %d,
                date = %s,
                statut = %s,
                interventionsCount = %d
            }
            """.formatted(id, dateConsultation, statut,
                interventions == null ? 0 : interventions.size());
    }
}
