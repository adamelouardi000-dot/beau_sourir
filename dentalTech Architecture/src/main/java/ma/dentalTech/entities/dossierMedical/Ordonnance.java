package ma.dentalTech.entities.dossierMedical;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.users.Medecin;

/**
 * Entité représentant une ordonnance médicale.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ordonnance extends BaseEntity {


    private LocalDate date;
    private DossierMedical dossierMedical;
    private Medecin medecin;

    private List<Prescription> prescriptions = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ordonnance)) return false;
        Ordonnance that = (Ordonnance) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Ordonnance {
                id = %d,
                date = %s,
                prescriptionsCount = %d
            }
            """.formatted(id, date,
                prescriptions == null ? 0 : prescriptions.size());
    }
}
