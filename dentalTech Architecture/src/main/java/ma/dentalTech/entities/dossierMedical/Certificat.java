package ma.dentalTech.entities.dossierMedical;

import java.time.LocalDate;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.users.Medecin;

/**
 * Entité représentant un certificat médical.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificat  extends BaseEntity {

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer duree;
    private String noteMedecin;

    private DossierMedical dossierMedical;
    private Medecin medecin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Certificat)) return false;
        Certificat that = (Certificat) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Certificat {
                id = %d,
                dateDebut = %s,
                dateFin = %s,
                duree = %d jours
            }
            """.formatted(id, dateDebut, dateFin, duree);
    }
}
