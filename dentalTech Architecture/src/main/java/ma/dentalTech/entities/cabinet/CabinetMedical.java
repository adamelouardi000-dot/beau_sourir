package ma.dentalTech.entities.cabinet;

import java.util.ArrayList;
import java.util.List;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.users.Staff;

/**
 * Entité représentant le cabinet médical dentaire.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CabinetMedical extends BaseEntity {

    private String nom;
    private String email;
    private String logo;
    private String adresse;
    private String cin;
    private String tel1;
    private String tel2;
    private String siteWeb;
    private String instagram;
    private String facebook;
    private String description;

    private List<Staff> staff = new ArrayList<>();
    private List<Charges> charges = new ArrayList<>();
    private List<Revenues> revenues = new ArrayList<>();
    private List<Statistiques> statistiques = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CabinetMedical)) return false;
        CabinetMedical that = (CabinetMedical) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            CabinetMedical {
                id = %d,
                nom = '%s',
                email = '%s',
                adresse = '%s',
                tel1 = '%s'
            }
            """.formatted(id, nom, email, adresse, tel1);
    }
}
