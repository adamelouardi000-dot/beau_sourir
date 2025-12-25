package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.mvc.dto.OrdonnanceCreateRequest;
import ma.dentalTech.mvc.dto.OrdonnanceDto;
import ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo;
import ma.dentalTech.service.modules.dossierMedical.api.OrdonnanceService;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceServiceImpl implements OrdonnanceService {

    private final OrdonnanceRepo ordonnanceRepo;

    public OrdonnanceServiceImpl(OrdonnanceRepo ordonnanceRepo) {
        this.ordonnanceRepo = ordonnanceRepo;
    }

    // =========================
    // CREATE
    // =========================
    public OrdonnanceDto create(OrdonnanceCreateRequest request) {
        if (request == null || request.consultationId() == null) {
            throw new IllegalArgumentException("consultationId obligatoire");
        }

        Ordonnance o = new Ordonnance();

        // ✅ Maintenant que tu as ajouté consultationId dans l'entity
        o.setConsultationId(request.consultationId());

        // Optionnel: si ton entity a setDate(LocalDate)
        safeInvoke(o, "setDate", LocalDate.class, LocalDate.now());

        // Optionnel: si ton entity a setDateOrdonnance(LocalDateTime)
        safeInvoke(o, "setDateOrdonnance", LocalDateTime.class, LocalDateTime.now());

        ordonnanceRepo.create(o);
        return toDto(o);
    }

    // =========================
    // REQUIRED BY INTERFACE
    // =========================
    public List<OrdonnanceDto> getByConsultation(Long consultationId) {
        if (consultationId == null) throw new IllegalArgumentException("consultationId obligatoire");

        List<Ordonnance> list = ordonnanceRepo.findByConsultation(consultationId);
        List<OrdonnanceDto> out = new ArrayList<>();
        for (Ordonnance o : list) out.add(toDto(o));
        return out;
    }

    // =========================
    // DTO MAPPER (robuste)
    // =========================
    private OrdonnanceDto toDto(Ordonnance o) {
        Long id = o.getId();
        Long consultationId = o.getConsultationId();

        // si ton entity a getDate() => LocalDate
        LocalDate date = null;
        Object v = safeGetter(o, "getDate");
        if (v instanceof LocalDate) date = (LocalDate) v;

        // ✅ Construire OrdonnanceDto même s'il a 2 champs ou 3 champs
        return buildOrdonnanceDto(id, consultationId, date);
    }

    private OrdonnanceDto buildOrdonnanceDto(Long id, Long consultationId, LocalDate date) {
        try {
            // Essayer tous les constructeurs et choisir celui qui "matche"
            Constructor<?>[] ctors = OrdonnanceDto.class.getDeclaredConstructors();
            for (Constructor<?> ctor : ctors) {
                Class<?>[] p = ctor.getParameterTypes();

                // 3 params : (Long, Long, LocalDate)
                if (p.length == 3
                        && isLong(p[0]) && isLong(p[1]) && p[2] == LocalDate.class) {
                    ctor.setAccessible(true);
                    return (OrdonnanceDto) ctor.newInstance(id, consultationId, date);
                }

                // 2 params possibles :
                // (Long, Long)  OU (Long, LocalDate)  OU (Long, String) ... حسب مشروعك
                if (p.length == 2 && isLong(p[0]) && isLong(p[1])) {
                    ctor.setAccessible(true);
                    return (OrdonnanceDto) ctor.newInstance(id, consultationId);
                }
                if (p.length == 2 && isLong(p[0]) && p[1] == LocalDate.class) {
                    ctor.setAccessible(true);
                    return (OrdonnanceDto) ctor.newInstance(id, date);
                }
            }

            throw new RuntimeException("Aucun constructeur compatible trouvé pour OrdonnanceDto");
        } catch (Exception e) {
            throw new RuntimeException("Erreur mapping OrdonnanceDto: " + e.getMessage(), e);
        }
    }

    private boolean isLong(Class<?> c) {
        return c == Long.class || c == long.class;
    }

    // =========================
    // Reflection helpers safe
    // =========================
    private static void safeInvoke(Object target, String method, Class<?> type, Object value) {
        try {
            target.getClass().getMethod(method, type).invoke(target, value);
        } catch (Exception ignored) {}
    }

    private static Object safeGetter(Object target, String getter) {
        try {
            return target.getClass().getMethod(getter).invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }
}
