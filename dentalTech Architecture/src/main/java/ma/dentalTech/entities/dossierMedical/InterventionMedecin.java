package ma.dentalTech.entities.dossierMedical;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;

/**
 * Entité représentant une intervention médicale lors d'une consultation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterventionMedecin extends BaseEntity {

    private Long id;
    private Double prixPatient;
    private Integer numDent;

    private Long consultationId;
    private Long acteId;

    private Acte acte;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterventionMedecin)) return false;
        InterventionMedecin that = (InterventionMedecin) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            InterventionMedecin {
                id = %d,
                prixPatient = %.2f,
                numDent = %d,
                acteId = %d
            }
            """.formatted(id, prixPatient != null ? prixPatient : 0.0,
                numDent != null ? numDent : 0, acteId);
    }
}
