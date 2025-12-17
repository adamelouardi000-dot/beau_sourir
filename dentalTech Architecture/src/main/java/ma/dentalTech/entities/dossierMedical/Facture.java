package ma.dentalTech.entities.dossierMedical;

import java.time.LocalDateTime;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutFacture;

/**
 * Entité représentant une facture.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facture extends BaseEntity {

    private Double totaleFacture;
    private Double totalePaye;
    private Double reste;
    private StatutFacture statut;
    private LocalDateTime dateFacture;

    private SituationFinanciere situationFinanciere;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Facture)) return false;
        Facture that = (Facture) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void calculerReste() {
        if (totaleFacture != null && totalePaye != null) {
            this.reste = totaleFacture - totalePaye;
        }
    }

    @Override
    public String toString() {
        return """
            Facture {
                id = %d,
                totaleFacture = %.2f,
                totalePaye = %.2f,
                reste = %.2f,
                statut = %s,
                dateFacture = %s
            }
            """.formatted(id,
                totaleFacture != null ? totaleFacture : 0.0,
                totalePaye != null ? totalePaye : 0.0,
                reste != null ? reste : 0.0,
                statut, dateFacture);
    }
}
