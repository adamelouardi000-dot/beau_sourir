package ma.dentalTech.entities.dossierMedical;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;

/**
 * Entité représentant une prescription de médicament dans une ordonnance.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription extends BaseEntity {

    private Integer quantite;
    private String frequence;
    private Integer dureeEnJours;

    private Ordonnance ordonnance;
    private Medicament medicament;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prescription)) return false;
        Prescription that = (Prescription) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Prescription {
                id = %d,
                medicament = %s
                quantite = %d,
                frequence = '%s',
                dureeEnJours = %d
            }
            """.formatted(id, medicament.getNom(), quantite, frequence, dureeEnJours);
    }
}
