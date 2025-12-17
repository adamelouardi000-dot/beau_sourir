package ma.dentalTech.entities.cabinet;

import java.time.LocalDate;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.CategorieStatistique;

/**
 * Entité représentant les statistiques du cabinet.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statistiques extends BaseEntity {

    private String nom;
    private CategorieStatistique categorie;
    private Double chiffre;
    private LocalDate dateCalcul;

    private CabinetMedical cabinet;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Statistiques)) return false;
        Statistiques that = (Statistiques) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Statistiques {
                id = %d,
                nom = '%s',
                categorie = %s,
                chiffre = %.2f,
                dateCalcul = %s
            }
            """.formatted(id, nom, categorie, chiffre, dateCalcul);
    }
}
