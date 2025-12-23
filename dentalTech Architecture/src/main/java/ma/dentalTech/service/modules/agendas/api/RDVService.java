package ma.dentalTech.service.modules.agendas.api;

import ma.dentalTech.mvc.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface RDVService {

    List<RdvDto> findAll();
    RdvDto findById(Long id);
    RdvDto create(RdvCreateRequest request);
    RdvDto update(RdvUpdateRequest request);
    void deleteById(Long id);

    List<RdvDto> findByMedecinAndDate(Long medecinId, LocalDate date);
    List<RdvDto> findByMedecinAndDateRange(Long medecinId, LocalDate start, LocalDate end);
    List<RdvDto> findByPatient(Long patientId);
    List<RdvDto> findByAgenda(Long agendaId);
}
