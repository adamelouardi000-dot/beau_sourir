package ma.dentalTech.entities.cabinet;

import java.time.LocalDateTime;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;

/**
 * Entité représentant les revenus du cabinet.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Revenues extends BaseEntity {

    private String titre;
    private String description;
    private Double montant;
    private LocalDateTime date;

    private CabinetMedical cabinet;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Revenues)) return false;
        Revenues that = (Revenues) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Revenues {
                id = %d,
                titre = '%s',
                montant = %.2f,
                date = %s
            }
            """.formatted(id, titre, montant, date);
    }
}
