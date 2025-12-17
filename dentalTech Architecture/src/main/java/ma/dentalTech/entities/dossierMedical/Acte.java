package ma.dentalTech.entities.dossierMedical;

import java.util.ArrayList;
import java.util.List;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;

/**
 * Entité représentant un acte médical dentaire.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Acte  extends BaseEntity {

    private String libelle;
    private String categorie;
    private Double prixBase;

    List<InterventionMedecin> interventions = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Acte)) return false;
        Acte that = (Acte) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Acte {
                id = %d,
                libelle = '%s',
                categorie = '%s',
                prixBase = %.2f,
                interventionsCount = %d
            }
            """.formatted(id, libelle, categorie, prixBase != null ? prixBase : 0.0, interventions == null ? 0 : interventions.size());
    }
}
