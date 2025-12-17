package ma.dentalTech.entities.agenda;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.enums.StatutRDV;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.users.Medecin;

/**
 * Entité représentant un rendez-vous.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RDV  extends BaseEntity {

    private LocalDate date;
    private LocalTime heure;
    private String motif;
    private StatutRDV statut;
    private String noteMedecin;

    private Long agendaMensuelId;

    private Patient patient;
    private Medecin medecin;
    private DossierMedical dossierMedical;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RDV)) return false;
        RDV that = (RDV) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            RDV {
                id = %d,
                date = %s,
                heure = %s,
                motif = '%s',
                statut = %s
            }
            """.formatted(id, date, heure, motif, statut);
    }
}
