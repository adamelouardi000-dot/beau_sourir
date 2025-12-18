package ma.dentalTech.service.modules.agendas.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.service.modules.agendas.api.AgendaMensuelService;

import java.time.LocalDate;

public class TestAgendaMensuelService {

    public static void main(String[] args) {
        AgendaMensuelService service = ApplicationContext.getBean(AgendaMensuelService.class);

        try {
            AgendaMensuel a = AgendaMensuel.builder()
                    .medecinId(1L)
                    .mois(Mois.JANVIER)
                    .annee(LocalDate.now().getYear())
                    .build();

            service.create(a);
            System.out.println("✅ agenda créé id=" + a.getId());

            service.addJourNonDisponible(a.getId(), LocalDate.now().plusDays(2));
            System.out.println("✅ jour ajouté");

            System.out.println("joursNonDispo=" + service.getJoursNonDisponibles(a.getId()).size());

            service.clearJoursNonDisponibles(a.getId());
            System.out.println("✅ clear jours");

            service.deleteById(a.getId());
            System.out.println("✅ agenda supprimé");
        } finally {
            SessionFactory.getInstance().closeConnection();
        }
    }
}
