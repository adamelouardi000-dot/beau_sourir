package ma.dentalTech.entities.patient;

import java.time.LocalDate;
import java.util.List;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Patient extends BaseEntity {

    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;
    private String email;
    private LocalDate dateNaissance;
    private Sexe sexe;
    private Assurance assurance;

    private List<Antecedent> antecedents = null;

    @Override
    public String toString() {
        return """
        Patient {
          id = %d,
          nom = '%s',
          prenom = '%s',
          adresse = '%s',
          telephone = '%s',
          email = '%s',
          dateNaissance = %s,
          dateCreation = %s,
          sexe = %s,
          assurance = %s,
          antecedentsCount = %d
        }
        """.formatted(
                id,
                nom,
                prenom,
                adresse,
                telephone,
                email,
                dateNaissance,
                dateCreation,
                sexe,
                assurance,
                antecedents == null ? 0 : antecedents.size()
        );
    }
}
