package ma.dentalTech.service.modules.agendas.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.service.modules.agendas.api.AgendaMensuelService;
import ma.dentalTech.mvc.dto.AgendaMensuelCreateRequest;
import ma.dentalTech.mvc.dto.AgendaMensuelSearchRequest;
import ma.dentalTech.entities.enums.Mois;

import java.time.LocalDate;

public class TestAgendaMensuelService {

    public static void main(String[] args) {
        AgendaMensuelService service = ApplicationContext.getBean(AgendaMensuelService.class);

        // ✅ Test mapping/validation DTO (sans DB)
        var req = new AgendaMensuelCreateRequest(1L, Mois.JANVIER, LocalDate.now().getYear());
        System.out.println("CreateRequest OK: " + req);

        var search = new AgendaMensuelSearchRequest(1L, Mois.JANVIER, LocalDate.now().getYear());
        System.out.println("SearchRequest OK: " + search);

        // ⚠️ Insert réel dépend de la BD/FK (on active plus tard)
        System.out.println("✅ DTO agendas prêts.");
    }
}
