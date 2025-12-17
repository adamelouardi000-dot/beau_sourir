package ma.dentalTech.entities.dossierMedical;

import java.util.ArrayList;
import java.util.List;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.FormeMedicament;

/**
 * Entité représentant un médicament.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicament extends BaseEntity {

    private String nom;
    private String laboratoire;
    private String type;
    private FormeMedicament forme;
    private boolean remboursable;
    private Double prixUnitaire;
    private String description;

    List<Prescription> prescriptions = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicament)) return false;
        Medicament that = (Medicament) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Medicament {
                id = %d,
                nom = '%s',
                laboratoire = '%s',
                forme = %s,
                prixUnitaire = %.2f, 
                prescriptionCount = '%d',
            }
            """.formatted(id, nom, laboratoire, forme,
                prixUnitaire != null ? prixUnitaire : 0.0, prescriptions == null ? 0 : prescriptions.size());
    }
}
