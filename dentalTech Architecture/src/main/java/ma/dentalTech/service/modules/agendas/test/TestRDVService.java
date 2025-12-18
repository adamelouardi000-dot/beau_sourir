package ma.dentalTech.service.modules.agendas.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.StatutRDV;
import ma.dentalTech.service.modules.agendas.api.RDVService;

import java.time.LocalDate;
import java.time.LocalTime;

public class TestRDVService {

    public static void main(String[] args) {
        RDVService service = ApplicationContext.getBean(RDVService.class);

        try {
            RDV r = RDV.builder()
                    .date(LocalDate.now().plusDays(1))
                    .heure(LocalTime.of(10, 0))
                    .motif("Controle")
                    .statut(StatutRDV.values()[0]) // évite de deviner le nom exact
                    .build();

            service.create(r);
            System.out.println("✅ RDV créé id=" + r.getId());

            r.setMotif("Controle modifié");
            service.update(r);
            System.out.println("✅ RDV modifié");

            service.deleteById(r.getId());
            System.out.println("✅ RDV supprimé");
        } finally {
            SessionFactory.getInstance().closeConnection();
        }
    }
}
