package ma.dentalTech.entities.patient;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Antecedent extends BaseEntity {

    private String nom;
    private CategorieAntecedent categorie;
    private NiveauRisque niveauRisque;

    private List<Patient> patients = new ArrayList<>();

    @Override
    public String toString() {
        return """
        Antecedent {
          id = %d,
          nom = '%s',
          categorie = %s,
          niveauRisque = %s,
          patientsCount = %d
        }
        """.formatted(
                id,
                nom,
                categorie,
                niveauRisque,
                patients == null ? 0 : patients.size()
        );
    }
}

