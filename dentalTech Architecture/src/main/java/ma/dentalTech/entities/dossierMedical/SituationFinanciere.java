package ma.dentalTech.entities.dossierMedical;

import java.util.ArrayList;
import java.util.List;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;

/**
 * Entité représentant la situation financière d'un patient.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SituationFinanciere extends BaseEntity {

    private Double totaleDesActes;
    private Double totalePaye;
    private Double credit;
    private StatutSituationFinanciere statut;
    private boolean enPromo;

    private DossierMedical dossierMedical;

    private List<Facture> factures = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SituationFinanciere)) return false;
        SituationFinanciere that = (SituationFinanciere) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Double getReste() {
        if (totaleDesActes == null || totalePaye == null) return 0.0;
        return totaleDesActes - totalePaye;
    }

    @Override
    public String toString() {
        return """
            SituationFinanciere {
                id = %d,
                totaleDesActes = %.2f,
                totalePaye = %.2f,
                reste = %.2f,
                statut = %s
            }
            """.formatted(id,
                totaleDesActes != null ? totaleDesActes : 0.0,
                totalePaye != null ? totalePaye : 0.0,
                getReste(),
                statut);
    }
}
