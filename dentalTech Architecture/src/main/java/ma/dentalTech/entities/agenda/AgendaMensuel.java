package ma.dentalTech.entities.agenda;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import ma.dentalTech.entities.enums.Mois;

/**
 * Entité représentant l'agenda mensuel d'un médecin.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaMensuel {

    private Long id;
    private Mois mois;
    private Integer annee;
    private List<LocalDate> joursNonDisponibles = new ArrayList<>();

    private Long medecinId;

    private List<RDV> rendezVous = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgendaMensuel)) return false;
        AgendaMensuel that = (AgendaMensuel) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public boolean isJourDisponible(LocalDate date) {
        return joursNonDisponibles == null || !joursNonDisponibles.contains(date);
    }

    @Override
    public String toString() {
        return """
            AgendaMensuel {
                id = %d,
                mois = %s,
                annee = %d,
                joursNonDisponiblesCount = %d,
                rdvCount = %d
            }
            """.formatted(id, mois, annee,
                joursNonDisponibles == null ? 0 : joursNonDisponibles.size(),
                rendezVous == null ? 0 : rendezVous.size());
    }
}
