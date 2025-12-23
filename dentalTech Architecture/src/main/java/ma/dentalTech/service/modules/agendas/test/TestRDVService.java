package ma.dentalTech.service.modules.agendas.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.StatutRDV;
import ma.dentalTech.mvc.dto.RdvCreateRequest;
import ma.dentalTech.mvc.dto.RdvDto;
import ma.dentalTech.mvc.dto.RdvUpdateRequest;
import ma.dentalTech.service.modules.agendas.api.RDVService;

import java.time.LocalDate;
import java.time.LocalTime;

public class TestRDVService {

    public static void main(String[] args) {
        RDVService service = ApplicationContext.getBean(RDVService.class);

        // ⚠️ Mets ici des IDs EXISTANTS dans ta base (agenda/patient/medecin)
        Long agendaId  = 1L;
        Long patientId = 1L;
        Long medecinId = 1L;

        try {
            // =========================
            // 1) CREATE (DTO)
            // =========================
            RdvCreateRequest createReq = new RdvCreateRequest(
                    agendaId,
                    patientId,
                    medecinId,
                    null, // dossierId nullable
                    LocalDate.now().plusDays(1),
                    LocalTime.of(10, 0),
                    "Controle",
                    StatutRDV.values()[0], // évite deviner le nom exact
                    null  // noteMedecin nullable
            );

            RdvDto created = service.create(createReq);
            System.out.println("✅ RDV créé id=" + created.id());

            // =========================
            // 2) UPDATE (DTO)
            // =========================
            RdvUpdateRequest updateReq = new RdvUpdateRequest(
                    created.id(),
                    agendaId,
                    patientId,
                    medecinId,
                    created.dossierId(), // garde ce que tu as
                    created.date(),
                    created.heure(),
                    "Controle modifié",
                    created.statut(),
                    "RAS"
            );

            RdvDto updated = service.update(updateReq);
            System.out.println("✅ RDV modifié motif=" + updated.motif());

            // =========================
            // 3) DELETE
            // =========================
            service.deleteById(updated.id());
            System.out.println("✅ RDV supprimé id=" + updated.id());

        } finally {
            SessionFactory.getInstance().closeConnection();
        }
    }
}
